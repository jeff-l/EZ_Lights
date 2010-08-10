package transceiver.cm19a;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

import org.apache.log4j.Logger;

import transceiver.TransceiverException;


/**
 * Interface for the cm19a transceiver from X10
 * (It could probably be used for other usb-based transceiver devices easily)
 *
 */

public class CM19a implements transceiver.Transceiver
{
	static Logger log = Logger.getLogger(CM19a.class);	

	private final String COMMAND_FILENAME = "cm19a_commands.conf"; 
	private final int maxInputPacketSize = 8;

	private HashMap<String, byte[]> commands = null;
	public LinkedList<byte[]> commandOutputQueue = null;

	public Thread receiverThread = null; 
	public Thread transmitterThread = null;
	public UsbInterface usbInterface = null;
	public Transmitter transmitter = null;
	public Receiver receiver = null; 


	public CM19a() {
		commandOutputQueue = new LinkedList<byte[]>();
	}

	/**
	 * Finds given command string in known command list and gets corresponding
	 * byte array (the real command that the transceiver understands). 
	 * Adds the byte array to the queue of commands waiting to be sent to 
	 * the transceiver.
	 * 
	 * @throws TransceiverException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void sendCommand ( String command )
	throws TransceiverException, FileNotFoundException, IOException
	{
		log.debug("Sending command: " + command);

		if ( commands == null ) { // lazy load the commands
			loadCommands();
		}

		byte [] commandBytes = commands.get(command);
		if (commandBytes == null) {
			log.error("Command not supported: " + command);
			throw new IllegalArgumentException("Command not supported: " + command);
		}

		synchronized ( commandOutputQueue ) { 
			commandOutputQueue.add( commandBytes );
			commandOutputQueue.notify();
		}		
	}	


	/**
	 * Locates the CM19a device by traversing the tree of usb devices.
	 *
	 * @param hub is the system's usb hub, the CM19a will be sought starting from this hub
	 * @return the CM19a UsbDevice or null
	 *
	 */
	public UsbDevice findCM19a(UsbHub hub) {

		Iterator<?> usbPorts = hub.getUsbPorts().iterator();

		while ( usbPorts.hasNext() ) { // returns from middle of loop if found
			UsbPort port = (UsbPort)usbPorts.next();
			log.debug("Found a usb port: " + String.valueOf(port.getPortNumber()));

			if ( port.isUsbDeviceAttached() ) {
				UsbDevice usbDevice = port.getUsbDevice();
				if ( isCM19a(usbDevice) ) {
					return usbDevice;

				} else if ( usbDevice.isUsbHub() ) { // This is a hub, so search it's devices
					log.debug("Found a usb hub: ");

					usbDevice = findCM19a( (UsbHub)usbDevice );
					if ( isCM19a( usbDevice ) ) 
						return usbDevice;
				}
			}
		}
		return null;
	}


	/** 
	 * Use the vendor and product IDs to determine if the device is a CM19a
	 *
	 * @param device A UsbDevice
	 * @return true if device is the CM19a, false otherwise
	 */
	public boolean isCM19a( UsbDevice device ) {

		final short vendorId = (short)0x0bc7; 
		final short productId = (short)0x0002; 

		if ( device == null ) 
			return false;

		return ( device.getUsbDeviceDescriptor().idVendor() == vendorId &&
				device.getUsbDeviceDescriptor().idProduct() == productId );
	}


