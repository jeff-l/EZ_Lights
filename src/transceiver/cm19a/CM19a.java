package transceiver.cm19a;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.usb.UsbConfiguration;
import javax.usb.UsbConst;
import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;
import javax.usb.UsbPort;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;
import javax.usb.util.UsbUtil;

import transceiver.Transceiver;


/**
 * User space driver for the CM19a X10 RF transceiver
 * @author Jan Roberts
 *
 */

public class CM19a implements transceiver.Transceiver
{

		public Thread rt = null;
		public Thread tt = null;
		public UsbInterface usbInterface = null;
		public Transmitter transmitter = null;
		public Receiver receiver = null;
		public Properties protocol = null;
		public Hashtable cache = new Hashtable();
		public final int wMaxPacketSize = 8;
		public final String PROTOCOL_FILE = "cm19a.protocol";
		public boolean debug = false;
		public final String PROPERTIES_FILE = "cm19a.properties"; // TODO: change this
		public final String debugKey = "cm19a.debug";
		public LinkedList outQueue = null;


		public CM19a() {
				outQueue = new LinkedList();
		}

		/**
		 * Load the file that holds the CM19a protocol. Splits each input line
		 * and stores it. Further processing is done when the command is
		 * used for the first time.
		 *
		 * @author Jan Roberts
		 */
		public void loadProtocol() throws IOException, CM19aException {
				protocol = new Properties();
				InputStream file = ClassLoader.getSystemResourceAsStream(PROTOCOL_FILE);
				if ( file == null ) 
						throw new CM19aException( "Protocol file " + PROTOCOL_FILE + " not found");
				BufferedReader in = new BufferedReader( new InputStreamReader( file ));
				String line;
				while ( (line = in.readLine()) != null ) {
						String [] tokens = line.split("\\s+", 2 );
						protocol.setProperty( tokens[0], tokens[1] );
				}
				
		}


		/**
		 * Load the CM19a properties file
		 *
		 * @author Jan Roberts
		 */

		public void loadProperties() {
				Properties prop = new Properties();
				InputStream file = ClassLoader.getSystemResourceAsStream(PROPERTIES_FILE);
				if ( file != null ) {
						try {
								prop.load( file );
						} catch ( IOException e ) {
								System.out.println( "Problem loading properties file. " +
																		"Using default values. Error is: ");
								System.out.println( e.getMessage() );
						}
						String debugOn = prop.getProperty( debugKey, "false" );
						if ( debugOn.equals( "true" ) ) debug = true;
				}
					
		}	


		/**
		 * Find the CM19a device. Uses the isDevice() method to find out
		 * whether the device has been found.
		 *
		 * @param UsbHub A USB hub. Initially made with the Virtual Root USB hub.
		 * @return The CM19a device or null if it wasn't found
		 * @author Jan Roberts
		 *
		 */
		public UsbDevice findDevice(UsbHub hub) {

				UsbDevice cm19a = null;
				Iterator iterator = hub.getUsbPorts().iterator();
				while ( iterator.hasNext() ) {
						UsbPort port = (UsbPort)iterator.next();
						if ( debug ) System.out.println("Port found ");

						if ( port.isUsbDeviceAttached() ) {
								UsbDevice device = port.getUsbDevice();
								/* Is this the device? If so, quit looking */
								if ( isDevice(device) ) {
										cm19a = device;
										break;
										/* If this is a hub, recurse; if the CM19a is found,
											 return it */
								} else if ( device.isUsbHub() ) {
										cm19a = findDevice( (UsbHub)device );
										if ( isDevice( cm19a ) ) break;
										
								}
						}
				}

				return cm19a;
		}


