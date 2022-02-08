import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
public class Toggletastic {

	public Toggletastic() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}




class ToggleSwitch extends JPanel {
    private boolean activated = false;
    private Color switchColor = new Color(200, 200, 200), buttonColor = new Color(255, 255, 255), borderColor = new Color(50, 50, 50);
    private Color activeSwitch = new Color(0, 125, 255);
    private BufferedImage puffer;
    private Graphics2D g;
    public ToggleSwitch() {
        super();
        setVisible(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                activated = !activated;
                repaint();
            }
        });
        setBounds(0, 0, 41, 21);
    }
    @Override
    public void paint(Graphics gr) {
        if(g == null) {
            puffer = (BufferedImage) createImage(getWidth(), getHeight());
            g = (Graphics2D)puffer.getGraphics();
            RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHints(rh);
        }
        g.setColor(activated?activeSwitch:switchColor);
        g.fillRoundRect(0, 0, 40, 20, 5, 5);
        g.setColor(borderColor);
        g.drawRoundRect(0, 0, 40, 20, 5, 5);
        g.setColor(buttonColor);
        if(activated) {
            g.fillRoundRect(21, 1, 18, 18,  5, 5);
            g.setColor(borderColor);
            g.drawRoundRect(20, 0, 20, 20, 5, 5);
        }
        else {
            g.fillRoundRect(1, 1, 18, 18,  5, 5);
            g.setColor(borderColor);
            g.drawRoundRect(0, 0, 20, 20, 5, 5);
        }

        gr.drawImage(puffer, 0, 0, null);
    }
    public boolean isActivated() {
        return activated;
    }
    public void setActivated(boolean activated) {
        this.activated = activated;
    }
    public Color getSwitchColor() {
        return switchColor;
    }
    /**
     * Unactivated Background Color of switch
     */
    public void setSwitchColor(Color switchColor) {
        this.switchColor = switchColor;
    }
    public Color getButtonColor() {
        return buttonColor;
    }
    /**
     * Switch-Button color
     */
    public void setButtonColor(Color buttonColor) {
        this.buttonColor = buttonColor;
    }
    public Color getBorderColor() {
        return borderColor;
    }
    /**
     * Border-color of whole switch and switch-button
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }
    public Color getActiveSwitch() {
        return activeSwitch;
    }
    public void setActiveSwitch(Color activeSwitch) {
        this.activeSwitch = activeSwitch;
    }
}