	/**
	 * Find the CM19a and setup input and output pipes to it for sending commands
	 * and receiving messages.
	 * 
	 * @throws TransceiverException
	 * @throws UsbException
	 * @throws UnsupportedEncodingException
	 * 
	 */
	public void connect() throws TransceiverException, UsbException, UnsupportedEncodingException 
	{
		UsbDevice cm19a  = null;
		UsbEndpoint inEndpoint = null;
		UsbEndpoint outEndpoint = null;
		UsbPipe inputPipe = null;
		UsbPipe outputPipe = null;

		UsbHub rootUsbHub = UsbHostManager.getUsbServices().getRootUsbHub();
		if (rootUsbHub == null) {
			throw new TransceiverException("Cannot find root USB hub");
		}

		cm19a = findCM19a(rootUsbHub);
		if (cm19a == null ) {
			throw new TransceiverException("Cannot find CM19a. (Often, this is a permission problem in the /dev tree to the usb device.)");
		} else {
			log.info("Found CM19a");

			UsbConfiguration cm19aConfig = cm19a.getActiveUsbConfiguration();
			if ( cm19aConfig == null ) {
				throw new TransceiverException("Cannot get configuration from cm19a.");
			}

			try {
				usbInterface = (UsbInterface)cm19aConfig.getUsbInterfaces().get(0); // Danger? - Assumes there is only one
				log.info("CM19a manufacturer: " + cm19a.getManufacturerString());
				usbInterface.claim();
			} catch ( UsbException e ) {
				throw new TransceiverException( "Failed to claim the interface: " + e.getMessage() );
			}

			List<?> usbEndpoints = usbInterface.getUsbEndpoints();

			for (int i=0; i < usbEndpoints.size(); i++ ) {
				UsbEndpoint endpoint = (UsbEndpoint) usbEndpoints.get(i);

				if (UsbConst.ENDPOINT_DIRECTION_IN == endpoint.getDirection()) {
					inEndpoint = endpoint;
				} else if (UsbConst.ENDPOINT_DIRECTION_OUT == endpoint.getDirection()) {
					outEndpoint = endpoint;
				}
			}

			if (inEndpoint == null) {
				throw new TransceiverException ( "Could not find in endpoint for cm19a" );
			}
			if (outEndpoint == null) {
				throw new TransceiverException ( "Could not find out endpoint for cm19a" );
			}

			inputPipe = inEndpoint.getUsbPipe();

			try {
				inputPipe.open();
			} catch ( NullPointerException e ) {
				log.error("could not open the input pipe");
				try { 
					usbInterface.release();
					throw new TransceiverException("could not open the input pipe");
				} catch ( UsbException ue ) {
					throw new TransceiverException("could not open the input pipe and could not release the interface"
							+ ue.getMessage());
				}
			} catch ( UsbException ue2 ) {
				try { 
					log.error("Usb exception while trying to open the input pipe ");
					usbInterface.release();
					throw new TransceiverException("Usb exception while trying to open the input pipe " +
							ue2.getMessage());
				} catch ( UsbException ue ) {
					throw new TransceiverException("Usb exception while trying to open the input pipe and the interface was not released" +
							ue2.getMessage() + ue.getMessage());
				}
			}
			// Create a new thread that watches the input pipe
			receiver = new Receiver( inputPipe );
			receiverThread = new Thread( receiver );
			receiverThread.start();


			outputPipe = outEndpoint.getUsbPipe();
			try {
				outputPipe.open();
			} catch ( NullPointerException e ) {
				log.error("could not open the output pipe");
				try { 
					inputPipe.close();
					usbInterface.release();
					throw new TransceiverException("could not open the output pipe");
				} catch ( UsbException ue ) {
					throw new TransceiverException("could not open the output pipe and could not release the interface"
							+ ue.getMessage());
				}
			} catch ( UsbException ue2 ) {
				try { 
					log.error("Usb exception while trying to open the output pipe ");
					usbInterface.release();
					throw new TransceiverException("Usb exception while trying to open the output pipe " +
							ue2.getMessage());

				} catch ( UsbException ue ) {
					throw new TransceiverException("Usb exception while trying to open the output pipe and the interface was not released" +
							ue2.getMessage() + ue.getMessage());
				}
			}

			// Create a new thread for sending the commands to the output pipe
			transmitter = new Transmitter( outputPipe );
			transmitterThread = new Thread( transmitter );
			transmitterThread.start();
		}
	}


	/**
	 * Kills the thread watching for input from the CM19a
	 * Kills the thread sending commands to the CM19a
	 *
	 * @throws TransceiverException
	 * @throws UsbException 
	 */
	public void release() throws TransceiverException, UsbException {

		receiver.quit();
		transmitter.quit();
		commandOutputQueue.notify();

		try {
			usbInterface.release();
		} catch ( UsbException ue ) {
			throw new TransceiverException("Could not release the usbInterface: " 
					+ ue.getMessage());
		}
	}

	/**
	 * Implements the UsbPipeListener interface 
	 * - it is required by the lower level usb interface code
	 *
	 */
	class PipeListener implements UsbPipeListener {

		String direction = null;

		public PipeListener( String direction ) {
			this.direction = direction;
		}

