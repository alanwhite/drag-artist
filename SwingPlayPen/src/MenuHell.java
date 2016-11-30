import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class MenuHell extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MenuHell() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
		setTitle("Menu Hell");
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu menu = new JMenu("Fred");
		menuBar.add(menu);
		TCheckBoxMenuItem menuItem = new TCheckBoxMenuItem("Test 1");
		menu.add(menuItem);
		JMenuItem menuItem1 = new JMenuItem("que?");
		menu.add(menuItem1);
		JCheckBoxMenuItem menuItem2 = new JCheckBoxMenuItem("Test Normal");
		menu.add(menuItem2);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {			
				new MenuHell();
			}

		});

	}



}

class TCheckBoxMenuItem extends JCheckBoxMenuItem implements Icon, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static boolean MIDasSELECTED = true;  //consider mid-state as selected ?


	public TCheckBoxMenuItem() { this(""); }

	public TCheckBoxMenuItem(String text) {
		super(text);
		putClientProperty("SelectionState", 0);
		setIcon(this);
		addActionListener(this);
	}

	public TCheckBoxMenuItem(String text, int sel) {
		/* tri-state checkbox has 3 selection states:
		 * 0 unselected
		 * 1 mid-state selection
		 * 2 fully selected
		 */
		super(text, sel > 1 ? true : false);

		switch (sel) {
		case 2: setSelected(true);
		case 1:
		case 0:
			putClientProperty("SelectionState", sel);
			break;
		default:
			throw new IllegalArgumentException();
		}
		addActionListener(this);
		setIcon(this);
	}

	@Override
	public boolean isSelected() {
		if (MIDasSELECTED && (getSelectionState() > 0)) return true;
		else return super.isSelected();
	}

	public int getSelectionState() {
		return (getClientProperty("SelectionState") != null ? (int)getClientProperty("SelectionState") :
			super.isSelected() ? 2 :
				0);
	}

	public void setSelectionState(int sel) {
		switch (sel) {
		case 2: setSelected(true);
		break;
		case 1: 
		case 0: setSelected(false);
		break;
		default: throw new IllegalArgumentException();
		}
		putClientProperty("SelectionState", sel);
	}


	final static Icon icon = UIManager.getIcon("CheckBox.icon");

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		icon.paintIcon(c, g, x, y);
		if (getSelectionState() != 1) return;

		int w = getIconWidth();
		int h = getIconHeight();
		g.setColor(c.isEnabled() ? new Color(51, 51, 51) : new Color(122, 138, 153));
		g.fillRect(x+4, y+4, w-8, h-8);

		if (!c.isEnabled()) return;
		g.setColor(new Color(81, 81, 81));
		g.drawRect(x+4, y+4, w-9, h-9);
	}

	@Override
	public int getIconWidth() {
		return icon.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return icon.getIconHeight();
	}

	public void actionPerformed(ActionEvent e) {
		TCheckBoxMenuItem tcb = (TCheckBoxMenuItem)e.getSource();
		if (tcb.getSelectionState() == 0)
			tcb.setSelected(false);

		tcb.putClientProperty("SelectionState", tcb.getSelectionState() == 2 ? 0 :
			tcb.getSelectionState() + 1);

		// test
		System.out.println(">>>>IS SELECTED: "+tcb.isSelected());
		System.out.println(">>>>IN MID STATE: "+(tcb.getSelectionState()==1));
	}
}
