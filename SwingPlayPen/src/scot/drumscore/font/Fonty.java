package scot.drumscore.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Fonty extends JFrame {

	private static Logger logger = Logger.getLogger(Fonty.class.getName());
	
	private Font music, warmedMusic;
	private JSONObject indexJSON; 
	
	public Fonty() {
		 setTitle("Fonty Fluff");
	        setSize(500, 300);
	        setLocationRelativeTo(null);
	        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	        loadResources();
	     
	        add(new MyCanvas());
	        
	        // pack();
	        setVisible(true);
	}

	private void loadResources() {
		 try {
		        InputStream is = Fonty.class.getResourceAsStream("Bravura.otf");
		        music = Font.createFont(Font.TRUETYPE_FONT, is);
		        warmedMusic = music.deriveFont(120.0f);
		        
		        JSONParser parser = new JSONParser();
		        indexJSON = (JSONObject) parser.parse(new InputStreamReader(Fonty.class.getResourceAsStream("bravura_metadata.json")));
		        
		        JSONObject constants = (JSONObject) indexJSON.get("engravingDefaults");
		        Set keys = constants.keySet();
		        
		        for ( Object key : keys ) {
		        	System.out.println(key+" "+constants.get(key));
		        }
		        
		    } catch (FontFormatException | IOException | ParseException ex) {
		        logger.log(Level.SEVERE, null, ex);
		    }
	}
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              	new Fonty();
            }
        });

	}
	
	class MyCanvas extends JComponent {
		  public void paint(Graphics g) {
		    Graphics2D g2 = (Graphics2D)g;

		    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		    
		    final char brace = '\uE000';
		    final char staff5 = '\ue01a';
		    final char staff1LineWide = '\uE016';
		    final char noteheadHalf = '\uE0A3';
		    final char graceNoteAcciaccaturaStemUp = '\uE560';
		    final char timeSig2 = '\uE082';
		    final char augmentationDot = '\ue1e7';
		    
		    char [] c = { brace, staff5, staff1LineWide, staff5 };
		    char [] d = { noteheadHalf, noteheadHalf, graceNoteAcciaccaturaStemUp, noteheadHalf, augmentationDot, timeSig2 };
		    
		    
		    long begin1 = System.nanoTime();
		    Font m50 = warmedMusic.deriveFont(50f);
		    long begin2 = System.nanoTime();
		    Font m25 = warmedMusic.deriveFont(25f);
		    long end = System.nanoTime();
		    
		    System.out.println("1st = "+(begin2-begin1)+", 2nd = "+(end-begin2));
		    
		    long r1 = System.nanoTime();
		    FontRenderContext frc = g2.getFontRenderContext();
		    
		    GlyphVector gv = m50.createGlyphVector(frc, c);
		    System.out.println("width "+gv.getGlyphMetrics(0).getBounds2D().getWidth());
		    g2.drawGlyphVector(gv, 100, 100);
		    
		    gv = m50.createGlyphVector(frc, d);
		    System.out.println("width "+gv.getGlyphMetrics(0).getBounds2D().getWidth());
		    g2.drawGlyphVector(gv, 100, 100);
		    
		    gv = m25.createGlyphVector(frc, c);
		    System.out.println("width "+gv.getGlyphMetrics(0).getBounds2D().getWidth());
		    g2.drawGlyphVector(gv, 100, 150);
		    long r2 = System.nanoTime();
		    System.out.println(r2-r1);
		  }
		}

}
