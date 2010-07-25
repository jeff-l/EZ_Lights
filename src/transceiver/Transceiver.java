package transceiver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.usb.UsbException;

import transceiver.cm19a.CM19aException;



public interface Transceiver {

	void startDriver() throws CM19aException, UsbException, UnsupportedEncodingException;

	void releaseDevice() throws CM19aException, UsbException;

	void init() throws UsbException;

	String formatCommand(String command) throws CM19aException;

	void send(String formattedCommand) throws CM19aException, FileNotFoundException, IOException;

}
