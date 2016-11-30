import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class MoreMenuHell extends JFrame {
	
	JPopupMenu menu = new JPopupMenu("Fred");
	private JCheckBoxMenuItem menuItem1 = new JCheckBoxMenuItem("Que?");
	private JCheckBoxMenuItem menuItem2 = new JCheckBoxMenuItem("Que2");
	private ButtonGroup bg = new ButtonGroup();
	
	public MoreMenuHell() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
		
		this.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				menu.show(e.getComponent(), e.getX(), e.getY());
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}});
		

		menu.add(menuItem1);

		menu.add(menuItem2);

		bg.add(menuItem1);
		bg.add(menuItem2);
		
		menuItem1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				System.out.println("State Changed "+ 
					(e.getStateChange() == ItemEvent.SELECTED ? "selected" : "deselected"));
			}
			
		});
		
		JMenuItem trigger = new JMenuItem(new AbstractAction("Trigger") {
			public void actionPerformed(ActionEvent e) {
//				menuItem1.setSelected(false);
//				menuItem2.setSelected(false);
				bg.clearSelection();

				
				// do some work and establish we need to set it true
				// menuItem1.setSelected(true);

			}});
		menu.add(trigger);
		
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, 	
											IllegalAccessException, UnsupportedLookAndFeelException {
		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {			
				new MoreMenuHell();
			}
		});
	}
}

