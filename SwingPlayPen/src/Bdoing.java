import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class Bdoing extends JPanel implements ActionListener {
	
	private static final double FPS = 85;
	private static final double BPM = 78;
	private static final double DURATION = 60.0f/BPM; // how many seconds each bounce should take
	private static final int BOUNCES = 8; // how many bounces
	private static final int LINES = 3;
	private static final double MARGIN = 0.05f;

	private double ballRadius = 10;
	private double ballX, ballY;
	private QuadCurve2D q = new QuadCurve2D.Double();
	private Ellipse2D b = new Ellipse2D.Double();
	private double progress = 0;
	private int bounceIndex = 0;
	
	private List<BeatPoint> beatNotes = new ArrayList<BeatPoint>();
	private List<Rectangle2D.Double> lines = new ArrayList<Rectangle2D.Double>();
	
	private class BeatPoint {
		public double x1, x2, ctrlx, ctrly, y1, y2;
		public QuadCurve2D q = new QuadCurve2D.Double();
	}
	
	public Bdoing() {
		addComponentListener(resizeListener);
		setBackground(Color.WHITE);
		Timer timer = new Timer((int)(1000.0f/FPS), this);
		timer.setInitialDelay(2000);
		timer.start(); 
	}

	private ComponentListener resizeListener = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			double lineHeight = (double)getHeight() / LINES;
			lines.clear();
			beatNotes.clear();
			
			for ( int l=1; l<=LINES; l++ ) {
				
				double width = (double)getWidth() * (1.0f - (2*MARGIN));
				double margin = (double)getWidth() * MARGIN;
				Rectangle2D.Double line = new Rectangle2D.Double(margin, ((double)getHeight()*((double)l/(double)LINES)), width, 1);
				lines.add(line);

				double xLanding = margin + ballRadius;
				for ( int i=1; i<=BOUNCES; i++ ) {
					double lineWidth = width - (2 * ballRadius);
					double bouncePercentage = (double) i / BOUNCES;
					Point2D bouncePoint = new Point2D.Double(ballRadius+(bouncePercentage*lineWidth), line.getMinY() - ballRadius);
					BeatPoint bp = new BeatPoint();
					bp.x1 = xLanding;
					bp.x2 = bouncePoint.getX();
					bp.y1 = bouncePoint.getY() - ballRadius;
					bp.y2 = bouncePoint.getY() - ballRadius;
					bp.ctrlx = bp.x1 + ((bp.x2 - bp.x1) * 0.35f);
					bp.ctrly = bp.y1 - lineHeight;
					bp.q.setCurve(bp.x1, bp.y1, bp.ctrlx, bp.ctrly, bp.x2, bp.y2);
					beatNotes.add(bp);
					xLanding = bp.x2;
				}
			}
		}
	};
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		/**
		 * We increment the progress when the timer fires to be able to build the %age along 
		 * If we reach the end of the time a bounce should take we reset the progress to be able 
		 * to calculate the correct x,y on the quadratic curve.
		 * 
		 * Each time we reset the progress, because it's time to process the next bounce, we have to
		 * increment the bounceIndex to point to the next curve in the sequence.
		 * 
		 * What we need to add in, is when we've hit the last bounce point on a line, is to run half a curve
		 * to the end of the window, and then half a curve in on the next line!
		 * 
		 * So we need to make sure that when the points are set up they include some half-point indicator
		 * and then down here on the timer, we understand that and adapt to use the second half at the
		 * right point.
		 */
		
		/**
		 * So this is the bit where we decide if we're still in the same beat, here we need to note when 
		 * we should switch bounceIndex entries to take into account the half beat piece
		 * 
		 * Something like if we're on a half beat only check for half the duration and if so 
		 * take a switch on the beat points
		 */
		if ( progress++ >= (DURATION * FPS)) {
			progress = 1;
			if ( ++bounceIndex >= beatNotes.size() )
				bounceIndex=0;
		}
		
		double t = progress / (DURATION * FPS); // how far we are along between the points as a %age
		
		BeatPoint bp = beatNotes.get(bounceIndex);
		
		// complicated maths bit courtesy of Wikipedia 
		ballX = (1 - t) * (1 - t) * bp.x1 + 2 * (1 - t) * t * bp.ctrlx + t * t * bp.x2;
		ballY = (1 - t) * (1 - t) * bp.y1 + 2 * (1 - t) * t * bp.ctrly + t * t * bp.y2;

		b.setFrame(ballX-ballRadius, ballY-ballRadius, 2*ballRadius, 2*ballRadius);
		
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(Color.BLACK);
		
		for ( Rectangle2D.Double line : lines ) 
			g2.fill(line);
		
		for ( BeatPoint bp: beatNotes ) 
			g2.draw(bp.q);
		
		g2.setColor(Color.RED);
		g2.fill(b);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {		
				JFrame frame = new JFrame("Bdoing");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setPreferredSize(new Dimension(640, 480));
				frame.setLocationByPlatform(true);
				frame.add(new Bdoing());
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

}

