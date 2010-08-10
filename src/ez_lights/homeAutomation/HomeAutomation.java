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

import java.util.List;

import transceiver.Transceiver;


public interface HomeAutomation {

	public List<Device> getDevices ();
	
	public void setDevices(List<Device> devices);
	
	public Transceiver getTranceiver();
	
	public void setTransceiver(Transceiver transceiver);
	
}
