package ez_lights;

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

import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import transceiver.Transceiver;
import transceiver.cm19a.CM19a;
import ez_lights.gui.EZ_LightsMainFrame;
import ez_lights.homeAutomation.Device;
import ez_lights.homeAutomation.HomeAutomation;

public class EZ_Lights implements HomeAutomation {

	static Logger log = Logger.getLogger(EZ_Lights.class);	

    List<Device> devices = null;
	Transceiver transceiver = null;

	
	public EZ_Lights (Transceiver transceiver) {
		super();
		this.transceiver = transceiver;
	}
	
	public static void main(String[] args) {

		Transceiver transceiver = new CM19a();

		final EZ_Lights ez = new EZ_Lights(transceiver);
		
		ez.setDevices(Device.loadDevices(transceiver, "EZ_Lights.conf"));

		// Good for debugging
		// TODO: Check for debug flag or change to log
		/*		System.out.println("Devices:");
		for (Device d : ez.getDevices()) {
			System.out.println(d.toString());
		}
		 */
		
		// TODO: Have a popup or status pane stating we are trying to attach to the transceiver

		// Connect to the transceiver
		try {
			log.info("Connecting to the transceiver...");
			transceiver.connect(); 
		} catch ( Exception e ) {
			log.error( "Error when trying to connect to the transciever: " + e.getMessage());
			System.exit(0);
		}
		log.info("Connected to transceiver.");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				EZ_LightsMainFrame myFrame = new EZ_LightsMainFrame(ez);
				myFrame.setLocationRelativeTo(null);
				myFrame.setVisible(true);
			}
		});
	}
	
	@Override
	public List<Device> getDevices() {
		return devices;
	}
	
	@Override
	public void setDevices(List<Device> devices) {
		this.devices = devices;		
	}

	@Override
	public Transceiver getTranceiver() {
		return transceiver;
	}

	@Override
	public void setTransceiver(Transceiver transceiver) {
		this.transceiver = transceiver;		
	}

}