		/** 
		 * Test for whether or not the device being examined is the CM19a.
		 * Assumes that the vendor id and the product id will uniquely
		 * identify it.
		 *
		 * @param device A UsbDevice
		 * @return true if device is the CM19a, false otherwise
		 * @author Jan Roberts
		 */
		public boolean isDevice( UsbDevice device ) {

				final short vendorId = (short)0x0bc7; 
				final short productId = (short)0x0002; 

				boolean isDevice = false;
				if ( device == null ) return false;

				if ( device.getUsbDeviceDescriptor().idVendor() == vendorId &&
						 device.getUsbDeviceDescriptor().idProduct() == productId ) {
						isDevice = true;;
				} else {

						if ( debug ) {
								System.out.println("Looking for " + 
																	 UsbUtil.toHexString(vendorId) + 
																	 " and " + UsbUtil.toHexString(productId) );
								System.out.println("Vendor id: " + 
																	 UsbUtil.toHexString(device.getUsbDeviceDescriptor().idVendor()) +
																	 " Product id: " +
																	 UsbUtil.toHexString(device.getUsbDeviceDescriptor().idProduct()) ); 
						}
						
				}
				return isDevice; 
		}

										
		/**
		 * Starts the user space driver. The CM19a has two endpoints, 
		 * one for communication from the host to the CM19a and the
		 * other for communication from the CM19a to the host. 
		 *
		 * @author Jan Roberts
		 */
		public void startDriver() throws CM19aException, UsbException, UnsupportedEncodingException
		{

				UsbDevice cm19a  = null;
				UsbEndpoint inEndpoint = null;
				UsbEndpoint outEndpoint = null;
				UsbPipe inPipe = null;
				UsbPipe outPipe = null;

				loadProperties();

				UsbHub virtualRootUsbHub = 
						UsbHostManager.getUsbServices().getRootUsbHub();

				if (virtualRootUsbHub == null) {
						throw new CM19aException("No virtual root usb hub found");
				}

				/* Find the attached CM19a */
				cm19a = findDevice(virtualRootUsbHub);

				if (cm19a == null ) {
						throw new CM19aException("No CM19a found; /proc/bus/usb permissions problem?");
				} else {

						if ( debug ) {
								System.out.println("Device found " );
								System.out.println( "Is configured is " + 
																		cm19a.isConfigured() );
						}

						UsbConfiguration config = cm19a.getActiveUsbConfiguration();
						if ( config == null ) {
								throw new CM19aException("No active configuration found");
						}

						/* Get the first interface, there should only be one */
						usbInterface = (UsbInterface)config.getUsbInterfaces().get(0);
						
						if ( debug ) {
								System.out.println("Got device; Manufacturer is " +
																	 cm19a.getManufacturerString() );
								System.out.println("Got interface; Active is " + 
																	 usbInterface.isActive() + 
																	 " Claimed is " + usbInterface.isClaimed() );
						} 

						try {
								usbInterface.claim();
								if ( debug ) System.out.println("Interface has been claimed");
						} catch ( UsbException ue ) {
								throw new CM19aException( "Couldn't claim interface. " +
																					"Message is " + ue.getMessage() );
						}
						
						List usbEndpoints = usbInterface.getUsbEndpoints();


						for (int i=0; i<usbEndpoints.size(); i++ ) {
								UsbEndpoint endpoint = (UsbEndpoint)usbEndpoints.get(i);

								if (UsbConst.ENDPOINT_DIRECTION_IN == endpoint.getDirection()) {
										inEndpoint = endpoint;
										if ( debug ) System.out.println("Got in endpoint");
								} else if (UsbConst.ENDPOINT_DIRECTION_OUT == 
													 endpoint.getDirection()) {
										outEndpoint = endpoint;
										if ( debug ) System.out.println("Got out endpoint");
								}
						}

						if (inEndpoint == null || outEndpoint == null) {
								throw new CM19aException ( "Couldn't find both endpoints" );
						}

						inPipe = inEndpoint.getUsbPipe();

						try {
								inPipe.open();
						} catch ( NullPointerException e ) {
								try { 
										usbInterface.release();
										throw new CM19aException("inPipe is null; interface released");
								} catch ( UsbException ue ) {
										throw new CM19aException("inPipe is null; interface not released "
																						 + ue.getMessage());
								}
						} catch ( UsbException ue2 ) {
								try { 
										usbInterface.release();
										throw new CM19aException("Usb exception on inPipe " +
																						 ue2.getMessage() + 
																						 "; interface released");

								} catch ( UsbException ue ) {
										throw new CM19aException("Usb exception on inPipe " +
																						 ue2.getMessage() + 
																						 "; interface not released; Exception " +
																						 ue.getMessage());

								}
						} //Opened the inPipe successfully


						/** 
						 * The receiver attaches to the inPipe and 
						 * gets all device to host messages 
						 */
						receiver = new Receiver( inPipe );
						rt = new Thread( receiver );
						rt.start();


						//Now open the outbound pipe
						outPipe = outEndpoint.getUsbPipe();

						try {
								outPipe.open();
						} catch ( NullPointerException e ) {
								try { 
										inPipe.close();
										usbInterface.release();
										throw new CM19aException("outPipe is null; interface released");
								} catch ( UsbException ue ) {
										throw new CM19aException("outPipe is null; interface not released "
																						 + ue.getMessage() );

								}
						} catch ( UsbException ue2 ) {
								try { 
										inPipe.close();
										usbInterface.release();
										throw new CM19aException ("Usb exception on outPipe " 
																							+ ue2.getMessage()
																							+ "; interface released");

								} catch ( UsbException ue ) {
										throw new CM19aException ("Usb exception on outPipe " 
																							+ ue2.getMessage()
																							+ "; interface not released "
																							+ ue.getMessage());
								}
						} //outPipe successfully opened

						/**
						 * The transmitter attaches to the outPipe and
						 * controls the CM19a by sending signals to it
						 */
						transmitter = new Transmitter( outPipe );
						tt = new Thread( transmitter );
						tt.start();

				}
		}

