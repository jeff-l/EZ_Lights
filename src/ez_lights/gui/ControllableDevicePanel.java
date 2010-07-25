package ez_lights.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import ez_lights.device.Device;

public class ControllableDevicePanel extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Device device;
	private GroupLayout groupLayout;
	private JButton OffButton;
	private JButton OnButton;
	private JTextField DeviceName;
	
	public ControllableDevicePanel() {
		super();
		initPanel("Unknown");
	}
	
	public ControllableDevicePanel(Device d) {
		super();
		this.device = d;
		initPanel(d.getName());
	}
	
	public GroupLayout getGroupLayout() {
		return groupLayout;
	}
	
	private void initPanel(String deviceNameText) {

		DeviceName = new JTextField();
		DeviceName.setText(deviceNameText);

		OnButton = new JButton();
		OnButton.setText("On");
		OnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.println(DeviceName.getText() + " On button pushed.");
				device.on();
			}
		});

		OffButton = new JButton();
		OffButton.setText("Off");
		OffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.println(DeviceName.getText() + " Off button pushed.");
				device.off();
			}
		});
		
		groupLayout = new GroupLayout((JComponent)this);
		this.setLayout(groupLayout);
		this.setBorder( BorderFactory.createEtchedBorder() );

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(DeviceName, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(OnButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(OffButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
		);	
		
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(DeviceName, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE))				
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(OnButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(OffButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
		);		
	
	}
	
}
