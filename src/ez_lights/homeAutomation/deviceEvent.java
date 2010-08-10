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

import java.util.TimerTask;

import org.apache.log4j.Logger;

public final class deviceEvent extends TimerTask {
	
	static Logger log = Logger.getLogger(deviceEvent.class);	

	Device device;
	String action;

	public deviceEvent (Device device, String action) {
		this.device = device;
		this.action = action;
	}

	public void run(){
		log.debug(String.format("Event fired for device: %s, action; %s", device.getName(), action));
		device.runCommand(action);
	}

}





