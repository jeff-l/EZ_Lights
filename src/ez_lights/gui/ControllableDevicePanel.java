package ez_lights.gui;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import org.apache.log4j.Logger;

import ez_lights.homeAutomation.Device;

public class ControllableDevicePanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;
	
	static Logger log = Logger.getLogger(ControllableDevicePanel.class);	
	
	private Device device;
	private GroupLayout groupLayout;
	private JButton offButton;
	private JButton onButton;
	private JTextField deviceName;
	private JTextField autoOffTextField;
	private JLabel autoOffLabel;
	private JTextField autoOnTextField;
	private JLabel autoOnLabel;	
	private JTextField deviceCodeTextField;
	private JLabel deviceCodeLabel;
	private JTextField houseCodeTextField;
	private JLabel houseCodeLabel;
	
	
	public ControllableDevicePanel(Device d) {
		super();
		this.device = d;
		initPanel();
	}
	
	public GroupLayout getGroupLayout() {
		return groupLayout;
	}
	
	private void initPanel() {

		{
			deviceName = new JTextField();
			deviceName.setText(device.getName());
			deviceName.addFocusListener(new deviceNameFocusListener());
			deviceName.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					device.setName(deviceName.getText());
				}
			});
		}

		onButton = new JButton();
		onButton.setText("On");
		onButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				device.on();
			}
		});

		offButton = new JButton();
		offButton.setText("Off");
		offButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				device.off();
			}
		});
		
		autoOnLabel = new JLabel();
		autoOnLabel.setText("Auto On:");
		
		{
			autoOnTextField = new JTextField();
			autoOnTextField.setText((device.getAutoOn() == null) 
					? "none" : String.format("%1$tH%1$tM", device.getAutoOn())
			);
			autoOnTextField.addFocusListener(new autoOnTextFieldFocusListener());
			autoOnTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateAutoOnTextField();
				}
			});
		}

		autoOffLabel = new JLabel();
		autoOffLabel.setText("Auto Off:");
		{
			autoOffTextField = new JTextField();
			autoOffTextField.setText((device.getAutoOff() == null) 
					? "none" : String.format("%1$tH%1$tM", device.getAutoOff()));
			
			autoOffTextField.addFocusListener(new autoOffTextFieldFocusListener());

			autoOffTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
									updateAutoOffTextField();
				}
			});
		}

		houseCodeLabel = new JLabel();
		houseCodeLabel.setText("H/C");

		{
			houseCodeTextField = new JTextField();
			houseCodeTextField.setText(device.getHouseCode());
			houseCodeTextField.addFocusListener(new houseCodeFocusListener());
			houseCodeTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateHouseCodeTextField();
				}
			});
		}
		
		deviceCodeLabel = new JLabel();
		deviceCodeLabel.setText("D/C");

		{
			deviceCodeTextField = new JTextField();
			deviceCodeTextField.setText(device.getDeviceCode());
			deviceCodeTextField.addFocusListener(new deviceCodeFocusListener());
			deviceCodeTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateDeviceCodeTextField();
				}
			});
		}		
		
		groupLayout = new GroupLayout((JComponent)this);
		this.setLayout(groupLayout);
		this.setBorder( BorderFactory.createEtchedBorder() );

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(deviceName, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(onButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(offButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(autoOnLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(autoOnTextField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(autoOffLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(autoOffTextField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)						
						    .addComponent(houseCodeLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(houseCodeTextField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)						
						    .addComponent(deviceCodeLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(deviceCodeTextField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)						
				)
		);	
		
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(deviceName, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE))			
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(onButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(offButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addGap(24)
						.addComponent(autoOnLabel, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
						.addComponent(autoOnTextField, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(autoOffLabel, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
						.addComponent(autoOffTextField, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)	
						.addGap(15)
						.addComponent(houseCodeLabel, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
						.addComponent(houseCodeTextField, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
						.addGap(10)
						.addComponent(deviceCodeLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(deviceCodeTextField, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
		);		
	
	}
	
	private void updateHouseCodeTextField () {
		log.debug("User input for house code: " + houseCodeTextField.getText());
		String newCode = normalizeHouseCode(houseCodeTextField.getText());

		if (newCode != null) {
			device.setHouseCode(newCode);
			houseCodeTextField.setText(newCode);
		}
		else {
			houseCodeTextField.setText(device.getHouseCode());
		}
	}	
	
	private String normalizeHouseCode(String code) {
		
		if (code == null)  {
			return null;
		}
		
		String newCode = code.trim().toUpperCase();
		if (newCode.length() != 1) {
			return null;
		}
		
		if ((newCode.compareTo("A") < 0) || (newCode.compareTo("P") > 0)) {
			return null;
		}
		
		return newCode;
	}

	
	private void updateDeviceCodeTextField () {
		log.debug("User input for device code: " + deviceCodeTextField.getText());
		String newCode = normalizeDeviceCode(deviceCodeTextField.getText());

		if (newCode != null) {
			device.setDeviceCode(newCode);
			deviceCodeTextField.setText(newCode);
		}
		else {
			deviceCodeTextField.setText(device.getDeviceCode());
		}
	}	
	
	private String normalizeDeviceCode(String code) {
		int newCode;
		
		if (code == null)  {
			return null;
		}
		
		try {
			newCode = Integer.parseInt(code.trim());
		} catch (Exception e) {
			return null;
		}
		
		if ((newCode < 1) || (newCode > 16)) {
			return null;
		}
		
		return Integer.toString(newCode);
	}

	
	private void updateAutoOnTextField () {
		log.debug("User input for auto on: " + autoOnTextField.getText());
		try {
			device.setAutoOn(autoOnTextField.getText());
		} catch (Exception e2) {
			// ignore
		}
		// because the time might have been normalized somewhere
		autoOnTextField.setText((device.getAutoOn() == null) ? "none" : String.format("%1$tH%1$tM", device.getAutoOn())
		);	
	}
	
	private void updateAutoOffTextField () {
		log.debug("User input for auto off: " + autoOffTextField.getText());
		try {
			device.setAutoOff(autoOffTextField.getText());
		} catch (Exception e2) {
			// ignore
		}
		// because the time might have been normalized somewhere
		autoOffTextField.setText((device.getAutoOff() == null) ? "none" : String.format("%1$tH%1$tM", device.getAutoOff())
		);	
	}
	
	class deviceNameFocusListener implements FocusListener {

		public void focusLost(FocusEvent e) {
			device.setName(deviceName.getText());
		}

		@Override
		public void focusGained(FocusEvent arg0) {
			// ignore
		}
	}
	
	class houseCodeFocusListener implements FocusListener {

		public void focusLost(FocusEvent e) {
			updateHouseCodeTextField();		}

		@Override
		public void focusGained(FocusEvent arg0) {
			// ignore
		}
	}	
	
	class deviceCodeFocusListener implements FocusListener {

		public void focusLost(FocusEvent e) {
			updateDeviceCodeTextField();		}

		@Override
		public void focusGained(FocusEvent arg0) {
			// ignore
		}
	}	
	
	class autoOnTextFieldFocusListener implements FocusListener {

		public void focusLost(FocusEvent e) {
			updateAutoOnTextField();
		}

		@Override
		public void focusGained(FocusEvent arg0) {
			// ignore
		}
	}
	class autoOffTextFieldFocusListener implements FocusListener {

		public void focusLost(FocusEvent e) {
			updateAutoOffTextField();
		}

		@Override
		public void focusGained(FocusEvent arg0) {
			// ignore
		}
	}
	
}
