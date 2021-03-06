import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;

/*
 * adds multi-widget selection & drag / drop
 */
public class DragginSwing3 extends JFrame {

	private DataFlavor widgetListFlavor = new DataFlavor(CanvasWidget.class,"Draggin canvas widget list");
	private DataFlavor widgetOffsetFlavor = new DataFlavor(CanvasWidget.class,"Draggin canvas widget offset");
	
	public DragginSwing3() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600,400));
		
		Canvas canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);
		new CanvasDragController(canvas);
		
		for ( int i=0; i<3 ; i++) {
			CanvasWidget widget = new CanvasWidget();
			widget.setBounds(new Rectangle(10+(i*50),10+(i*50),50,50));
			canvas.add(widget);
		}
		
		pack();
		setVisible(true);
	}
	
	class Canvas extends JPanel {
		private JPanel selectionPanel = new JPanel();
		
		public Canvas() {
			setLayout(null);
			selectionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			selectionPanel.setBounds(0, 0, 0, 0);
			selectionPanel.setOpaque(false);
			selectionPanel.setVisible(false);
			add(selectionPanel);
		}
		
		public void setSelectionBounds(Point point1, Point point2) {
			selectionPanel.setLocation(Math.min(point1.x, point2.x),Math.min(point1.y, point2.y));
			selectionPanel.setSize(
					Math.max(point1.x - point2.x, point2.x - point1.x),
					Math.max(point1.y - point2.y, point2.y - point1.y));
			if ( selectionPanel.getBounds().isEmpty() ) 
				selectionPanel.setVisible(false);
			else
				selectionPanel.setVisible(true);
		}
				
		public Rectangle getSelectionBounds() {
			return selectionPanel.getBounds();
		}
	}
		
	class CanvasWidget extends JPanel {
		private boolean moving = false;
		private boolean selected = false;
		
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

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
			if ( selected )
				setBackground(Color.RED);
			else
				setBackground(Color.DARK_GRAY);
		}
	}
	
	class CanvasWidgetTransferable implements Transferable {
		private DataFlavor[] flavorArray = { widgetListFlavor, widgetOffsetFlavor, DataFlavor.stringFlavor };
		private List<Rectangle> boundsList = new ArrayList<Rectangle>();
		private Point canvasOffset = new Point(0,0);
		
		public CanvasWidgetTransferable(CanvasWidget canvasWidget) {
			boundsList.add(canvasWidget.getBounds());
		}

		public CanvasWidgetTransferable(List<CanvasWidget> canvasWidgets, Point canvasOffset) {
			for ( CanvasWidget widget : canvasWidgets )
				boundsList.add(widget.getBounds());
			this.canvasOffset = canvasOffset;
		}
		
		public DataFlavor[] getTransferDataFlavors() {
			return flavorArray;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if ( boundsList.size() != 0 && flavor == widgetListFlavor )
				return true;
			if ( flavor == DataFlavor.stringFlavor )
				return true;
			if ( flavor == widgetOffsetFlavor )
				return true;
			return false;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if ( flavor == DataFlavor.stringFlavor ) 
				return boundsList.toString();
			if ( flavor == widgetListFlavor )
				return new ArrayList<Rectangle>(boundsList);
			if ( flavor == widgetOffsetFlavor )
				return new Point(canvasOffset);
			return null;
		}	
		
		public Point getCanvasOffset() {
			return canvasOffset;
		}
	}
	
	class CanvasTransferHandler extends TransferHandler {	
		private Point dragStart = new Point();
		
		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			dragStart = ((MouseEvent) e).getPoint();
			super.exportAsDrag(comp, e, action);
		}
		
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		protected Transferable createTransferable(JComponent c) {
			// if the component under the cursor isn't selected, deselect all, and make that component the one we're moving
			Component hitComp = c.getComponentAt(dragStart);
			if ( hitComp instanceof CanvasWidget ) {
				CanvasWidget hitWidget = (CanvasWidget) hitComp;
				if ( !hitWidget.isSelected() ) {
					for ( Component comp : c.getComponents() ) {
						if ( comp instanceof CanvasWidget ) 
							((CanvasWidget) comp).setSelected(false);
					}
					hitWidget.setSelected(true);
				}

				// if we get here user started drag on a selected widget so all selected widgets get dragged
				Rectangle allSelectedArea = null;
				List<CanvasWidget> selectedWidgetList = new ArrayList<CanvasWidget>();
				for ( Component comp : c.getComponents() ) {
					if ( comp instanceof CanvasWidget ) {
						CanvasWidget canvasWidget = (CanvasWidget) comp;
						if ( canvasWidget.isSelected() ) {
							if ( allSelectedArea == null ) 
								allSelectedArea = new Rectangle(canvasWidget.getBounds());
							else
								allSelectedArea.add(canvasWidget.getBounds());
							selectedWidgetList.add(canvasWidget);
						}
					}
				}

				BufferedImage selectedWidgetsImage = new BufferedImage(allSelectedArea.width,allSelectedArea.height,BufferedImage.TYPE_INT_ARGB);
				Graphics g = selectedWidgetsImage.getGraphics();
				int xlatX = 0, xlatY = 0;
				for ( CanvasWidget drawingWidget : selectedWidgetList ) {
					g.translate(-xlatX, -xlatY);
					xlatX = drawingWidget.getX() - allSelectedArea.getLocation().x;
					xlatY = drawingWidget.getY() - allSelectedArea.getLocation().y;
					g.translate(xlatX,xlatY);
					drawingWidget.paintAll(g);
					drawingWidget.setMoving(true);
				}

				setDragImage(selectedWidgetsImage);
				
				Point imageOffset = new Point(allSelectedArea.x - dragStart.x, allSelectedArea.y - dragStart.y);
				// stupid java inconsistency between platforms
	    	    if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) { 
	    	    	imageOffset = new Point(-imageOffset.x,-imageOffset.y);
	    	    }
	    	    
				setDragImageOffset(imageOffset);
				return new CanvasWidgetTransferable(selectedWidgetList,allSelectedArea.getLocation());
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
						if ( canvasWidget.isMoving() ) 
							canvas.remove(canvasWidget);
					}
				}
				canvas.repaint();
			} else if ( action == TransferHandler.NONE ) {
				for ( Component comp : source.getComponents() ) 
					if ( comp instanceof CanvasWidget ) 
						((CanvasWidget) comp).setMoving(false);
			}
		}


		public boolean canImport(TransferSupport support) {
			if ( support.isDataFlavorSupported(widgetListFlavor) )
				return true;
			return false;
		}
		
		public boolean importData(TransferSupport support) {
			if ( !canImport(support) )
				return false;

			Canvas canvas = (Canvas) support.getComponent();
			List<Rectangle> boundsList = new ArrayList<Rectangle>();
			Point canvasOffset = new Point(0,0);
			Transferable transferable = support.getTransferable();
			try {
				boundsList = (List<Rectangle>) transferable.getTransferData(widgetListFlavor);
				canvasOffset = (Point) transferable.getTransferData(widgetOffsetFlavor);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			Point mouseLocation = support.getDropLocation().getDropPoint();
			Point imageOffset = getDragImageOffset();
			
			// stupid java inconsistency between platforms
    	    if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) { 
    	    	imageOffset = new Point(-imageOffset.x,-imageOffset.y);
    	    }
			
			for ( Rectangle bounds : boundsList ) {
				CanvasWidget widget = new CanvasWidget();
				Point realDropLocation = new Point(
						mouseLocation.x + imageOffset.x + bounds.x - canvasOffset.x,
						mouseLocation.y + imageOffset.y + bounds.y - canvasOffset.y);
				bounds.setLocation(realDropLocation);
				widget.setBounds(bounds);
				canvas.add(widget);
			}
			canvas.repaint();
			return true;
		}
	}
	
	class CanvasDragController extends MouseAdapter {
		private Point dragStart = null;
		
		public CanvasDragController(Canvas canvas) {
			canvas.addMouseMotionListener(this);
			canvas.addMouseListener(this);
			canvas.setTransferHandler(new CanvasTransferHandler());
		}

		public void mouseDragged(MouseEvent e) {
			Canvas canvas = (Canvas) e.getComponent();
			
			if ( dragStart != null ) {
				canvas.setSelectionBounds(dragStart, e.getPoint());
			} else {
				Component hitComp = canvas.getComponentAt(e.getPoint());
				if ( hitComp instanceof CanvasWidget ) {
					TransferHandler th = canvas.getTransferHandler();
					th.exportAsDrag(canvas, e, TransferHandler.MOVE);
				} else {
					dragStart = e.getPoint();
					for ( Component comp : canvas.getComponents() ) {
						if ( comp instanceof CanvasWidget ) 
							((CanvasWidget) comp).setSelected(false);
					}
					canvas.setSelectionBounds(dragStart, dragStart);
				}				
			}
		}

		public void mouseReleased(MouseEvent e) {
			dragStart = null;
			Canvas canvas = (Canvas) e.getComponent();
			Rectangle selectionArea = canvas.getSelectionBounds();
			for ( Component comp : canvas.getComponents() ) {
				if ( comp instanceof CanvasWidget ) {
					CanvasWidget canvasWidget = (CanvasWidget) comp;
					canvasWidget.setSelected(selectionArea.contains(canvasWidget.getBounds()));
				}
			}
			canvas.setSelectionBounds(new Point(0,0), new Point(0,0));
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DragginSwing3();
			}
		});
	}
}