		/** 
		 * May be needed to initialize the CM19a; The command was sniffed
		 * from a Win98 driver, but doesn't appear to be needed; the CM19a
		 * has always responded to the protocol commands. The method was
		 * left here to be used as a last resort.
		 *
		 * @author Jan Roberts
		 */
		public void init( ) throws UsbException {

				transmitter.init();
		}

		/**
		 * Formats the incoming command to be consistent with what is
		 * expected, which is a single uppercased word, e.g. A1OFF.
		 * Commands can then be entered as reader friendly way, such as
		 * a1 off, A1 Off, etc.
		 *
		 * @param command the incoming x10 command
		 * @return the correctly formatted string
		 * @throws CM19aException If the incoming string is blank or empty
		 * @author Jan Roberts
		 *
		 */

		public String formatCommand( String command ) throws CM19aException {

				/*remove any spaces in the command*/
				String [] words = command.split("\\s+");
				if ( words.length == 0 ) 
						throw new CM19aException("Command is blank or empty");
				String spacelessCommand = words[0].trim();

				/*scrunch the string back together without the spaces*/
				if ( words.length > 1 ) {
						for (int i=1; i<words.length; i++) {
								spacelessCommand = spacelessCommand.concat(words[i].trim());

						}
				}

				return spacelessCommand.toUpperCase();
		}

		/**
		 * Sends the incoming command to the CM19a. If necessary, 
		 * first load in the protocol.
		 *
		 * @param command A properly formatted x10 command
		 * @author Jan Roberts
		 * 
		 */
		public void send ( String command ) 
				throws CM19aException, FileNotFoundException, IOException
		{
				if ( protocol == null ) {
						loadProtocol();
				}
				/* Retrieve the actual bytes to transmit */

				byte [] rfTransmittal = getRFTransmittal( command );
				send( rfTransmittal );
		}

		/**
		 * Retrieves the actual bytes to transmit to the CM19a.
		 * First looks in the cache, then the raw protocol
		 *
		 * @param command A properly formatted x10 command
		 * @return The byte array to be transmitted or null if not found
		 * @author Jan Roberts
		 *
		 */

		public byte[] getRFTransmittal( String command ) throws CM19aException {
			
				byte [] rfTransmittal = null;
				rfTransmittal = findInCache( command );
				if ( rfTransmittal == null ) {
						rfTransmittal = findInProtocol( command );
						if ( rfTransmittal != null )
								addToCache( command, rfTransmittal );
				}
				return rfTransmittal;
		}	

