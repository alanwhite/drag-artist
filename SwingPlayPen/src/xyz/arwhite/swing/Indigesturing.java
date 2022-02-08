package xyz.arwhite.swing;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.apple.eawt.event.GestureAdapter;
import com.apple.eawt.event.GesturePhaseEvent;
import com.apple.eawt.event.GestureUtilities;
import com.apple.eawt.event.MagnificationEvent;
import com.apple.eawt.event.RotationEvent;
import com.apple.eawt.event.SwipeEvent;

/*
 * To make this work ensure on jdk17.02+2 onwards
 * Compile with -XDignore.symbol.file --add-exports java.desktop/com.apple.eawt.event=ALL-UNNAMED 
 * Run with --add-opens java.desktop/com.apple.eawt.event=ALL-UNNAMED 
 */

@SuppressWarnings("serial")
public class Indigesturing extends JFrame {

	public Indigesturing() throws HeadlessException {
		super();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600,400));
		
		JPanel p = new JPanel();
		getContentPane().add(p);
		
		GestureAdapter ga = new GestureAdapter() {

			@Override
		    public void magnify(final MagnificationEvent e) { System.out.println(e); }
			
			@Override
		    public void rotate(final RotationEvent e) { System.out.println(e); }
			
			// these don't work still ...
			// https://bugs.openjdk.java.net/browse/JDK-8154865
			// probably because they're consumed and translated to mouse/wheel events
			
			@Override
		    public void gestureBegan(final GesturePhaseEvent e) { System.out.println(e); }
			
			@Override
		    public void gestureEnded(final GesturePhaseEvent e) { System.out.println(e); }
			
			@Override
		    public void swipedDown(final SwipeEvent e) { System.out.println(e); }
			
			@Override
		    public void swipedLeft(final SwipeEvent e) { System.out.println(e); }
			
			@Override
		    public void swipedRight(final SwipeEvent e) {System.out.println(e); }
			
			@Override
		    public void swipedUp(final SwipeEvent e) { System.out.println(e); }
		};
		
 		GestureUtilities.addGestureListenerTo(p, ga);
		
//		GestureUtilities.addGestureListenerTo(p, new MagnificationListener() { 
//            @Override 
//            public void magnify(MagnificationEvent magnificationEvent) { 
//                System.out.println("Magnify "+magnificationEvent.getMagnification());
//            } 
//        });
		
		final Object value = p.getClientProperty("com.apple.eawt.event.internalGestureHandler");
		System.out.println("value "+value);
	
		var ma = new MouseAdapter() {

			/*
			 * 2-finger swipe is a mouse wheel scroll
			 * modifier/ext-modifier of shift is supplied if horizontal, otherwise vertical
			 * wheelRotation is positive for up/left swipes, negative for down/right, preciseWheelRotation follows, almost always updates, even if wheelRotation 0
			 * using the touchpad appears to use some momentum algo under the covers as events can keep coming after fingers have left contact with the pad
			 * not evident how to tell when contact has been broken, rotation reduces as moment reduces
			 * 
			 * This bug is the only place I've seen that documents the shift behaviour https://bugs.openjdk.java.net/browse/JDK-8203048
			 */
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) { 
				System.out.println(e); 
			}

			/*
			 * 3-finger swipe is a drag event, reported as Button1 being pressed, even if no click down has been made
			 * There is no difference between a 3-finger drag whilst clicked/pressed into the pad, and not
			 * 
			 * 2-finger click and drag, is reported as a drag event with cmd+Button3 modifiers
			 * 2-finger swipe, ie no click, is a mouse scroll event, see above
			 * 
			 * 1-finger click and drag, is a drag and reports as Button1 pressed, exactly the same as a 3-finger swipe, just no button down event before (I imagine)
			 * 
			 * Direction is always determined by the co-ordinate move. There is no concept of momentum as this event relies on the co-ordinates of the pointer.
			 */
			@Override
			public void mouseDragged(MouseEvent e) { System.out.println(e); }
			
		};
		
		p.addMouseListener(ma);
		p.addMouseMotionListener(ma);
		p.addMouseWheelListener(ma);
		
		pack();
		setVisible(true);

	}
	
	public static void main(String[] args) {
		String os = System.getProperty("os.name");
		if ( !os.startsWith("Mac")) {
			System.out.println("OS is "+os+", this is all about gesture on a mac");
			return;
		}
		
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new Indigesturing();
            }
        });
	}
}
