import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.WindowConstants;

// this version adds a drag image
public class DragginSwing3 extends JFrame {

	private DataFlavor widgetListFlavor = new DataFlavor(CanvasWidget.class,"Draggin canvas widget list");
	
	public DragginSwing3() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600,400));
		
		Canvas canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);
		
		new CanvasDragController(canvas);
		
		CanvasWidget widget = new CanvasWidget();
		widget.setBounds(new Rectangle(10,10,50,50));
		canvas.add(widget);
		
		CanvasWidget widget2 = new CanvasWidget();
		widget2.setBounds(new Rectangle(100,100,50,50));
		canvas.add(widget2);
		
		
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
		
		public void setSelectionStartPoint(Point startPoint) {
			selectionPanel.setLocation(startPoint);
			if ( startPoint.x == 0 && startPoint.y == 0 ) {
				selectionPanel.setVisible(false);
				selectionPanel.setBounds(new Rectangle(0,0,0,0));
			} else {
				selectionPanel.setVisible(true);
			}
		}
		
		public void setSelectionEndPoint(Point endPoint) {
			Rectangle bounds = selectionPanel.getBounds();
			bounds.add(endPoint);
			selectionPanel.setBounds(bounds);
			selectionPanel.setVisible(true);
			repaint();
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
		private DataFlavor[] flavorArray = { widgetListFlavor, DataFlavor.stringFlavor };
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
			
			return false;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if ( flavor == DataFlavor.stringFlavor ) 
				return boundsList.toString();
			if ( flavor == widgetListFlavor )
				return new ArrayList<Rectangle>(boundsList);
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
					BufferedImage widgetImage = new BufferedImage(hitWidget.getWidth(),
							hitWidget.getHeight(),BufferedImage.TYPE_INT_ARGB);
					Graphics g = widgetImage.getGraphics();
					hitWidget.paintAll(g);
					hitWidget.setMoving(true);
					setDragImage(widgetImage);
					setDragImageOffset(new Point(hitWidget.getX() - dragStart.x, hitWidget.getY() - dragStart.y));
					return new CanvasWidgetTransferable(hitWidget);
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
				Point canvasOffset = allSelectedArea.getLocation();
				for ( CanvasWidget drawingWidget : selectedWidgetList ) {
					g.translate(drawingWidget.getX() - canvasOffset.x, drawingWidget.getY() - canvasOffset.y);
					drawingWidget.paintAll(g);
					drawingWidget.setMoving(true);
				}
				
				setDragImage(selectedWidgetsImage);
				setDragImageOffset(new Point(allSelectedArea.x - dragStart.x, allSelectedArea.y - dragStart.y));
				return new CanvasWidgetTransferable(selectedWidgetList,canvasOffset);
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
			Transferable transferable = support.getTransferable();
			try {
				boundsList = (List<Rectangle>) transferable.getTransferData(widgetListFlavor);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			Point canvasOffset = new Point(0,0);
			if ( transferable instanceof CanvasWidgetTransferable ) 
				canvasOffset = ((CanvasWidgetTransferable) transferable).getCanvasOffset();
			
			for ( Rectangle bounds : boundsList ) {
				CanvasWidget widget = new CanvasWidget();
				Point mouseLocation = support.getDropLocation().getDropPoint();
				Point imageOffset = getDragImageOffset();
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
		
		private boolean selecting = false;
		
		public CanvasDragController(Canvas canvas) {
			canvas.addMouseMotionListener(this);
			canvas.addMouseListener(this);
			canvas.setTransferHandler(new CanvasTransferHandler());
		}

		public void mouseDragged(MouseEvent e) {
			Canvas canvas = (Canvas) e.getComponent();
			
			if ( selecting ) {
				canvas.setSelectionEndPoint(e.getPoint());
			} else {
				Component hitComp = canvas.getComponentAt(e.getPoint());
				if ( hitComp instanceof CanvasWidget ) {
					TransferHandler th = canvas.getTransferHandler();
					th.exportAsDrag(canvas, e, TransferHandler.MOVE);
				} else {
					selecting = true;
					for ( Component comp : canvas.getComponents() ) {
						if ( comp instanceof CanvasWidget ) 
							((CanvasWidget) comp).setSelected(false);
					}
					canvas.setSelectionStartPoint(e.getPoint());
					canvas.setSelectionEndPoint(e.getPoint());	
				}				
			}
		}

		public void mouseReleased(MouseEvent e) {
			selecting = false;
			Canvas canvas = (Canvas) e.getComponent();
			Rectangle selectionArea = canvas.getSelectionBounds();
			for ( Component comp : canvas.getComponents() ) {
				if ( comp instanceof CanvasWidget ) {
					CanvasWidget canvasWidget = (CanvasWidget) comp;
					canvasWidget.setSelected(selectionArea.contains(canvasWidget.getBounds()));
				}
			}
			canvas.setSelectionEndPoint(new Point(0,0));
			canvas.setSelectionStartPoint(new Point(0,0));
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


