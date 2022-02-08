package org.whiteware.swing;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.font.TextAttribute;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * 
 */

/**
 * @author alanwhite
 *
 */
public class Textual extends JFrame {

	private JTextField textField = new JTextField();
	private JTextArea output = new JTextArea();
	
	public Textual() throws HeadlessException {
		super();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600,400));
		this.getContentPane().setLayout(new BorderLayout());
		
		textField.setEditable(false);
		textField.setOpaque(false);
		textField.setBorder(BorderFactory.createEmptyBorder());
		textField.setColumns(30);
		textField.setText("The Texting Field");
		
		Map<TextAttribute, Object> fontAttributes = new Hashtable<TextAttribute, Object>();
		fontAttributes.put(TextAttribute.FAMILY, Font.SERIF);
		fontAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		fontAttributes.put(TextAttribute.SIZE, new Double(24.0f));
		fontAttributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
		fontAttributes.put(TextAttribute.UNDERLINE, -1);
		fontAttributes.put(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_NONE);
		fontAttributes.put(TextAttribute.TRANSFORM, new TransformAttribute(AffineTransform.getScaleInstance(1.5, 1.5)));
		textField.setFont(new Font(fontAttributes));
		
		add(textField,BorderLayout.NORTH);
		
		output.setEditable(false);
		output.setRows(20);
		
		output.setText(
				"Preferred Size (H x W) "+textField.getPreferredSize().getHeight()+" x "+textField.getPreferredSize().getWidth()+"\n"+
				"Insets (T x B) "+textField.getInsets().top+" x "+textField.getInsets().bottom+"\n"+
				"Font Size "+textField.getFont().getSize2D()+"\n"+
				"Font (H x A x D) "+
					textField.getFontMetrics(textField.getFont()).getHeight()+" x "+
					textField.getFontMetrics(textField.getFont()).getAscent()+" x "+
					textField.getFontMetrics(textField.getFont()).getDescent()+"\n"+
				"Font Max (H x A x D) "+
					textField.getFontMetrics(textField.getFont()).getHeight()+" x "+
					textField.getFontMetrics(textField.getFont()).getMaxAscent()+" x "+
					textField.getFontMetrics(textField.getFont()).getMaxDescent()
				);
		
		
		
		add(output,BorderLayout.CENTER);
		
		pack();
		setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Textual();
			}
		});

	}

}
