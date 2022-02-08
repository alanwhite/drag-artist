package org.whiteware.swing;
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 


import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*
 * This applet displays a String with the user's selected 
 * fontname, style and size attributes.
*/

public class FontSelector extends JDialog
    implements ChangeListener, ItemListener {

	public static final int USER_OK = 1;
	public static final int USER_CANCEL = 2;
	
	public static final int STYLE_PLAIN = 1;
	public static final int STYLE_BOLD = 2;
	public static final int STYLE_ITALIC = 3;
	public static final int STYLE_BOLDITALIC = 4;
	
	
	private JButton okButton;
	private JButton cancelButton;
	
    private int	returnValue = USER_CANCEL;
	
    TextTestPanel textTestPanel;
    String exampleText = "The quick brown fox jumped over the lazy dog";
    JComboBox fonts, styles;
    JSpinner sizes;
    String fontChoice = "Dialog";
    int styleChoice = 0;
    int sizeChoice = 12;

    public FontSelector(JFrame frame) {
    		super(frame);
    		
    		this.setModalityType(ModalityType.APPLICATION_MODAL);

    		this.okButton = new JButton("OK");
    		this.okButton.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent event) {
    				setReturnValue(USER_OK);
    				setVisible(false);
    			}	
    		});

    		this.cancelButton = new JButton("Cancel");
    		this.cancelButton.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent event) {
    				setReturnValue(USER_CANCEL);
    				setVisible(false);
    			}
    		});
    		
        JPanel fontSelectorPanel = new JPanel();

        fontSelectorPanel.add(new JLabel("Font family:"));

        GraphicsEnvironment gEnv =
            GraphicsEnvironment.getLocalGraphicsEnvironment();

        fonts = new JComboBox(gEnv.getAvailableFontFamilyNames());

        fonts.setSelectedItem(fontChoice);
        fonts.setMaximumRowCount(5);
        fonts.addItemListener(this);
        fontSelectorPanel.add(fonts);

        fontSelectorPanel.add(new JLabel("Style:"));

        String[] styleNames = {"Plain", "Bold", "Italic", "Bold Italic"};
        styles = new JComboBox(styleNames);
        styles.addItemListener(this);
        fontSelectorPanel.add(styles);

        fontSelectorPanel.add(new JLabel("Size:"));

        sizes = new JSpinner(new SpinnerNumberModel(12, 6, 72, 1));
        sizes.addChangeListener(this);
        fontSelectorPanel.add(sizes);

        textTestPanel = new TextTestPanel();
        textTestPanel.setFont(new Font(fontChoice, styleChoice, sizeChoice));
        textTestPanel.setBackground(Color.white);

        JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(this.okButton);
		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		bottomPanel.add(this.cancelButton);
        
        add(BorderLayout.NORTH, fontSelectorPanel);
        add(BorderLayout.CENTER, textTestPanel);
        add(BorderLayout.SOUTH, bottomPanel);
       
        pack();
        setLocationRelativeTo(frame);
    }

    /*
     * Detect a state change in any of the settings and create a new
     * Font with the corresponding settings. Set it on the test component.
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        if (e.getSource() == fonts) {
            fontChoice = (String)fonts.getSelectedItem();
        } else {
            styleChoice = styles.getSelectedIndex();
        }
        textTestPanel.setFont(new Font(fontChoice, styleChoice, sizeChoice));
    }

    public void stateChanged(ChangeEvent e) {
        try {
            String size = sizes.getModel().getValue().toString();
            sizeChoice = Integer.parseInt(size);
            textTestPanel.setFont(new Font(fontChoice,styleChoice,sizeChoice));
        } catch (NumberFormatException nfe) {
        }
    }

	public static void main(String[] args) {

		try {
			String cn = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(cn);
		} catch (Exception cnf) {
		}
		
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	JFrame frame = new JFrame();
            	frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
            	
            	FontSelector theOne = new FontSelector(frame);
            	theOne.setVisible(true);
            	System.out.println(theOne.getReturnValue());
            	System.out.println(theOne.getFontChoice());
            	System.out.println(theOne.getStyleChoice());
            	System.out.println(theOne.getSizeChoice());
            }
        });
	}

	/**
	 * @return the returnValue
	 */
	public int getReturnValue() {
		return returnValue;
	}

	/**
	 * @param returnValue the returnValue to set
	 */
	public void setReturnValue(int returnValue) {
		this.returnValue = returnValue;
	}

	public String getFontChoice() {
		return fontChoice;
	}

	public void setFontChoice(String fontChoice) {
		this.fontChoice = fontChoice;
	}

	public int getStyleChoice() {
		return styleChoice;
	}

	public void setStyleChoice(int styleChoice) {
		this.styleChoice = styleChoice;
	}

	public int getSizeChoice() {
		return sizeChoice;
	}

	public void setSizeChoice(int sizeChoice) {
		this.sizeChoice = sizeChoice;
	}

	class TextTestPanel extends JPanel {

	    public Dimension getPreferredSize() {
	        return new Dimension(500,200);
	    }

	    public void setFont(Font font) {
	        super.setFont(font);
	        repaint();
	    }

	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);

	        g.setColor(Color.black);
	        g.setFont(getFont());
	        FontMetrics metrics = g.getFontMetrics();
	        // String text = "The quick brown fox jumped over the lazy dog";
	        int x = getWidth()/2 - metrics.stringWidth(exampleText)/2;
	        int y = getHeight() - 80;
	        g.drawString(exampleText, x, y);
	    }
	    

	}

	public String getExampleText() {
		return exampleText;
	}

	public void setExampleText(String exampleText) {
		this.exampleText = exampleText;
	}
	
}