		/**
		 * Retrieves the bytes to transmit to the CM19a. They're
		 * originally stored in a single string which has to be broken
		 * up and converted to a byte array.
		 *
		 * @param command A properly formatted x10 command
		 * @return The byte array to be transmitted
		 * @throws CM19aException If the command was not found in the 
		 *                        property table
		 * @author Jan Roberts
		 *
		 */
		public byte [] findInProtocol ( String command ) throws CM19aException {
				String rfTransmittalAsString = protocol.getProperty( command );
				if ( rfTransmittalAsString == null ) 
						throw new CM19aException("Error: " + command + 
																		 " not part of the recognized protocol");
				String [] s = rfTransmittalAsString.split("\\s+");

				byte [] rfTransmittal = new byte[s.length];
				for ( int i=0; i<s.length; i++ ) {
						rfTransmittal[i] = (byte)Integer.decode(s[i]).intValue();
				}

				return rfTransmittal;
		}
			
		/**
		 * Stores the command and the decoded bytes to a cache
		 *
		 * @param command An x10 command
		 * @param rfTransmittal The bytes to be sent to the CM19a
		 * @author Jan Roberts
		 *
		 */

		public void addToCache( String command, byte [] rfTransmittal ) {
				ByteBuffer buffer = ByteBuffer.allocate( rfTransmittal.length );
				buffer.put( rfTransmittal );
				cache.put( command, buffer );
		}
		
		/**
		 * Retrieves the bytes to be sent to the CM19a from the cache
		 *
		 * @param command An X10 command
		 * @return The bytes to be sent to the CM19a or null if the 
		 *         command has not yet been cached
		 * @author Jan Roberts
		 *
		 */

		public byte[] findInCache( String command ) {

				ByteBuffer buffer = (ByteBuffer)cache.get( command );
				if ( buffer == null ) {
						return null;
				} else {
						return buffer.array();
				}
		}

		/**
		 * Notify the transmitter that there is a message to be sent
		 * 
		 * @param rfTransmittal The bytes to be sent to the CM19a
		 * @author Jan Roberts
		 *
		 */
		public void send( byte[] rfTransmittal ) {
				synchronized ( outQueue ) {
						outQueue.add( rfTransmittal );
						outQueue.notify();
				}
		}

		/**
		 * Stops running threads and releases the CM19a
		 *
		 * @throws CM19aException If there was a problem releasing the CM19a
		 * @author Jan Roberts
		 */
		public void releaseDevice() throws CM19aException, UsbException {
				
				/* 
				 * Stop the threads and notify them so they'll know they've
				 * been stopped.
				 */
				
				receiver.quit();
				transmitter.quit();
				outQueue.notify();

				/* Release the interface*/
				try {
						usbInterface.release();
				} catch ( UsbException ue ) {
						throw new CM19aException("Failed to release device: " 
																		 + ue.getMessage());
				}

		}

		/**
		 * This class implements the UsbPipeListener interface by
		 * simply printing out all data that is transferred between 
		 * the CM19a and the host as hex bytes
		 *
		 * @author Jan Roberts
		 *
		 */
		class PipeListener implements UsbPipeListener {

				String direction = null;
				
				public PipeListener( String inOrOut ) {
						direction = inOrOut;
				}

				public void dataEventOccurred( UsbPipeDataEvent event ) {

						byte [] message = event.getData();
						int length = event.getActualLength();
						if ( message != null ) {
								/* Print out the message that the CM19a sent*/
								System.out.print(length + " bytes of data traveled from "
																 + direction + ": ");
								for (int i=0; i<length; i++)
										System.out.print(" 0x" + UsbUtil.toHexString(message[i]));
								System.out.println(""); 

						}

				}

				public void errorEventOccurred( UsbPipeErrorEvent event ) {
						UsbException exception = event.getUsbException();
						
						System.out.println( "Error traveled from " + direction +
																": " + exception.getMessage() );
						
				}
		}

		/*
		 * Receives data that travels from the CM19a device to the host
		 *
		 * @author Jan Roberts
		 *
		 */
		class Receiver implements Runnable {

				UsbPipe inPipe = null;
				UsbPipeListener inPipeListener = 
						new PipeListener("the CM19a device to the host");
				public boolean running = true;

				public Receiver(UsbPipe pipe) {
						inPipe = pipe;
						inPipe.addUsbPipeListener(inPipeListener);
				}

