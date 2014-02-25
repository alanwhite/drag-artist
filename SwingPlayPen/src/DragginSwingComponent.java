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
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.WindowConstants;

public class DragginSwingComponent extends JFrame {
	private DataFlavor widgetFlavor = new DataFlavor(CanvasWidget.class,"Draggin canvas widget");
	
	public DragginSwingComponent() {
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
		public CanvasWidget() {
			setBackground(Color.DARK_GRAY);
			setTransferHandler(new CanvasWidgetTransferHandler());
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
	
	class CanvasWidgetTransferHandler extends TransferHandler {	
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		protected Transferable createTransferable(JComponent c) {
			return new CanvasWidgetTransferable((CanvasWidget) c);
		}

		protected void exportDone(JComponent source, Transferable data,
				int action) {
			if ( action == TransferHandler.MOVE ) {
				Canvas canvas = (Canvas) source.getParent();
				canvas.remove(source);
				canvas.repaint();
			}
		}
	}
	
	class CanvasDragController extends MouseAdapter implements DropTargetListener {
		public CanvasDragController(Canvas canvas) {
			canvas.addMouseMotionListener(this);
			DropTarget target = new DropTarget(canvas,this);
		}

		public void mouseDragged(MouseEvent e) {
			Canvas canvas = (Canvas) e.getComponent();
			Component comp = canvas.getComponentAt(e.getPoint());
			if ( comp != null ) {
				if ( comp instanceof CanvasWidget ) {
					CanvasWidget widget = (CanvasWidget) comp;
					TransferHandler th = widget.getTransferHandler();
					th.exportAsDrag(widget, e, TransferHandler.MOVE);
				}
			}
		}

		public void dragEnter(DropTargetDragEvent dtde) {}
		public void dragOver(DropTargetDragEvent dtde) {}
		public void dropActionChanged(DropTargetDragEvent dtde) {}
		public void dragExit(DropTargetEvent dte) {}

		public void drop(DropTargetDropEvent dtde) {
			if (!dtde.isDataFlavorSupported(widgetFlavor)) {
				dtde.rejectDrop();
			}
			
			dtde.acceptDrop(DnDConstants.ACTION_MOVE);
			
			Canvas canvas = (Canvas) dtde.getDropTargetContext().getComponent();
			
			Rectangle bounds = new Rectangle(0,0,10,10);
			try {
				bounds = (Rectangle) dtde.getTransferable().getTransferData(widgetFlavor);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			CanvasWidget widget = new CanvasWidget();
			bounds.setLocation(dtde.getLocation());
			widget.setBounds(bounds);
			widget.setTransferHandler(new CanvasWidgetTransferHandler());
			canvas.add(widget);
			canvas.repaint();
			dtde.dropComplete(true);
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DragginSwingComponent();
			}
		});
	}
}
