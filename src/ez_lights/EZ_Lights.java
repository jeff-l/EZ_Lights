package ez_lights;

import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import transceiver.Transceiver;
import transceiver.cm19a.CM19a;
import ez_lights.device.Device;
import ez_lights.gui.EZ_LightsMainFrame;

public class EZ_Lights {

	static Logger log = Logger.getLogger(EZ_Lights.class);	

	static List<Device> devices = null;
	static Transceiver transceiver = null;

	public Transceiver getTranceiver () {
		return transceiver;
	}
	public static void main(String[] args) {

		transceiver = new CM19a();
		devices = Device.loadDevices(transceiver, "EZ_Lights.conf");

		// Good for debugging
		// TODO: Check for debug flag or change to log
		/*		System.out.println("Devices:");
		for (Device d : devices) {
			System.out.println(d.toString());
		}*/

		// TODO: Have a popup or status pane stating we are trying to attach to the transceiver

		// Connect to the transceiver
		try {
			log.info("Connecting to the transceiver...");
			transceiver.startDriver(); // TODO: what should this be?
		} catch ( Exception e ) {
			log.error( "Error when trying to connect to the transciever: " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Connected to transceiver.");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				EZ_LightsMainFrame myFrame = new EZ_LightsMainFrame(devices);
				myFrame.setLocationRelativeTo(null);
				myFrame.setVisible(true);
			}
		});

	}

}
