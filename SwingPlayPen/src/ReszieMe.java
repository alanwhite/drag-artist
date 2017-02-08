import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class ReszieMe extends JPanel {

	public ReszieMe() {
		setLayout(new BorderLayout());
		addComponentListener(resizeListener);
		
		JPanel fred = new JPanel();
		fred.setLayout(null);
		add(fred, BorderLayout.CENTER);
		fred.addComponentListener(resizeListener);
	}

	private ComponentListener resizeListener = new ComponentAdapter() {

		@Override
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);
			System.out.println("Resize event for "+e.getSource());
		}
		
	};
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {		
				JFrame frame = new JFrame("ResizeMe");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setPreferredSize(new Dimension(640, 480));
				frame.setLocationByPlatform(true);
				frame.add(new ReszieMe());
				frame.pack();
				frame.setVisible(true);
			}
		});

	}

}
