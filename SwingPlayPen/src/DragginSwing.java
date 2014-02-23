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


// doing it with swing based widgets, instead of custom ones

public class DragginSwing extends JFrame {

	public DragginSwing() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600,400));
		
		Canvas canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);
		
		CanvasWidget widget = new CanvasWidget();
		widget.setBounds(new Rectangle(10,10,50,50));
		canvas.add(widget);
		
		pack();
		setVisible(true);
	}
	
	class Canvas extends JPanel {
		
		public Canvas() {
			setLayout(null);
			new CanvasDragController(this);
		}

	}
		
	class CanvasWidget extends JPanel {
	
		public CanvasWidget() {
			setBackground(Color.DARK_GRAY);
			setTransferHandler(new TransferHandler("bounds"));
		}

	}
	
	class CanvasTransferHandler extends TransferHandler {
		private DataFlavor widgetFlavor = new DataFlavor(CanvasWidget.class,"Draggin canvas widget");
		
		@Override
		public boolean importData(TransferSupport support) {
			if (!canImport(support))
				return false;
			return super.importData(support);
		}

		@Override
		public boolean canImport(TransferSupport support) {
		    if (!support.isDataFlavorSupported(widgetFlavor)) {
		        return false;
		    }
			return super.canImport(support);
		}

		@Override
		protected void exportDone(JComponent source, Transferable data,
				int action) {
			System.out.println("exportDone is never called");
			super.exportDone(source, data, action);
		}
	}
	
	class CanvasDragController extends MouseAdapter implements DropTargetListener {
		public CanvasDragController(Canvas canvas) {
			canvas.addMouseMotionListener(this);
			canvas.setSourceActions ... somehow (?)
			DropTarget target = new DropTarget(canvas,this);
		}

		public void mouseDragged(MouseEvent e) {
			Canvas canvas = (Canvas) e.getComponent();
			Component comp = canvas.getComponentAt(e.getPoint());
			if ( comp != null ) {
				if ( comp instanceof CanvasWidget ) {
					CanvasWidget widget = (CanvasWidget) comp;
					TransferHandler th = widget.getTransferHandler();
					th.exportAsDrag(canvas, e, TransferHandler.MOVE);
					System.out.println("exported");
				}
			}
		}

		public void dragEnter(DropTargetDragEvent dtde) {}
		public void dragOver(DropTargetDragEvent dtde) {}
		public void dropActionChanged(DropTargetDragEvent dtde) {}
		public void dragExit(DropTargetEvent dte) {}

		public void drop(DropTargetDropEvent dtde) {
			// TODO Auto-generated method stub
			System.out.println("dropped");
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DragginSwing();
			}
		});
	}
}
