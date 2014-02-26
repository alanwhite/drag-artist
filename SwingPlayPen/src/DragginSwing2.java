import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.WindowConstants;

// this version adds a drag image
public class DragginSwing2 extends JFrame {

	private DataFlavor widgetFlavor = new DataFlavor(CanvasWidget.class,"Draggin canvas widget");
	
	public DragginSwing2() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600,400));
		
		Canvas canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);
		
		new CanvasDragController(canvas);
		
		CanvasWidget widget = new CanvasWidget();
		widget.setBounds(new Rectangle(10,10,50,50));
		canvas.add(widget);
		
		pack();
		setVisible(true);
	}
	
	class Canvas extends JPanel {
		public Canvas() {
			setLayout(null);
		}
	}
		
	class CanvasWidget extends JPanel {
		private boolean moving = false;
		
		public CanvasWidget() {
			setBackground(Color.DARK_GRAY);
		}
		
		public boolean isMoving() {
			return moving;
		}
		
		public void setMoving(boolean moving) {
			if ( moving )
				setBackground(Color.LIGHT_GRAY);
			else
				setBackground(Color.DARK_GRAY);
			this.moving = moving;
		}
	}
	
	class CanvasWidgetTransferable implements Transferable {
		private DataFlavor[] flavorArray = { widgetFlavor, DataFlavor.stringFlavor };
		private Rectangle bounds = null;
		
		public CanvasWidgetTransferable(CanvasWidget canvasWidget) {
			bounds = canvasWidget.getBounds();
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavorArray;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor == widgetFlavor || 
				flavor == DataFlavor.stringFlavor;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if ( flavor == DataFlavor.stringFlavor ) 
				return this.bounds.toString();
			if ( flavor == widgetFlavor )
				return new Rectangle(this.bounds);
			return null;
		}		
	}
	
	class CanvasTransferHandler extends TransferHandler {	
		private Point dragStart = new Point();
		
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		protected Transferable createTransferable(JComponent c) {
			for ( Component comp : c.getComponents() ) {
				if ( comp instanceof CanvasWidget ) {
					CanvasWidget canvasWidget = (CanvasWidget) comp;
					if ( canvasWidget.isMoving() ) {
						canvasWidget.setMoving(false);
						BufferedImage widgetImage = new BufferedImage(canvasWidget.getWidth(),
								canvasWidget.getHeight(),BufferedImage.TYPE_INT_ARGB);
						Graphics g = widgetImage.getGraphics();
						canvasWidget.paintAll(g);
						canvasWidget.setMoving(true);
						setDragImage(widgetImage);
						setDragImageOffset(new Point(canvasWidget.getX() - dragStart.x, canvasWidget.getY() - dragStart.y));
						return new CanvasWidgetTransferable(canvasWidget);
					}
				}
			}
			return null;
		}

		protected void exportDone(JComponent source, Transferable data,
				int action) {
			if ( action == TransferHandler.MOVE ) {
				Canvas canvas = (Canvas) source;
				for ( Component comp : source.getComponents() ) {
					if ( comp instanceof CanvasWidget ) {
						CanvasWidget canvasWidget = (CanvasWidget) comp;
						if ( canvasWidget.isMoving() ) {
							canvas.remove(canvasWidget);
							canvas.repaint();
						}
					}
				}
			}
		}

		public boolean importData(TransferSupport support) {
			if ( !canImport(support) )
				return false;

			Canvas canvas = (Canvas) support.getComponent();
			Rectangle bounds = new Rectangle(0,0,10,10);
			try {
				bounds = (Rectangle) support.getTransferable().getTransferData(widgetFlavor);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
			CanvasWidget widget = new CanvasWidget();
			Point mouseLocation = support.getDropLocation().getDropPoint();
			Point realDropLocation = new Point(mouseLocation.x + getDragImageOffset().x,mouseLocation.y + getDragImageOffset().y);
			bounds.setLocation(realDropLocation);
			widget.setBounds(bounds);
			canvas.add(widget);
			canvas.repaint();
			return true;
		}

		public boolean canImport(TransferSupport support) {
			if ( support.isDataFlavorSupported(widgetFlavor) )
				return true;
			return false;
		}

		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			dragStart = ((MouseEvent) e).getPoint();
			super.exportAsDrag(comp, e, action);
		}

	}
	
	class CanvasDragController extends MouseAdapter {
		public CanvasDragController(Canvas canvas) {
			canvas.addMouseMotionListener(this);
			canvas.setTransferHandler(new CanvasTransferHandler());
		}

		public void mouseDragged(MouseEvent e) {
			Canvas canvas = (Canvas) e.getComponent();
			Component comp = canvas.getComponentAt(e.getPoint());
			if ( comp != null ) {
				if ( comp instanceof CanvasWidget ) {
					CanvasWidget canvasWidget = (CanvasWidget) comp;
					canvasWidget.setMoving(true);
					TransferHandler th = canvas.getTransferHandler();
					th.exportAsDrag(canvas, e, TransferHandler.MOVE);
				}
			}
		}

	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DragginSwing2();
			}
		});
	}
}