		public void dataEventOccurred( UsbPipeDataEvent event ) {

			byte [] message = event.getData();
			int length = event.getActualLength();
			if ( message != null ) {
				StringBuilder sb = new StringBuilder();

				for (int i=0; i<length; i++)
					sb.append(" 0x" + UsbUtil.toHexString(message[i]));
				log.debug(String.format("%d bytes traveled %s: %s", length, direction, sb.toString()));
			}
		}

		public void errorEventOccurred( UsbPipeErrorEvent event ) {
			UsbException exception = event.getUsbException();

			log.error(String.format("Error %s: %s ", direction, exception.getMessage()));
		}
	}

	/**
	 * Reads commands and corresponding transceiver byte arrays (as strings) from file,
	 * converts transceiver command strings to byte arrays
	 * stores the tuples for later reference
	 * 
	 * @throws IOException
	 */
	public void loadCommands() throws IOException {

		commands = new HashMap<String, byte[]>();

		InputStream file = ClassLoader.getSystemResourceAsStream(COMMAND_FILENAME);
		if ( file == null )  {
			throw new IOException( "Command file not found: " + COMMAND_FILENAME);
		}
		BufferedReader in = new BufferedReader( new InputStreamReader( file ));

		String line;
		while ( (line = in.readLine()) != null ) {
			String [] tokens = line.split("\\s+", 2 );

			if (tokens.length != 2) { // skip empty lines
				continue;
			}

			String [] s = tokens[1].split("\\s+");

			byte [] commandBytes = new byte[s.length];
			for ( int i=0; i<s.length; i++ ) {
				commandBytes[i] = (byte)Integer.decode(s[i]).intValue();
			}			

			commands.put(tokens[0], commandBytes);
		}
	}

	/*
	 * Listens for data coming from the CM19a
	 *
	 */
	class Receiver implements Runnable {

		UsbPipe inPipe = null;
		UsbPipeListener inPipeListener = 
			new PipeListener("CM19a -> host");
		public boolean running = true;

		public Receiver(UsbPipe pipe) {
			inPipe = pipe;
			inPipe.addUsbPipeListener(inPipeListener);
		}

		public void run() {
			byte[] buffer = new byte[maxInputPacketSize];				

			while (running) {
				try {
					inPipe.syncSubmit( buffer );
				} catch ( UsbException uE ) {
					if (running) {
						log.error("Unable to submit data buffer to CM19a : " + uE.getMessage());
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
	 * Sends data (commands) to the CM19a
	 *
	 */
	class Transmitter implements Runnable {

		UsbPipe outPipe = null;
		UsbPipeListener outPipeListener = 
			new PipeListener("host -> CM19a");
		boolean running = true;
		byte[] sendThis = null;

		public Transmitter ( UsbPipe pipe ) {
			outPipe = pipe;
			outPipe.addUsbPipeListener( outPipeListener );
		}


		public synchronized void run() {
			while ( running ) {

				/*Keep processing messages or sleep if there are none*/
				synchronized ( commandOutputQueue ) {
					if ( commandOutputQueue.size() == 0 )
						try {
							commandOutputQueue.wait();
						} catch ( InterruptedException e ) {
							//Do nothing
						}
				}

				synchronized ( commandOutputQueue ) {
					if ( commandOutputQueue.size() > 0 ) 
						sendThis = commandOutputQueue.removeFirst();
				}

				if ( sendThis != null ) {
					try {
						outPipe.syncSubmit( sendThis );
						sendThis = null;
					}	catch (UsbException ue) {
						log.error( ue );

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
		String start = "start";

		CM19a transceiver = new CM19a();

		/*Start the driver*/
		try {
			transceiver.connect();
		} catch ( Exception e ) {
			log.error( "Could not connect to the CM19a: " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Ready for commands");

		while (true) {
			try {
				InputStreamReader converter = 
					new InputStreamReader(System.in);
				BufferedReader in = new BufferedReader(converter);
				UsbDevice device = null;

				String command = in.readLine();

				if (command.equals( quit )) {
					if ( device != null )
						transceiver.release();
					System.exit(0);
				}
				else if (command.equals( start )) {
					transceiver.connect();
					log.info("Connected to transceiver");
				}
				else {
					if ( command.trim().length() > 0 ) {
						log.debug(String.format("Sending command: %s", command));
						transceiver.sendCommand( command );
					}
				}

			} catch (Exception e) {
				log.error( e.getMessage() );
			}
		}
	}

}
