import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Scaley extends JFrame {

	public Scaley() {
		super();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600,400));
		this.getContentPane().setLayout(new BorderLayout());

		JLabel label = new JLabel();
		label.setText("test");
		label.setIcon(new ReverseRight1());
		this.getContentPane().add(label,BorderLayout.NORTH);
		this.getContentPane().setBackground(Color.WHITE);
		
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Scaley();
			}
		});
	}

}

class ReverseRight1 implements Icon {

    /** The width of this icon. */
    private int width;

    /** The height of this icon. */
    private int height;

    /** The rendered image. */
    private BufferedImage image;

    /**
     * Creates a new transcoded SVG image.
     */
    public ReverseRight1() {
        this(1590, 1590);
    }

    /**
     * Creates a new transcoded SVG image.
     */
    public ReverseRight1(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (image == null) {
            image = new BufferedImage(getIconWidth(), getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            double coef = Math.min((double) width / (double) 1590, (double) height / (double) 1590);
            
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.scale(coef, coef);
            paint(g2d);
            g2d.dispose();
        }
        
        g.drawImage(image, x, y, null);
    }

    /**
     * Paints the transcoded SVG image on the specified graphics context.
     * 
     * @param g Graphics context.
     */
    private static void paint(Graphics2D g) {
        Shape shape = null;
        
        float origAlpha = 1.0f;
        
        java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();
        

        // 

        // _0

        transformations.push(g.getTransform());
        g.transform(new AffineTransform(1.25f, 0, 0, -1.25f, 0, 180));

        // _0_0

        // _0_0_0

        // _0_0_1

        // _0_0_1_0

        // _0_0_1_0_0
        transformations.push(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, 130.5f, 72));

        // _0_0_1_0_0_0

        // _0_0_1_0_0_0_0
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(0.0, 0.0);
        ((GeneralPath) shape).curveTo(0.0, 32.309, -26.191, 58.5, -58.5, 58.5);
        ((GeneralPath) shape).curveTo(-90.809, 58.5, -117.0, 32.309, -117.0, 0.0);
        ((GeneralPath) shape).curveTo(-117.0, -32.309, -90.809, -58.5, -58.5, -58.5);
        ((GeneralPath) shape).curveTo(-26.191, -58.5, 0.0, -32.309, 0.0, 0.0);
        ((GeneralPath) shape).closePath();

        g.setPaint(new Color(0x231F20));
        g.setStroke(new BasicStroke(10, 0, 0, 10));
        g.draw(shape); // the circle

        g.setTransform(transformations.pop()); // _0_0_1_0_0_0

        transformations.push(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, 30.664f, 30.664f));

        // _0_0_1_0_0_1

        // _0_0_1_0_0_1_0
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(0.0, 0.0);
        ((GeneralPath) shape).lineTo(82.672, 82.672);

        g.draw(shape); // dodgy line

        g.setTransform(transformations.pop()); // _0_0_1_0_0_1

        transformations.push(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, 30.664f, 113.336f));

        // _0_0_1_0_0_2

        // _0_0_1_0_0_2_0
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(0.0, 0.0);
        ((GeneralPath) shape).lineTo(82.672, -82.672);

        g.draw(shape);

        g.setTransform(transformations.pop()); // _0_0_1_0_0_2

        g.setTransform(transformations.pop()); // _0_0

    }

}

class Ruler extends JComponent {

    private static final long serialVersionUID = 1L;

    public static final Color COLOR = Color.decode("#C0FF3E");

    public static final int PIXELS_PER_INCH = Toolkit.getDefaultToolkit().getScreenResolution();
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int SIZE = 18;

    public int orientation;
    private boolean isMetric;
    // private float increment;
    private float unitSize;
    private int indicatorValue = -1;
    private float ticksPerUnit;

    private Graphics bufferGraphics;
    private Image bufferImage;

    private double zoomLevel = 1d;

    private double cmSpacing;
    private double inSpacing;

    public Ruler(int orientation, boolean isMetric) {
            this(orientation, isMetric, 0, 0);
    }

    public Ruler(int orientation, boolean isMetric, double cmSpacing, double inSpacing) {
            this.orientation = orientation;
            this.isMetric = isMetric;
            this.cmSpacing = cmSpacing;
            this.inSpacing = inSpacing;
            setIncrementAndUnits();

            addComponentListener(new ComponentAdapter() {

                    @Override
                    public void componentResized(ComponentEvent e) {
                            bufferImage = new BufferedImage(e.getComponent().getWidth(), e.getComponent()
                                            .getHeight(), BufferedImage.TYPE_INT_RGB);
                            bufferGraphics = bufferImage.getGraphics();
                    }
            });
    }

    public void setZoomLevel(double zoomLevel) {
            this.zoomLevel = zoomLevel;
            setIncrementAndUnits();
            repaint();
    }

    public void setIsMetric(boolean isMetric) {
            this.isMetric = isMetric;
            setIncrementAndUnits();
            repaint();
    }

