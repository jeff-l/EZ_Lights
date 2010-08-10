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
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import ez_lights.homeAutomation.Device;
import ez_lights.homeAutomation.HomeAutomation;


public class EZ_LightsMainFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(EZ_LightsMainFrame.class);	

	private JButton CloseButton;
	private AbstractAction closeAboutAction;
	private ControllableDevicePanel controllableDevicePanel;
	private JTextField jTextField1;
	private JButton OKButton;
	private JDialog AboutDialog;
	private AbstractAction aboutAction;
	private JMenuItem jMenuItem1;
	private JMenuItem fileMenuItem_saveFile;
	// private JMenuItem fileMenuItem_loadFile;
	private JMenu HelpMenu;
	private JMenu fileMenu;
	private JMenuBar jMenuBar1;
	private JFileChooser chooser;	

	private HomeAutomation ha;


	//	public EZ_LightsMainFrame(List<Device> devices) {
	//		super("EZ_Lights");
	//		initGUI(devices);
	//	}	

	public EZ_LightsMainFrame(HomeAutomation ha) {
		super("EZ_Lights");
		this.ha = ha;
		initGUI(ha.getDevices());
	}

	private void initGUI(List<Device> devices) {

		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			jMenuBar1 = new JMenuBar();
			setJMenuBar(jMenuBar1);
			{
				fileMenu = new JMenu();
				jMenuBar1.add(fileMenu);
				fileMenu.setText("File");
				{
					String filename = "~" + File.pathSeparator + "EZ_Lights.conf";
					chooser = new JFileChooser(new File(filename)); 
					{
						fileMenuItem_saveFile = new JMenuItem();
						fileMenu.add(fileMenuItem_saveFile);
						fileMenuItem_saveFile.setText("Save file...");
						fileMenuItem_saveFile.addActionListener(new deviceFileActionListener2());
					}
					/*					{
						fileMenuItem_loadFile = new JMenuItem();
						fileMenu.add(fileMenuItem_loadFile);
						fileMenuItem_loadFile.setText("Load file...");
						fileMenuItem_loadFile.addActionListener(new deviceFileActionListener2());
					}*/
				}

				HelpMenu = new JMenu();
				jMenuBar1.add(HelpMenu);
				HelpMenu.setText("Help");
				{
					jMenuItem1 = new JMenuItem();
					HelpMenu.add(jMenuItem1);
					jMenuItem1.setText("jMenuItem1");
					jMenuItem1.setAction(getAboutAction());
				}
			}
			CloseButton = new JButton();
			CloseButton.setText("Close");
			CloseButton.setAction(new AbstractAction("Close", null) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					setVisible(false);
					dispose();
					// TODO: Must disconnect from the transceiver
					System.exit(0);
				}
			});

			GroupLayout.SequentialGroup sequentialGroup = 	thisLayout.createSequentialGroup()
			.addContainerGap();
			GroupLayout.ParallelGroup parallelGroup = thisLayout.createParallelGroup(); 

			for (Device d : devices) {
				controllableDevicePanel = new ControllableDevicePanel(d);
				sequentialGroup.addComponent(controllableDevicePanel, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE);
				parallelGroup.addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
						.addComponent(controllableDevicePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				);			
			}

			sequentialGroup.addGap(10);
			sequentialGroup.addComponent(CloseButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
			sequentialGroup.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);			
			thisLayout.setVerticalGroup(sequentialGroup);

			parallelGroup.addGroup(GroupLayout.Alignment.CENTER, thisLayout.createSequentialGroup()
					//	.addGap(293)
					.addComponent(CloseButton, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
					//	.addGap(0, 0, Short.MAX_VALUE)
			);

			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(parallelGroup)
					.addGap(7));			

			pack();
			//	this.setSize(396, 322);
		} catch (Exception e) {
			// TO DO: add error handling
			e.printStackTrace();
		}
	}

	private AbstractAction getAboutAction() {
		if(aboutAction == null) {
			aboutAction = new AbstractAction("About", null) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					getAboutDialog().pack();
					getAboutDialog().setLocationRelativeTo(null);
					getAboutDialog().setVisible(true);
				}
			};
		}
		return aboutAction;
	}

	private JDialog getAboutDialog() {
		if(AboutDialog == null) {
			AboutDialog = new JDialog(this);
			GroupLayout AboutDialogLayout = new GroupLayout((JComponent)AboutDialog.getContentPane());
			AboutDialog.setLayout(AboutDialogLayout);
			AboutDialog.setSize(339, 237);
			{
				OKButton = new JButton();
				OKButton.setText("OK");
				OKButton.setAction(getCloseAboutAction());
			}
			{
				jTextField1 = new JTextField();
				jTextField1.setText("EZ_Lights Copyright (c) 2010 Jeff Luhrsen");
				jTextField1.setHorizontalAlignment(SwingConstants.CENTER);
			}
			AboutDialogLayout.setHorizontalGroup(AboutDialogLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(AboutDialogLayout.createParallelGroup()
							.addComponent(jTextField1, GroupLayout.Alignment.LEADING, 0, 306, Short.MAX_VALUE)
							.addGroup(GroupLayout.Alignment.LEADING, AboutDialogLayout.createSequentialGroup()
									.addGap(0, 239, Short.MAX_VALUE)
									.addComponent(OKButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)))
									.addContainerGap());
			AboutDialogLayout.setVerticalGroup(AboutDialogLayout.createSequentialGroup()
					.addContainerGap(39, 39)
					.addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
					.addGap(0, 72, Short.MAX_VALUE)
					.addComponent(OKButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap());
		}
		return AboutDialog;
	}

	private AbstractAction getCloseAboutAction() {
		if(closeAboutAction == null) {
			closeAboutAction = new AbstractAction("OK", null) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					getAboutDialog().dispose();
				}
			};
		}
		return closeAboutAction;
	}


	public class deviceFileActionListener2 implements ActionListener {


		@Override
		public void actionPerformed(ActionEvent evt) {
			int returnVal;

			if (evt.getSource() == fileMenuItem_saveFile) {
				returnVal = chooser.showSaveDialog(null);
			}
			else {
				returnVal = chooser.showOpenDialog(null);
			}

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				log.debug("Selected file: " + file.getName());
				log.debug("Selected file: " + file.getAbsolutePath());

				if (evt.getSource() == fileMenuItem_saveFile) {
					Device.saveToFile(ha.getDevices(), file);
				}
				else {
					// Here if a load file is ever implemented 
				}				                
			}
		}

	}


}