				public void run() {
						byte[] buffer = new byte[ wMaxPacketSize ];				
						int length = 0;

						while (running) {
								try {
										length = inPipe.syncSubmit( buffer );
								} catch ( UsbException uE ) {
										if (running) {
												System.out.println("Unable to submit data buffer to CM19a : " + uE.getMessage());
												break;
										}
								}
						
						}
				}
				/**
				 * Stop/abort listening for data events.
				 */
				public void quit() throws UsbException
				{
						running = false;
						inPipe.removeUsbPipeListener(inPipeListener);
						inPipe.abortAllSubmissions();
						inPipe.close();
				}
		}


		/**
		 * Transmits data from the host to the CM19a. This is the
		 * class that actually controls the X10 devices by having
		 * the CM19a send out the RF protocol for the users input
		 * command.
		 *
		 * @author Jan Roberts
		 *
		 */
		class Transmitter implements Runnable {
				
				UsbPipe outPipe = null;
				UsbPipeListener outPipeListener = 
						new PipeListener("the host to the CM19a device");
				boolean running = true;
				byte[] sendThis = null;

				public Transmitter ( UsbPipe pipe ) {
						outPipe = pipe;
						outPipe.addUsbPipeListener( outPipeListener );
				}


				/**
				 * Not currently used. These commands were sniffed from
				 * a Windows driver, but have never been needed to drive
				 * the CM19a on a Linux box. Don't know what they're for.
				 */
				public void init( ) throws UsbException {

						byte [] init1 = {(byte)0x80, (byte)0x01, (byte)0x00, 
														 (byte)0x20, (byte)0x14};
						byte [] init2 = {(byte)0x80, (byte)0x01, (byte)0x00, 
														 (byte)0x20, (byte)0x14, (byte)0x24, 
														 (byte)0x20, (byte)0x20};

						//enable write??				
						byte [] write_ok = {(byte)0x20, (byte)0x34, (byte)0xcb, 
																(byte)0x58, (byte)0xa7};

						outPipe.syncSubmit( write_ok );
						outPipe.syncSubmit( init1 );
						outPipe.syncSubmit( init2 );
						if ( debug ) System.out.println("Completed initialization");
						
				}

				public synchronized void run() {
						while ( running ) {

								/*Keep processing messages or sleep if there are none*/
								synchronized ( outQueue ) {
										if ( outQueue.size() == 0 )
												try {
														outQueue.wait();
												} catch ( InterruptedException e ) {
														//Do nothing
												}
								}

								synchronized ( outQueue ) {
										if ( outQueue.size() > 0 ) 
												sendThis = (byte [])outQueue.removeFirst();
								}

								if ( sendThis != null ) {
										try {
												outPipe.syncSubmit( sendThis );
												if (debug) System.out.println("RfTransmittal sent");
												sendThis = null;
										}	catch (UsbException ue) {
												System.out.println( ue );
										
										}
								}
						
						}

				}

				public synchronized void quit() throws UsbException {
						running = false;
						outPipe.abortAllSubmissions();
						outPipe.close();
				}

		}

		public static void main (String argv[])
		{
				String quit = "quit";
				String init = "init";
				String start = "start";
				
				CM19a transceiver = new CM19a();

				/*Start the driver*/
				try {
						transceiver.startDriver();
				} catch ( Exception e ) {
						System.out.println( "Problem starting driver: " + e.getMessage());
						System.exit(0);
				}
				System.out.println("Started driver. Accepting X10 commands now.");

				while (true) {
						try {
								InputStreamReader converter = 
										new InputStreamReader(System.in);
								BufferedReader in = new BufferedReader(converter);
								UsbDevice device = null;

								String command = in.readLine();

								if (command.equals( quit )) {
										if ( device != null )
												transceiver.releaseDevice();
										System.exit(0);
								}
								else if (command.equals( start )) {
										transceiver.startDriver();
										System.out.println("Started driver");
								}
										
								else if ( command.equals( init )) {
										transceiver.init( );
								}

								else {
										if ( command.trim().length() > 0 ) {
												System.out.println( "Sending " + command );
												String formattedCommand = 
														transceiver.formatCommand( command );
												transceiver.send( formattedCommand );
										}
								}
		    
						} catch (Exception e) {
								System.out.println( e.getMessage() );
						}
				}
		}

}