    /**
     * Changes cursor position. If less than zero, indication will not be
     * rendered. For horizontal ruler this should be X coordinate of mouse
     * position, and Y for vertical.
     * 
     * @param indicatortValue
     */
    public void setIndicatorValue(int indicatortValue) {
            this.indicatorValue = indicatortValue;
    }

    private void setIncrementAndUnits() {
            if (isMetric) {
                    unitSize = (float) ((cmSpacing == 0 ? PIXELS_PER_INCH / 2.54f : cmSpacing) * zoomLevel);
                    ticksPerUnit = 4;
            } else {
                    ticksPerUnit = 10;
                    unitSize = (float) ((inSpacing == 0 ? (PIXELS_PER_INCH) : inSpacing) * zoomLevel);
            }
            // ticksPerUnit = 1;
            // while (unitSize / ticksPerUnit > 48) {
            // ticksPerUnit *= 2;
            // }
            // while (unitSize / ticksPerUnit < 24) {
            // ticksPerUnit /= 2;
            // }
    }

    public boolean isMetric() {
            return this.isMetric;
    }

    public void setPreferredHeight(int ph) {
            setPreferredSize(new Dimension(SIZE, ph));
    }

    public void setPreferredWidth(int pw) {
            setPreferredSize(new Dimension(pw, SIZE));
    }

    protected void paintComponent(Graphics g) {
            if (bufferGraphics == null) {
                    return;
            }
            Rectangle clipRect = g.getClipBounds();

            bufferGraphics.setColor(COLOR);
            bufferGraphics.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);

            // Do the ruler labels in a small font that's black.
            bufferGraphics.setFont(new Font("SansSerif", Font.PLAIN, 10));
            bufferGraphics.setColor(Color.black);

            // Some vars we need.
            float start = 0;
            int tickLength = 0;
            String text = null;
            // int count;
            float increment = unitSize / ticksPerUnit;

            // Use clipping bounds to calculate first and last tick locations.
            int firstUnit;
            if (orientation == HORIZONTAL) {
                    firstUnit = Math.round(clipRect.x / unitSize);
                    start = (int) (clipRect.x / unitSize) * unitSize;
                    // count = Math.round(clipRect.width / increment) + 1;
            } else {
                    firstUnit = Math.round(clipRect.y / unitSize);
                    start = (int) (clipRect.y / unitSize) * unitSize;
                    // count = Math.round(clipRect.height / increment) + 1;
            }

            // ticks and labels
            int x = 0;
            int i = 0;
            while (x < (orientation == HORIZONTAL ? (clipRect.x + clipRect.width)
                            : (clipRect.y + clipRect.height))) {
                    if ((ticksPerUnit <= 1) || (i % Math.round(ticksPerUnit) == 0)) {
                            tickLength = 10;
                            text = Integer.toString(firstUnit + Math.round(i / ticksPerUnit));
                    } else {
                            tickLength = 7;
                            if (isMetric) {
                                    tickLength -= 2 * (i % Math.round(ticksPerUnit) % 2);
                            } else if (i % Math.round(ticksPerUnit) != 5) {
                                    tickLength -= 2;
                            }
                            text = null;
                    }

                    x = (int) (start + i * increment);

                    if (tickLength != 0) {
                            if (orientation == HORIZONTAL) {
                                    bufferGraphics.drawLine(x, SIZE - 1, x, SIZE - tickLength - 1);
                                    if (text != null) {
                                            bufferGraphics.drawString(text, x + 2, 15);
                                    }
                            } else {
                                    bufferGraphics.drawLine(SIZE - 1, x, SIZE - tickLength - 1, x);
                                    if (text != null) {
                                            FontMetrics fm = bufferGraphics.getFontMetrics();
                                            bufferGraphics.drawString(text, SIZE
                                                            - (int) fm.getStringBounds(text, bufferGraphics).getWidth() - 2,
                                                            x + 10);
                                    }
                            }
                    }
                    i++;
            }

            // highlight value
            if (indicatorValue >= 0) {
                    bufferGraphics.setColor(Color.red);
                    if (orientation == HORIZONTAL) {
                            if (indicatorValue < getWidth()) {
                                    bufferGraphics.drawLine(indicatorValue, 0, indicatorValue, SIZE - 1);
                            }
                    } else {
                            if (indicatorValue < getHeight()) {
                                    bufferGraphics.drawLine(0, indicatorValue, SIZE - 1, indicatorValue);
                            }
                    }
            }

            // lines
            bufferGraphics.setColor(Color.black);
            if (orientation == HORIZONTAL) {
                    bufferGraphics.drawLine(0, SIZE - 1, getWidth(), SIZE - 1);
            } else {
                    bufferGraphics.drawLine(SIZE - 1, 0, SIZE - 1, getHeight());
            }
            // Draw the buffer
            g.drawImage(bufferImage, 0, 0, this);
    }

    @Override
    public void update(Graphics g) {
    	paint(g);
    }
}

