import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Utility for finding out all about the physical screen environment
 * as per Java
 * 
 * @author alanwhite
 *
 */
public class DeviceUtility extends JFrame {

	int res;//store screen resolution here
	int width;//store screen width here
	int height;//store screen height here

	public DeviceUtility() {
		//Get screen resolution, width, and height
		res = Toolkit.getDefaultToolkit().
				getScreenResolution();
		width = Toolkit.getDefaultToolkit().
				getScreenSize().width;
		height = Toolkit.getDefaultToolkit().
				getScreenSize().height;

		//Display screen resolution,                                 
		System.out.println(res + " pixels per inch");
		System.out.println(width + " pixels wide");
		System.out.println(height + " pixels high");

		//Set Frame size to two-inch by two-inch
		this.setSize(2*res,2*res);
		this.setVisible(true);
		this.setTitle("Blah Wibble Hatstand");

	    for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment ().getScreenDevices()) {
	    	System.out.println("Device: "+gd.getIDstring());
	    	for (GraphicsConfiguration gc : gd.getConfigurations() ) {
	    		System.out.println(gc.getColorModel());
	    	}
	    }
		
		//Window listener to terminate program.
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);}});
	}

	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;

		g2.draw(new Rectangle2D.Double(
				res*0.5,res*0.5,res*1.0,res*1.0));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new DeviceUtility();
			}

		});

	}

}
