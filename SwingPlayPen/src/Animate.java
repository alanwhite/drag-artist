import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.RepaintManager;
import javax.swing.Timer;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;


@SuppressWarnings("serial")
public class Animate extends JFrame implements ActionListener {

	private JPanel contentPane;
	private GamePanel1 gamePanel1;
	private GamePanel2 gamePanel2;
	private JLabel statusLabel;
	private long animeStart = 0;
	private Color textColor = Color.BLUE;
	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	private long pStartTime = 0;
	private GamePanel activePanel;

	private long tLatency = -1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Animate frame = new Animate();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Animate() {
		
		int freq = 1000/70;
		Timer timer = new Timer(freq, this);
		System.out.println("Timer set to "+freq+" microseconds");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder());
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panelButtons = new JPanel();
		contentPane.add(panelButtons, BorderLayout.NORTH);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				activePanel.getGameStats().resetPaintStats();
				activePanel.getAnimStats().resetPaintStats();
				statusLabel.setText("running");
				pStartTime = animeStart = System.currentTimeMillis();
				tLatency = -1;       
				timer.start();
			}
		});
		panelButtons.add(btnStart);

		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				statusLabel.setText("idle");
				activePanel.getGameStats().printPaintStats(System.currentTimeMillis() - pStartTime);
				activePanel.getAnimStats().printPaintStats(System.currentTimeMillis() - pStartTime);
				System.out.println("timer start latency "+tLatency+" ms");
			}
		});
		panelButtons.add(btnStop);

		JButton btnSwitch = new JButton("Switch");
		btnSwitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( timer.isRunning() ) {
					timer.stop();
					statusLabel.setText("idle");
					activePanel.getGameStats().printPaintStats(System.currentTimeMillis() - pStartTime);
					activePanel.getAnimStats().printPaintStats(System.currentTimeMillis() - pStartTime);
				}
				if ( activePanel == gamePanel1 ) {
					contentPane.remove(gamePanel1);
					contentPane.add(gamePanel2,BorderLayout.CENTER);
					activePanel = (GamePanel) gamePanel2;
				} else {
					contentPane.remove(gamePanel2);
					contentPane.add(gamePanel1,BorderLayout.CENTER);
					activePanel = gamePanel1;
				}
				resetTransparency();
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		panelButtons.add(btnSwitch);
		
		
		gamePanel1 = new GamePanel1();		
		gamePanel1.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				activePanel.resized();
			}
		});
		gamePanel1.setBorder(BorderFactory.createEmptyBorder());
		contentPane.add(gamePanel1, BorderLayout.CENTER);
		activePanel = gamePanel1;
		
		gamePanel2 = new GamePanel2();		
		gamePanel2.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				populate();
			}
		});
		gamePanel2.setBorder(BorderFactory.createEmptyBorder());
		
		// contentPane.add(gamePanel2, BorderLayout.CENTER);
		
		JPanel panelStatus = new JPanel();
		contentPane.add(panelStatus, BorderLayout.SOUTH);
		panelStatus.setLayout(new BoxLayout(panelStatus, BoxLayout.X_AXIS));

		JLabel label1 = new JLabel("Status:");
		label1.setFont(new Font("SansSerif", Font.PLAIN, 10));
		panelStatus.add(label1);

		statusLabel = new JLabel("idle");
		label1.setLabelFor(statusLabel);
		statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		panelStatus.add(statusLabel);

	}

	public JPanel getGamePanel() {
		return activePanel;
	}

	private void populate() {
		font = new Font("SansSerif", Font.PLAIN, activePanel.getHeight()/2);
	}

	/**
	 * Action listener for when the timer fires, it's job is to set up the variables needed 
	 * for the animation at the point in time it fires, then schedule a repaint
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ( tLatency == -1 ) {
			tLatency = System.currentTimeMillis() - pStartTime;
		}
		activePanel.animator();
	}
	
	private void resetTransparency() {
		textColor = new Color(Color.BLUE.getColorSpace(), Color.BLUE.getColorComponents(null),1.0f);
	}
	
	public JLabel getStatusLabel() {
		return statusLabel;
	}
	
	abstract class GamePanel extends JPanel {
		public abstract GameStats getGameStats();
		public abstract GameStats getAnimStats();
		public abstract void animator();
		public abstract void resized();
	}
	
	/**
	 * GamePanel1: uses no explicit image caching / flipping
	 * @author alanwhite
	 *
	 */
	class GamePanel1 extends GamePanel {
		
		private GameStats gameStats = new GameStats("GamePanel1 Paint");
		private GameStats animStats = new GameStats("GamePanel1 Anime");
		
	    public void paintComponent(Graphics g) {
	    	long before = System.nanoTime();
	        paintGame((Graphics2D) g);
	        gameStats.updatePaintStats(System.nanoTime() - before);
	    }

		/**
		 * Called when the panel needs to have it's contents redrawn
		 * @param g2D
		 */
		private void paintGame(Graphics2D g2D) {
			// redraw background
			g2D.setColor(Color.WHITE);
			g2D.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			g2D.setColor(textColor);
			
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			FontRenderContext frc = g2D.getFontRenderContext();
			TextLayout layout = new TextLayout("4", font, frc);

			Rectangle2D bounds = layout.getBounds();

			float x = ((float)this.getWidth()/2)-(float)bounds.getX()-((float)bounds.getWidth()/2);
			float y = (this.getHeight()/2)+(float)(bounds.getHeight()/2);			
			
			layout.draw(g2D, x, y);

		}

		public GameStats getGameStats() {
			return gameStats;
		}

		@Override
		public void animator() {
	    	long before = System.nanoTime();
			float transparency = 1.0f;
			long diff = System.currentTimeMillis() - animeStart;
			
			// full second, switch back
			if ( diff > 1000 ) {
				animeStart += 1000;
				diff = System.currentTimeMillis() - animeStart;
			}
			
			if ( diff > 500 ) {
				transparency = 0.0f;
			} else {
				transparency = (500.0f - ((float) diff)) / 500.0f;
			}
			
			textColor = new Color(Color.BLUE.getColorSpace(), Color.BLUE.getColorComponents(null),transparency);
	        animStats.updatePaintStats(System.nanoTime() - before);
			repaint();
		}

		public GameStats getAnimStats() {
			return animStats;
		}

		@Override
		public void resized() {
			font = new Font("SansSerif", Font.PLAIN, getHeight()/2);
			
		}
	}	
	
	/**
	 * GamePanel2: prepares images on the animation thread, and blits them on paint thread
	 * @author alanwhite
	 *
	 */
	class GamePanel2 extends GamePanel {
		
		private GameStats gameStats = new GameStats("GamePanel2 Paint");
		private GameStats animStats = new GameStats("GamePanel2 Anime");
		private BufferedImage cachedImage;
		
		private boolean backgroundPainted = false;
		private java.awt.Canvas canvas;
		private BufferStrategy bufferStrategy;
		
		public GamePanel2() {
			setLayout(new BorderLayout());
			

			
		}
		
	    public void paintComponent(Graphics g) {
	    	long before = System.nanoTime();
	        paintGame((Graphics2D) g);
	        gameStats.updatePaintStats(System.nanoTime() - before);
	    }

		/**
		 * Called when the panel needs to have it's contents redrawn
		 * @param g2D
		 */
		private void paintGame(Graphics2D g2D) {
			if ( cachedImage != null )
				g2D.drawImage(cachedImage, 0, 0, cachedImage.getWidth(), cachedImage.getHeight(), null);
		}

		public GameStats getGameStats() {
			return gameStats;
		}

		@Override
		public void animator() {
	    	long before = System.nanoTime();
	    	// populateImage();
	        animStats.updatePaintStats(System.nanoTime() - before);
			// repaint();
	        
//	        RepaintManager rm = RepaintManager.currentManager(this);
//	        boolean b = rm.isDoubleBufferingEnabled();
//	        rm.setDoubleBufferingEnabled(false);
	        
	        
	        Graphics2D g2D = null;
	        try {
	        	g2D = (Graphics2D) bufferStrategy.getDrawGraphics();

	        	if ( g2D == null ) {
	        		System.out.println("got a null graphics");
	        		return;
	        	}

	        	float transparency = 1.0f;
	        	long diff = System.currentTimeMillis() - animeStart;

	        	// full second, switch back
	        	if ( diff > 1000 ) {
	        		animeStart += 1000;
	        		diff = System.currentTimeMillis() - animeStart;
	        	}

	        	if ( diff > 500 ) {
	        		transparency = 0.0f;
	        	} else {
	        		transparency = (500.0f - ((float) diff)) / 500.0f;
	        	}

	        	textColor = new Color(Color.BLUE.getColorSpace(), Color.BLUE.getColorComponents(null),transparency);


	        	g2D.setColor(Color.WHITE);
	        	g2D.fillRect(0, 0, this.getWidth(), this.getHeight());

	        	g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        	FontRenderContext frc = g2D.getFontRenderContext();
	        	TextLayout layout = new TextLayout("4", font, frc);

	        	Rectangle2D bounds = layout.getBounds();

	        	float x = ((float)getWidth()/2)-(float)bounds.getX()-((float)bounds.getWidth()/2);
	        	float y = (getHeight()/2)+(float)(bounds.getHeight()/2);			


	        	g2D.setColor(textColor);
	        	layout.draw(g2D, x, y);
//	        	
//	        	g2D.setColor(Color.RED);
//	        	g2D.fillRect((int)x,(int)y-(int)bounds.getHeight(),(int)bounds.getWidth(),(int)bounds.getHeight());
	        	
	        } catch(Exception e) {
	        	e.printStackTrace();
	        } finally {
				g2D.dispose();
	        }
	        
	        bufferStrategy.show();
			Toolkit.getDefaultToolkit().sync();

//			  rm.setDoubleBufferingEnabled(b);
		}

		public GameStats getAnimStats() {
			return animStats;
		}

		@Override
		public void resized() {
			System.out.println("resize");
			font = new Font("SansSerif", Font.PLAIN, getHeight()/2);
			cachedImage = this.getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight());
			populateImage();
			
			if ( canvas == null ) {
				canvas = new Canvas();
				add(canvas, BorderLayout.CENTER);
				canvas.setIgnoreRepaint(true);
			}
			
			canvas.setSize(getSize());
			canvas.createBufferStrategy(2);
			bufferStrategy = canvas.getBufferStrategy();
		}
		
		private void populateImage() {
			float transparency = 1.0f;
			long diff = System.currentTimeMillis() - animeStart;
			
			// full second, switch back
			if ( diff > 1000 ) {
				animeStart += 1000;
				diff = System.currentTimeMillis() - animeStart;
			}
			
			if ( diff > 500 ) {
				transparency = 0.0f;
			} else {
				transparency = (500.0f - ((float) diff)) / 500.0f;
			}
			
			textColor = new Color(Color.BLUE.getColorSpace(), Color.BLUE.getColorComponents(null),transparency);
			
			Graphics2D g2D = cachedImage.createGraphics();
			g2D.setColor(Color.WHITE);
			g2D.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			g2D.setColor(textColor);
			
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			FontRenderContext frc = g2D.getFontRenderContext();
			TextLayout layout = new TextLayout("4", font, frc);

			Rectangle2D bounds = layout.getBounds();

			float x = ((float)getWidth()/2)-(float)bounds.getX()-((float)bounds.getWidth()/2);
			float y = (getHeight()/2)+(float)(bounds.getHeight()/2);			
			
			layout.draw(g2D, x, y);
			g2D.dispose();
		}

		@Override
		public void setBounds(int x, int y, int width, int height) {
			super.setBounds(x, y, width, height);
			resized();
		}

	}
	
	class GameStats {
		// stats
		private long pMin = 0;
		private long pAvg = 0;
		private long pMax = 0;
		private long pCnt = 0;
		private long pTot = 0;
		private String name = "";
		
		public GameStats(String name) {
			this.name = name;
		}
		
		public void resetPaintStats() {
			pMin = Long.MAX_VALUE;
			pMax = 0;
			pAvg = 0;
			pCnt = 0;
			pTot = 0;
		}
		
		public void updatePaintStats(long duration) {
			if ( duration < pMin )
				pMin = duration;
		
			if ( duration > pMax )
				pMax= duration;
			
			pCnt++;
			
			pTot += duration;
		}
		
		public void printPaintStats(long duration) {
			if ( pCnt == 0 ) {
				System.out.println(name+" no paints");
				return;
			}
			
			pAvg = pTot / pCnt;
			
			float fps = ((float) pCnt / (float) duration) * 1000.0f;
			System.out.println(name+" Stats min/avg/max "+pMin+"/"+pAvg+"/"+pMax+" nanoseconds for "+pCnt+" frames in "+
							duration/1000+" seconds ("+fps+" fps)");
		}
		
	}
}