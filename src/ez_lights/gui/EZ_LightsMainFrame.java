package ez_lights.gui;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import ez_lights.device.Device;


public class EZ_LightsMainFrame extends javax.swing.JFrame {
	private JButton CloseButton;
	private AbstractAction closeAboutAction;
	private ControllableDevicePanel controllableDevicePanel;
	private JTextField jTextField1;
	private JButton OKButton;
	private JDialog AboutDialog;
	private AbstractAction aboutAction;
	private JMenuItem jMenuItem1;
	private JMenu HelpMenu;
	private JMenu jMenu1;
	private JMenuBar jMenuBar1;

//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				EZ_LightsMainFrame myFrame = new EZ_LightsMainFrame();
//				myFrame.setLocationRelativeTo(null);
//				myFrame.setVisible(true);
//			}
//		});
//	}

	public EZ_LightsMainFrame(List<Device> devices) {
		super("EZ_Lights");
		initGUI(devices);
	}

	private void initGUI(List<Device> devices) {
//		List<ControllableDevicePanel> controllableDevicePanels = new ArrayList<ControllableDevicePanel>();
		
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			jMenuBar1 = new JMenuBar();
			setJMenuBar(jMenuBar1);
			{
				jMenu1 = new JMenu();
				jMenuBar1.add(jMenu1);
				jMenu1.setText("File");

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
			//		CloseButton.setAction(getCloseMainFrame());
			CloseButton.setAction(new AbstractAction("Close", null) {
				public void actionPerformed(ActionEvent evt) {
					setVisible(false);
					dispose();
					System.exit(0);
				}
			});
			
			 GroupLayout.SequentialGroup sequentialGroup = 	thisLayout.createSequentialGroup()
				.addContainerGap();
			 GroupLayout.ParallelGroup parallelGroup = thisLayout.createParallelGroup(); 
			 
			for (Device d : devices) {
				controllableDevicePanel = new ControllableDevicePanel(d.getName());
				sequentialGroup.addComponent(controllableDevicePanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE);
				parallelGroup.addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
						.addComponent(controllableDevicePanel, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
						.addGap(0, 66, Short.MAX_VALUE));			
			}

			sequentialGroup.addGap(10);
			sequentialGroup.addComponent(CloseButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
			sequentialGroup.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);			
			thisLayout.setVerticalGroup(sequentialGroup);
			
			parallelGroup.addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
					.addGap(293)
					.addComponent(CloseButton, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
					.addGap(0, 0, Short.MAX_VALUE));
			
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
				public void actionPerformed(ActionEvent evt) {
					getAboutDialog().dispose();
				}
			};
		}
		return closeAboutAction;
	}

	/*	private AbstractAction getCloseMainFrame() {
		if(closeMainFrame == null) {
			closeMainFrame = new AbstractAction("Close", null) {
				public void actionPerformed(ActionEvent evt) {
					setVisible(false);
					dispose();
					System.exit(0);
				}
			};
		}
		return closeMainFrame;
	}
	 */

}
