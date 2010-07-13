package ez_lights;

import java.util.List;

import javax.swing.SwingUtilities;

import ez_lights.device.Device;
import ez_lights.gui.EZ_LightsMainFrame;

public class EZ_Lights {

	static List<Device> devices = null;
	
	public static void main(String[] args) {

		devices = Device.loadDevices("/home/jeff/EZ_Lights/resources/EZ_Lights.conf"); // TO DO: access this indirectly

		// Good for debugging
/*		System.out.println("Devices:");
		for (Device d : devices) {
			System.out.println(d.toString());
		}
*/
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				EZ_LightsMainFrame myFrame = new EZ_LightsMainFrame(devices);
				myFrame.setLocationRelativeTo(null);
				myFrame.setVisible(true);
			}
		});
	
	}

}
