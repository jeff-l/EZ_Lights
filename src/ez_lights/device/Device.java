package ez_lights.device;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Device {
	private String name;

	public Device (String name) {
		this.name = name;
	}

	public static List<Device> loadDevices(String fileName) {
		List<Device> devices = new ArrayList<Device>();

		Device t;
		String inputLine;
		String [] fields = null;

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			while ((inputLine = br.readLine()) != null) {
				if (inputLine.startsWith("#")) { // Skip comment lines
					continue;
				}
				fields = inputLine.split("/t");
				if (fields.length >= 1) {
					t = new Device(fields[0]); 
					devices.add(t);		
				}
			} 
		}
		catch (IOException e) {
			// TO DO Handle this
			System.err.println("Error: " + e);
		}

		// TO DO need to close file handles- which one?


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
