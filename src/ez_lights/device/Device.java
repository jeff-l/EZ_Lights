package ez_lights.device;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class Device {
	private String name;

	public Device (String name) {
		this.name = name;
	}

	public static List<Device> loadDevices() {
		List<Device> devices = new ArrayList<Device>();

		Device t;

		t = new Device("Unknown 1"); // TO DO - change this to get this from 
		devices.add(t);
		t = new Device("Unknown 2"); // TO DO - change this to get this from 
		devices.add(t);
		t = new Device("Unknown 3"); // TO DO - change this to get this from 
		devices.add(t);

		return devices;
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
		result.append("   Auto ON time: ");
		result.append("TO DO"); // TO DO
		result.append("   Auto OFF time: ");
		result.append("TO DO"); // TO DO

		return result.toString();
	}
}
