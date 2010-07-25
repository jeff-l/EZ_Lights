package ez_lights.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import transceiver.Transceiver;


public class Device {
	private static final int DEVICE_NAME = 0;
	private static final int HOUSE_CODE = 1;
	private static final int DEVICE_CODE = 2;
	private static final int TIME_ON_INDEX = 3;
	private static final int TIME_OFF_INDEX = 4;
	private static final int DEVICE_INFO_FIELD_COUNT = 5;

	static Logger log = Logger.getLogger(Device.class);	

	private Transceiver transceiver;
	private String name;
	private String houseCode;
	private String deviceCode;
	private Calendar autoOn;
	private Calendar autoOff;

	public Device (Transceiver transceiver, String name) { // I need to find a way that does NOT require the transceiver being passed in
		this.transceiver = transceiver;
		this.name = name;
	}

	public static List<Device> loadDevices(String fileName) {
		return loadDevices(null, fileName);
	}

	// TODO: All this loading from a file needs to go somewhere else
	// TODO: It all needs to be converted to xml
	public static List<Device> loadDevices(Transceiver transceiver, String fileName) {

		List<Device> devices = new ArrayList<Device>();

		Device t;
		String inputLine;
		String [] fields = null;
		
		try {
			InputStream file = ClassLoader.getSystemResourceAsStream(fileName);
			BufferedReader br = new BufferedReader( new InputStreamReader( file ));			

			while ((inputLine = br.readLine()) != null) {
				inputLine = inputLine.trim();
				log.debug("Device config file line: " + inputLine);
				if ((inputLine.startsWith("#")) || (inputLine.length() == 0)){ // Skip comment lines and empty lines
					continue;
				}
				fields = inputLine.split(",");
				if (fields.length != DEVICE_INFO_FIELD_COUNT) { // Skip lines with the wrong number of fields
					log.error("Skipping bad device config file line: " + inputLine);
				}

				fields[DEVICE_NAME] = fields[DEVICE_NAME].trim();
				t = new Device(transceiver, fields[DEVICE_NAME]); 
				devices.add(t);	

				fields[HOUSE_CODE] = fields[HOUSE_CODE].trim();
				t.houseCode = fields[HOUSE_CODE];
				fields[DEVICE_CODE] = fields[DEVICE_CODE].trim();
				t.deviceCode = fields[DEVICE_CODE];

				// Parse for autoOn field
				fields[TIME_ON_INDEX] = fields[TIME_ON_INDEX].trim();
				if (fields[TIME_ON_INDEX].equalsIgnoreCase("none")) {
					// No autoOn time 
					;
				} else {
					// TODO: This needs a lot more error/exception checking/handling and range checking
					t.autoOn = new GregorianCalendar(0, 0, 0, Integer.parseInt(fields[TIME_ON_INDEX].substring(0,2)), Integer.parseInt(fields[TIME_ON_INDEX].substring(2)));
				}

				// Parse for autoOff field
				fields[TIME_OFF_INDEX] = fields[TIME_OFF_INDEX].trim();
				if (fields[TIME_OFF_INDEX].equalsIgnoreCase("none")) {
					// No autoOff time 
					;
				} else {
					// TODO: This needs a lot more error/exception checking/handling and range checking
					t.autoOff = new GregorianCalendar(0, 0, 0, Integer.parseInt(fields[TIME_OFF_INDEX].substring(0,2)), Integer.parseInt(fields[TIME_OFF_INDEX].substring(2)));
				}
			}
	}
	catch (IOException e) {
		// TO DO Handle this
		System.err.println("Error: " + e);
	}

	// TODO: need to close file handles- which one?

	return devices;
}

public void on() {
	log.debug(getName() + "On button pushed");

	try {
		String command = houseCode + deviceCode + "ON"; // TODO: this format okay?

		if ( command.trim().length() > 0 ) {
			log.debug( "Sending " + command );
			String formattedCommand = transceiver.formatCommand( command );
			transceiver.send( formattedCommand );
		}

	} catch (Exception e) {
		System.out.println( e.getMessage() );
	}		
}

public void off() {
	log.debug(getName() + "Off button pushed");

	try {
		String command = houseCode + deviceCode + "OFF"; // TODO: this format okay?

		if ( command.trim().length() > 0 ) {
			log.debug( "Sending " + command );
			String formattedCommand = transceiver.formatCommand( command );
			transceiver.send( formattedCommand );
		}

	} catch (Exception e) {
		System.out.println( e.getMessage() );
	}		
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String toString() {
	StringBuilder result = new StringBuilder();

	result.append("Name: ");
	result.append(name);
	result.append("House Code: ");
	result.append(houseCode);
	result.append("Device Code: ");
	result.append(deviceCode);
	result.append("   Auto ON time: ");
	result.append((autoOn == null) ? "None" : String.format("Time: %1$tH%1$tM", autoOn));
	result.append("   Auto OFF time: ");
	result.append((autoOff == null) ? "None" : String.format("Time: %1$tH%1$tM", autoOff));

	return result.toString();
}
}
