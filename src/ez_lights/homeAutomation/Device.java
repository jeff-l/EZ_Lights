package ez_lights.homeAutomation;

/**
 * Copyright (c) 2010, Jeff Luhrsen
 * All Rights Reserved.
 *
 *     This file is part of EZ_Lights.
 *
 *  EZ_Lights is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  EZ_Lights is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with EZ_Lights.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import transceiver.Transceiver;


public class Device {
	private final static long MILL_SEC_PER_DAY = 1000*60*60*24;

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
	private Timer onTimer;
	private Timer offTimer;

	public String getHouseCode() {
		return houseCode;
	}

	public void setHouseCode(String houseCode) {
		this.houseCode = houseCode;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public Calendar getAutoOn() {
		return autoOn;
	}

	public Calendar getAutoOff() {
		return autoOff;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Device (Transceiver transceiver) { // I need to find a way that does NOT require the transceiver being passed in
		this.transceiver = transceiver;
	}

	public Timer buildTimer (String command, Calendar time) {

		TimerTask task  = new deviceEvent(this, command);

		Calendar eventTime = new GregorianCalendar();
		eventTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		eventTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		if (eventTime.before(new GregorianCalendar())) {
			// Event time has already passed for today, so set the timer for tomorrow
			eventTime.add(Calendar.DAY_OF_MONTH, 1);
		}

		Timer timer = new Timer();
		log.debug("Setting timer for: " + eventTime.getTime().toString());

		timer.scheduleAtFixedRate(task, eventTime.getTime(), MILL_SEC_PER_DAY);
		return timer;
	}

	public String buildCommand (String c) {
		// TODO: instead of a string, should the command be a class?
		return houseCode + deviceCode + c;
	}

	public void runCommand(String command) {

		try {
			log.debug( "Sending " + command );
			transceiver.sendCommand(command);
		} catch (Exception e) {
			log.error( e.getMessage() );
		}		
	}

	public void on() {

		try {
			String command = buildCommand("ON"); 
			runCommand(command);
		} catch (Exception e) {
			log.error( e.getMessage() );
		}		
	}

	public void off() {

		try {
			String command = buildCommand("OFF"); 
			runCommand(command);
		} catch (Exception e) {
			log.error( e.getMessage() );
		}		
	}

	public String toPrettyString() {
		StringBuilder result = new StringBuilder();

		result.append("Name: ");
		result.append(name);
		result.append("House Code: ");
		result.append(houseCode);
		result.append("Device Code: ");
		result.append(deviceCode);
		result.append("   Auto ON: ");
		result.append((autoOn == null) ? "None" : String.format("Time: %1$tH%1$tM", autoOn));
		result.append("   Auto OFF: ");
		result.append((autoOff == null) ? "None" : String.format("Time: %1$tH%1$tM", autoOff));

		return result.toString();
	}

	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(name);
		result.append(",");
		result.append(houseCode);
		result.append(",");
		result.append(deviceCode);
		result.append(",");
		result.append((autoOn == null) ? "None" : String.format("%1$tH%1$tM", autoOn));
		result.append(",");
		result.append((autoOff == null) ? "None" : String.format("%1$tH%1$tM", autoOff));

		return result.toString();
	}

	public Calendar setAutoOn(String s ) throws IllegalArgumentException { 
		Calendar newTime = null;
		String newTimeString = s.trim();

		try {
			newTime = new GregorianCalendar(0, 0, 0, Integer.parseInt(newTimeString.substring(0,2)), Integer.parseInt(newTimeString.substring(2)), 0);
		} catch (Exception e1) {
			log.debug("New value not a valid time. Not setting new timer.");
			throw new IllegalArgumentException("Invalid time");
		}

		if (autoOn != null) {
			if ((autoOn.get(Calendar.HOUR_OF_DAY) == newTime.get(Calendar.HOUR_OF_DAY)) && 
					(autoOn.get(Calendar.MINUTE) == newTime.get(Calendar.MINUTE))) {
				// time hasn't changed - no need to stop the old timer and start another
				log.debug("Time has not changed - not setting new timer.");
				return autoOn;
			}
		}

		if (onTimer != null) {
			onTimer.cancel();
		}
		autoOn = newTime;
		onTimer = buildTimer(buildCommand("ON"), autoOn);
		log.debug(String.format("New timer set for %1$tH%1$tM", autoOn));

		return newTime;
	}

	public Calendar setAutoOff(String s ) throws IllegalArgumentException { 
		Calendar newTime = null;
		String newTimeString = s.trim();

		try {
			newTime = new GregorianCalendar(0, 0, 0, Integer.parseInt(newTimeString.substring(0,2)), Integer.parseInt(newTimeString.substring(2)), 0);
		} catch (Exception e1) {
			log.debug("New value not a valid time. Not setting new timer.");
			throw new IllegalArgumentException("Invalid time");
		}

		if (autoOff != null) {
			if ((autoOff.get(Calendar.HOUR_OF_DAY) == newTime.get(Calendar.HOUR_OF_DAY)) && 
					(autoOff.get(Calendar.MINUTE) == newTime.get(Calendar.MINUTE))) {
				// time hasn't changed - no need to stop the old timer and start another
				log.debug("Time has not changed - not setting new timer.");
				return autoOff;
			}
		}

		if (offTimer != null) {
			offTimer.cancel();
		}
		autoOff = newTime;
		offTimer = buildTimer(buildCommand("OFF"), autoOff);
		log.debug(String.format("New timer set for %1$tH%1$tM", autoOff));

		return newTime;
	}	

	public static List<Device> loadDevices(String fileName) {
		return loadDevices(null, fileName);
	}

	// TODO: All this loading from a file needs to go somewhere else
	// It isn't xml so the conf file is easy for non-programmers to edit
	public static List<Device> loadDevices(Transceiver transceiver, String fileName) {

		List<Device> devices = new ArrayList<Device>();
		InputStream file = null;
		BufferedReader br = null;

		Device t;
		String inputLine;
		String [] fields = null;

		try {
			file = ClassLoader.getSystemResourceAsStream(fileName);
			br = new BufferedReader( new InputStreamReader( file ));			

			while ((inputLine = br.readLine()) != null) {
				inputLine = inputLine.trim();
				// log.debug("Device config file line: " + inputLine);
				if ((inputLine.startsWith("#")) || (inputLine.length() == 0)){ // Skip comment lines and empty lines
					continue;
				}
				fields = inputLine.split(",");
				if (fields.length != DEVICE_INFO_FIELD_COUNT) { // Skip lines with the wrong number of fields
					log.error("Skipping bad device config file line: " + inputLine);
				}

				fields[DEVICE_NAME] = fields[DEVICE_NAME].trim();
				//				t = new Device(transceiver, fields[DEVICE_NAME]); 
				t = new Device(transceiver); 
				t.name = fields[DEVICE_NAME];
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
					try {
						t.setAutoOn(fields[TIME_ON_INDEX]);
					} catch (Exception e) {
						log.warn(String.format("Bad time field in config file: %s", fields[TIME_ON_INDEX]));
					}
				}

				// Parse for autoOff field
				fields[TIME_OFF_INDEX] = fields[TIME_OFF_INDEX].trim();
				if (fields[TIME_OFF_INDEX].equalsIgnoreCase("none")) {
					// No autoOff time 
					;
				} else {
					try {
						t.setAutoOff(fields[TIME_OFF_INDEX]);
					} catch (Exception e) {
						log.warn(String.format("Bad time field in config file: %s", fields[TIME_OFF_INDEX]));
					}
				}
			}
		}
		catch (IOException e) {
			// TO DO Handle this
			log.error("Error: " + e);
		}

		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				log.error("Error: " + e);
			}
		}

		return devices;
	}

	public static void saveToFile(List<Device> devices, File file) {

		BufferedWriter out = null;

		log.debug("Saving to" + file.getAbsolutePath());

		try {
			out = new BufferedWriter(new FileWriter(file));

			// Write some header information
			out.write("#Fields are read sequentially so place-holders must exist for fields you wish to skip. For undesired time values, use 'none'");
			out.newLine();
			out.write("#Times must be specified in 24-hour format");
			out.newLine();
			out.write("#House codes must be upper case");
			out.newLine();
			out.write("#");
			out.newLine();
			out.write("# Device Name ,House Code, Device Code,Auto Time On ,Auto Time Off");
			out.newLine();

			for (Device d : devices) {
				out.write(d.toString());
				out.newLine();
			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}	

}
