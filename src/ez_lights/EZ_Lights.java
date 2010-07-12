package ez_lights;

import java.util.List;

import javax.swing.SwingUtilities;

import ez_lights.device.Device;
import ez_lights.gui.EZ_LightsMainFrame;

public class EZ_Lights {

	public static void main(String[] args) {

		List<Device> devices = Device.loadDevices();

		System.out.println("Devices:");
		for (Device d : devices) {
			System.out.println(d.toString());
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				EZ_LightsMainFrame myFrame = new EZ_LightsMainFrame();
				myFrame.setLocationRelativeTo(null);
				myFrame.setVisible(true);
			}
		});
	}

}
