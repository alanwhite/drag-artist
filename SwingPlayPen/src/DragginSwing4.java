import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

/*
 * adds mouse pointer visualisation if over a draggable widget, illustrating what happens if each widget 
 * actually has content that cares about mouse and dnd actions and how to manage mouse actions for resize
 * as well as DnD and selection 
 */
public class DragginSwing4 extends JFrame {

	private DataFlavor widgetListFlavor = new DataFlavor(CanvasWidget.class,"Draggin canvas widget list");
	private DataFlavor widgetOffsetFlavor = new DataFlavor(CanvasWidget.class,"Draggin canvas widget offset");
	
	public DragginSwing4() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600,400));
		
		Canvas canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);
		new CanvasDragController(canvas);
		
		for ( int i=0; i<3 ; i++) {
			CanvasWidget widget = new CanvasWidget();
			widget.setLocation(new Point(10+(i*50),10+(i*50)));
			canvas.add(widget);
		}
		
		pack();
		setVisible(true);
	}
	
	class Canvas extends JPanel {
		private JPanel selectionPanel = new JPanel();
		private CanvasWidgetResizeHandler canvasWidgetResizeHandler = new CanvasWidgetResizeHandler();
		
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
			setComponentZOrder(selectionPanel, 0);
		}
				
		public Rectangle getSelectionBounds() {
			return selectionPanel.getBounds();
		}

		public CanvasWidgetResizeHandler getResizeHandler() {
			return canvasWidgetResizeHandler;
		}		
	}
		
	class CanvasWidget extends JPanel {
		
		private boolean moving = false;
		private boolean selected = false;
		private Border emptyBorder = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		private Border selectedBorder = new CanvasWidgetBorder();
		
		public CanvasWidget() {
			setBorder(emptyBorder);
			setBackground(Color.WHITE);

			CanvasWidgetMouse cwm = new CanvasWidgetMouse();
			addMouseMotionListener(cwm);	// so we can re-dispatch drag
			addMouseListener(cwm); 			// and release events on the border	
			
			setLayout(new BorderLayout());
			JTextField text = new JTextField("data to edit");
			add(text, BorderLayout.CENTER);
			Dimension size = new Dimension(
					getInsets().left+getInsets().right+text.getPreferredSize().width,
					getInsets().top+getInsets().bottom+text.getPreferredSize().height);
			setSize(size);
		}
		
		public boolean isMoving() {
			return moving;
		}
		
		public void setMoving(boolean moving) {
			if ( moving )
				setBackground(Color.LIGHT_GRAY);
			else
				setBackground(Color.WHITE);
			this.moving = moving;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
			if ( selected )
				setBorder(selectedBorder);
			else
				setBorder(emptyBorder);
		}
	}
	
	class CanvasWidgetBorder implements Border {

		final private int size = 8;
		final private Rectangle[] handle = { 
				new Rectangle(0,0,size,size), new Rectangle(0,0,size,size), new Rectangle(0,0,size,size),
				new Rectangle(0,0,size,size), new Rectangle(0,0,size,size),
				new Rectangle(0,0,size,size), new Rectangle(0,0,size,size), new Rectangle(0,0,size,size) };

		
		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			handle[0].setLocation(x, y);
			handle[1].setLocation(x + width/2 - size/2, y);
			handle[2].setLocation(x + width - size, y);
			handle[3].setLocation(x, y + height/2 - size/2);
			handle[4].setLocation(x + width - size, y + height/2 - size/2);
			handle[5].setLocation(x, y + height - size);
			handle[6].setLocation(x + width/2 - size/2, y + height - size);
			handle[7].setLocation(x + width - size, y + height - size);
			g.setColor(SystemColor.textHighlight);
			g.drawRect(x + size/2, y + size/2 , width - size, height - size);
			for ( int i=0; i<handle.length; i++)
				g.fillOval(handle[i].x, handle[i].y, handle[i].width, handle[i].height);
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(size,size,size,size);
		}

		public boolean isBorderOpaque() {
			return false;
		}

		public Rectangle[] getHandle() {
			return handle;
		}	
	}
	
	class CanvasWidgetMouse extends MouseAdapter {
		final private int[] cursor = {
				Cursor.NW_RESIZE_CURSOR, Cursor.N_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
				Cursor.E_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR,
				Cursor.SW_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR, 
				Cursor.HAND_CURSOR };

		public void mouseMoved(MouseEvent e) {
			CanvasWidget widget = (CanvasWidget) e.getComponent();
			Border border = widget.getBorder();
			if ( border instanceof CanvasWidgetBorder ) {
				CanvasWidgetBorder canvasBorder = (CanvasWidgetBorder) widget.getBorder();
				Rectangle[] handle = canvasBorder.getHandle();
				int foundIndex = 8;
				for ( int i=0; i<handle.length; i++ ) {
					if ( handle[i].contains(e.getPoint()) ) {
						foundIndex=i;
						break;
					}
				}
				widget.setCursor(Cursor.getPredefinedCursor(cursor[foundIndex]));
			}
		}

		public void mouseDragged(MouseEvent e) {
			mouseToParent(e);
		}
		
		public void mouseReleased(MouseEvent e) {
			mouseToParent(e);
		}
		
		private void mouseToParent(MouseEvent e) {
			Component parent = e.getComponent().getParent();
			parent.dispatchEvent(new MouseEvent(parent,e.getID(),e.getWhen(),e.getModifiers(),
					e.getComponent().getX() + e.getX(),e.getComponent().getY() + e.getY(),
					e.getXOnScreen(),e.getYOnScreen(),e.getClickCount(),e.isPopupTrigger(),
					e.getButton()));
		}

		public void mouseEntered(MouseEvent e) {
			e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}
	
	class CanvasWidgetTransferable implements Transferable {
		// private DataFlavor[] flavorArray = { widgetListFlavor, widgetOffsetFlavor, DataFlavor.stringFlavor };
		private DataFlavor[] flavorArray = { widgetListFlavor, widgetOffsetFlavor };

		private List<Rectangle> boundsList = new ArrayList<Rectangle>();
		private Point canvasOffset = new Point(0,0);
		
		public CanvasWidgetTransferable(CanvasWidget canvasWidget) {
			boundsList.add(canvasWidget.getBounds());
		}

		public CanvasWidgetTransferable(List<CanvasWidget> canvasWidgets, Point canvasOffset) {
			for ( CanvasWidget widget : canvasWidgets ) {
				boundsList.add(widget.getBounds());
			}
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
		private Point dragStart = null;
		
		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			if ( dragStart == null ) {
				dragStart = ((MouseEvent) e).getPoint();
				super.exportAsDrag(comp, e, action);
			}
		}
		
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		protected Transferable createTransferable(JComponent c) {
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
				setDragImageOffset(new Point(allSelectedArea.x - dragStart.x, allSelectedArea.y - dragStart.y));
				return new CanvasWidgetTransferable(selectedWidgetList,allSelectedArea.getLocation());
			}
			return null;				
		}

		protected void exportDone(JComponent source, Transferable data,
				int action) {
			dragStart = null;
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
			canvas.validate();
			canvas.repaint();
			return true;
		}
	}
	
	class CanvasWidgetResizeHandler {
		private List<CWBeforeResize> originalWidgets = new ArrayList<CWBeforeResize>();
		//private List<CanvasWidget> selectedWidgets = new ArrayList<CanvasWidget>();
		private Canvas canvas = null;
		private MouseMotionListener[] savedMMLs = null;
		private MouseListener[] savedMLs = null;
		private Point start = new Point();
		private Dimension hitSize = new Dimension();
		private Component hitWidget = null;
		private int direction = 0;
		int count=0;
		
		private boolean resizing = false;
		
		private MouseAdapter ma = new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				e.getComponent().removeMouseListener(this);
				e.getComponent().removeMouseMotionListener(this);
				restoreOtherMLs((Canvas) e.getComponent());
				setResizing(false);
				e.consume();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point current = e.getPoint();
				Point delta = new Point(current.x - start.x, current.y - start.y);
				double xratio = (double)delta.x / hitSize.getWidth();
				double yratio = (double)delta.y / hitSize.getHeight();
				
				for ( CWBeforeResize cwOrig : originalWidgets ) {
					int x=cwOrig.bounds.x, y=cwOrig.bounds.y, w=cwOrig.bounds.width, h=cwOrig.bounds.height; 
					delta.setLocation(xratio * cwOrig.bounds.width, yratio * cwOrig.bounds.height);
					
					switch(direction) {
					case Cursor.NW_RESIZE_CURSOR:
						x += delta.x; y += delta.y; w -= delta.x; h -= delta.y;
						break;
					case Cursor.N_RESIZE_CURSOR:
						y += delta.y; h -= delta.y;
						break;
					case Cursor.NE_RESIZE_CURSOR:
						y += delta.y; w += delta.x; h -= delta.y;
						break;
					case Cursor.E_RESIZE_CURSOR:
						x += delta.x; w -= delta.x;
						break;
					case Cursor.W_RESIZE_CURSOR:
						w += delta.x; 
						break;
					case Cursor.SW_RESIZE_CURSOR:
						x += delta.x; w -= delta.x; h += delta.y;
						break;
					case Cursor.S_RESIZE_CURSOR:
						h += delta.y;
						break;
					case Cursor.SE_RESIZE_CURSOR:			
						w += delta.x; h += delta.y;
						break;
					}

					cwOrig.widget.setBounds(x, y, w, h);
					cwOrig.widget.revalidate();
				}
				canvas.repaint();
				e.consume();
			}
			
		};
		
		public void startResize(MouseEvent e) {
			if ( !isResizing() ) {
				setResizing(true);
				originalWidgets.clear();
				//selectedWidgets.clear();

				canvas = (Canvas) e.getComponent();
				// get all the selected widgets
				for ( Component comp : canvas.getComponents() ) {
					if ( comp instanceof CanvasWidget ) {
						if ( ((CanvasWidget) comp).isSelected() ) {
							// save away for reference and any TBD undo operation
							originalWidgets.add(new CWBeforeResize((CanvasWidget) comp, new Rectangle(comp.getBounds())));
							// create handy local list of selected widgets
							//selectedWidgets.add((CanvasWidget) comp);
						}
					}
				}

				// figure out which component was hit and get the cursor
				start = e.getPoint();
				hitWidget = canvas.getComponentAt(e.getPoint());
				direction = hitWidget.getCursor().getType();
				hitSize = hitWidget.getSize();

				// use subsequent drags to work out moves from opposite corner / side
				suspendOtherMLs(canvas);
				canvas.addMouseListener(ma);
				canvas.addMouseMotionListener(ma);

			}
			
		}
		
		private void suspendOtherMLs(Canvas canvas) {
			savedMMLs=canvas.getMouseMotionListeners();
			savedMLs=canvas.getMouseListeners();
			for ( MouseMotionListener mml : savedMMLs )
				canvas.removeMouseMotionListener(mml);
			for ( MouseListener ml : savedMLs )
				canvas.removeMouseListener(ml);
		}
		
		private void restoreOtherMLs(Canvas canvas) {
			for ( MouseMotionListener mml : savedMMLs )
				canvas.addMouseMotionListener(mml);
			for ( MouseListener ml : savedMLs )
				canvas.addMouseListener(ml);
		}
		
		public boolean isResizing() {
			return resizing;
		}

		public void setResizing(boolean resizing) {
			this.resizing = resizing;
		}

		class CWBeforeResize {
			private CanvasWidget widget;
			private Rectangle bounds;
			
			public CWBeforeResize(CanvasWidget comp, Rectangle rectangle) {
				widget = comp;
				bounds = rectangle;
			}			
		}
	}
	
	class CanvasDragController extends MouseAdapter implements FocusListener {
		private Point dragStart = null;
		
		public CanvasDragController(Canvas canvas) {
			canvas.addMouseMotionListener(this);
			canvas.addMouseListener(this);
			canvas.setTransferHandler(new CanvasTransferHandler());
			canvas.setFocusable(true);
			canvas.addFocusListener(this);
		}

		public void mouseDragged(MouseEvent e) {
			Canvas canvas = (Canvas) e.getComponent();
			if ( dragStart != null ) {
				canvas.setSelectionBounds(dragStart, e.getPoint());
			} else {
				Component hitComp = canvas.getComponentAt(e.getPoint());
				if ( hitComp instanceof CanvasWidget ) {
					if ( hitComp.getCursor() == Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) ) {
						TransferHandler th = canvas.getTransferHandler();
						th.exportAsDrag(canvas, e, TransferHandler.MOVE);
					} else {
						canvas.getResizeHandler().startResize(e);
					}
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
			Canvas canvas = (Canvas) e.getComponent();
			if ( dragStart != null ) {
				dragStart = null;
				Rectangle selectionArea = canvas.getSelectionBounds();
				for ( Component comp : canvas.getComponents() ) {
					if ( comp instanceof CanvasWidget ) {
						CanvasWidget canvasWidget = (CanvasWidget) comp;
						canvasWidget.setSelected(selectionArea.contains(canvasWidget.getBounds()));
					}
				}
				canvas.requestFocus();
			} else {
				for ( Component comp1 : canvas.getComponents() ) {
					if ( comp1 instanceof CanvasWidget ) 
						((CanvasWidget) comp1).setSelected(false);
				}
				Component hitComponent = canvas.getComponentAt(e.getPoint());
				if ( hitComponent instanceof CanvasWidget ) {
					((CanvasWidget) hitComponent).setSelected(true);
					canvas.setComponentZOrder(hitComponent,0);
					hitComponent.requestFocus();
				}
			}
			canvas.setSelectionBounds(new Point(0,0), new Point(0,0));
		}

		public void focusGained(FocusEvent e) {}

		public void focusLost(FocusEvent e) {
			Canvas canvas = (Canvas) e.getComponent();
			for ( Component comp1 : canvas.getComponents() ) 
				if ( comp1 instanceof CanvasWidget ) 
					((CanvasWidget) comp1).setSelected(false);
			canvas.repaint();
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DragginSwing4();
			}
		});
	}
}


