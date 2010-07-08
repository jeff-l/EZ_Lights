package ez_lights.gui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class EZ_LightsMainFrame extends javax.swing.JFrame {
	private JButton CloseButton;
	private JButton OnButton;
	private JTextField DeviceName;
	private AbstractAction closeMainFrame;
	private AbstractAction closeAboutAction;
	private JPanel jPanel1;
	private JTextField jTextField1;
	private JButton OKButton;
	private JDialog AboutDialog;
	private AbstractAction aboutAction;
	private JMenuItem jMenuItem1;
	private JMenu HelpMenu;
	private JMenu jMenu1;
	private JMenuBar jMenuBar1;
	private JButton OffButton;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				EZ_LightsMainFrame inst = new EZ_LightsMainFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public EZ_LightsMainFrame() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(jMenuBar1);
				{
					jMenu1 = new JMenu();
					jMenuBar1.add(jMenu1);
					jMenu1.setText("File");
				}
				{
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
			}
			{
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
			}
			{
				jPanel1 = new JPanel();
				GroupLayout jPanel1Layout = new GroupLayout((JComponent)jPanel1);
				jPanel1.setLayout(jPanel1Layout);
				{
					DeviceName = new JTextField();
					DeviceName.setText("Device 1");
				}
				{
					OnButton = new JButton();
					OnButton.setText("On");
					OnButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							System.out.println(DeviceName.getText() + " On button pushed.");
						}
					});					
				}
				{
					OffButton = new JButton();
					OffButton.setText("Off");
					OffButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							System.out.println(DeviceName.getText() + " Off button pushed.");
						}
					});
				}
				jPanel1Layout.setVerticalGroup(jPanel1Layout.createSequentialGroup()
						.addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(DeviceName, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(OnButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(OffButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
				jPanel1Layout.setHorizontalGroup(jPanel1Layout.createSequentialGroup()
						.addGroup(jPanel1Layout.createSequentialGroup()
								.addComponent(DeviceName, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(jPanel1Layout.createSequentialGroup()
										.addComponent(OnButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(jPanel1Layout.createSequentialGroup()
												.addComponent(OffButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)));
			}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
				.addGap(201)
				.addComponent(CloseButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 66, Short.MAX_VALUE))
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addGap(293)
				        .addComponent(CloseButton, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 0, Short.MAX_VALUE)))
				.addGap(7));
			pack();
			this.setSize(396, 322);
		} catch (Exception e) {
		    //add your error handling code here
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
