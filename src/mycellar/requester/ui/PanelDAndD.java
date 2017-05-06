package mycellar.requester.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mycellar.Program;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 13/11/16
 */
public class PanelDAndD extends JPanel {

	private static final long serialVersionUID = -3180057277279430308L;

	public LabelSearch draggingLabel;
	private boolean isTarget = false;
	protected ChangeListener listener = MainChangeListener.getChangeListener();
	
	private ArrayList<LabelSearch> labels = new ArrayList<LabelSearch>();

	public PanelDAndD() {
		super();
		addMouseListener(new PanelHandler());
		setTransferHandler(new PanelLabelTransferHandler());
	}

	public PanelDAndD(boolean isTarget) {
		super();
		addMouseListener(new PanelHandler());
		setTransferHandler(new PanelLabelTransferHandler());
		this.isTarget = isTarget;
	}

	public boolean isTarget() {
		return isTarget;
	}

	@Override
	public Component add(Component comp) {
		boolean add = true;
		if(!isTarget) {
			if(!labels.contains((LabelSearch)comp))
				labels.add((LabelSearch) comp);
			else
				add = false;
		}
		if(add) {
			listener.stateChanged(new ChangeEvent(this));
			return super.add(comp);
		}
		return null;
	}

	@Override
	public void add(Component comp, Object constraints) {
		boolean add = true;
		if(!isTarget) {
			if(!labels.contains((LabelSearch)comp))
				labels.add((LabelSearch) comp);
			else
				add = false;
		}
		if(add) {
			listener.stateChanged(new ChangeEvent(this));
			super.add(comp, constraints);
		}
	}

	@Override
	public void remove(Component comp) {
		super.remove(comp);
		if(comp instanceof LabelSearch) {
			LabelSearch search = (LabelSearch)comp;
			// Lorsque l'on est sur un élément qui peut uniquement être déplacé
			// On le supprime de la liste si l'on est sur la source
			// et on l'ajoute à la source si l'on n'est pas déjà dessus. 
			if(!search.isCopy()) {
				if(!isTarget) {
					labels.remove(search);
				if(search.getSource() != null && search.getSource() != this)
					search.getSource().add(search);
				}
				search.setAsKeyword(true);
			}
			listener.stateChanged(new ChangeEvent(this));
		}
	}

	public void setChangeListener(ChangeListener l) {
		listener = l;
	}
}

class PanelHandler extends MouseAdapter {
	@Override
	public void mousePressed(MouseEvent e) {
		final PanelDAndD p = (PanelDAndD)e.getSource();
		Component c = SwingUtilities.getDeepestComponentAt(p, e.getX(), e.getY());
		if(c != null && c.getParent() instanceof LabelSearch && c.getParent().getParent() instanceof PanelDAndD) {
			final LabelSearch labelSearch = (LabelSearch)c.getParent();
			if(e.getButton() == MouseEvent.BUTTON3) {
				if(p.isTarget() && labelSearch.getPredicate().isValueRequired()) {
					JPopupMenu popup = new JPopupMenu();
					JMenuItem menu = new JMenuItem(Program.getLabel("Infos079"));
					menu.addActionListener((e1) -> {
							labelSearch.setValue(null);
							labelSearch.askForValue();
							labelSearch.updateUI();
							p.listener.stateChanged(new ChangeEvent(this));
					});
					popup.add(menu);
					popup.show(c, c.getX()+5, c.getY()+5);
				}
			}
			else  {
				p.draggingLabel = labelSearch;
				p.getTransferHandler().exportAsDrag(p, e, labelSearch.isCopy() ? TransferHandler.COPY: TransferHandler.MOVE);
			}
		}
	}
}

class PanelLabelTransferHandler extends TransferHandler {
	private static final long serialVersionUID = -4338469857987642038L;
	private final DataFlavor localObjectFlavor;
	private final JLabel label = new JLabel() {
		private static final long serialVersionUID = 5065631180392050633L;

		@Override 
		public boolean contains(int x, int y) {
			return false;
		}
	};
	private final JWindow window = new JWindow();

	public PanelLabelTransferHandler() {
		localObjectFlavor = new ActivationDataFlavor(
				PanelDAndD.class, DataFlavor.javaJVMLocalObjectMimeType, "JPanel");
		window.add(label);
		window.setAlwaysOnTop(true);
		window.setBackground(new Color(0,true));
		DragSource.getDefaultDragSource().addDragSourceMotionListener(
				new DragSourceMotionListener() {
					@Override
					public void dragMouseMoved(DragSourceDragEvent dsde) {
						Point pt = dsde.getLocation();
						pt.translate(5, 5); // offset
						window.setLocation(pt);
					}
				});
	}
	@Override
	protected Transferable createTransferable(JComponent c) {
		PanelDAndD p = (PanelDAndD)c;
		LabelSearch l = p.draggingLabel;
		String text = l.getText();
		final DataHandler dh = new DataHandler(c, localObjectFlavor.getMimeType());
		if(text == null)
			return dh;
		final StringSelection ss = new StringSelection(text+"\n");
		return new Transferable() {
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				ArrayList<DataFlavor> list = new ArrayList<DataFlavor>();
				for(DataFlavor f:ss.getTransferDataFlavors()) {
					list.add(f);
				}
				for(DataFlavor f:dh.getTransferDataFlavors()) {
					list.add(f);
				}
				return list.toArray(dh.getTransferDataFlavors());
			}
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				for (DataFlavor f: getTransferDataFlavors()) {
					if (flavor.equals(f)) {
						return true;
					}
				}
				return false;
			}
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if(flavor.equals(localObjectFlavor)) {
					return dh.getTransferData(flavor);
				} else {
					return ss.getTransferData(flavor);
				}
			}
		};
	}

	@Override
	public boolean canImport(TransferSupport support) {
		return support.isDrop();
	}

	@Override 
	public int getSourceActions(JComponent c) {
		PanelDAndD p = (PanelDAndD)c;
		//label.setIcon(p.draggingLabel.getIcon());
		label.setText(p.draggingLabel.getText());
		window.pack();
		Point pt = p.draggingLabel.getLocation();
		SwingUtilities.convertPointToScreen(pt, p);
		window.setLocation(pt);
		window.setVisible(true);
		if(p.draggingLabel.isCopy())
			return COPY;
		return MOVE;
	}

	@Override 
	public boolean importData(TransferSupport support) {
		if(!canImport(support))
			return false;
		final PanelDAndD target = (PanelDAndD)support.getComponent();
		try {
			PanelDAndD src = (PanelDAndD)support.getTransferable().getTransferData(localObjectFlavor);
			final LabelSearch l = new LabelSearch(((LabelSearch)src.draggingLabel).getPredicate(), ((LabelSearch)src.draggingLabel).getSource());
			l.setLabel(src.draggingLabel.getLabel());
			l.setAsKeyword(!target.isTarget());
			if(target.isTarget()) {
				l.setValue(src.draggingLabel.getValue());
				l.setAsKeyword(false);
				SwingUtilities.invokeLater(() -> {
					l.askForValue();
					target.listener.stateChanged(new ChangeEvent(this));
				});
			}
			target.add(l);
			target.revalidate();
			return true;
		} catch(UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
		} catch(java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		return false;
	}

	@Override 
	protected void exportDone(JComponent c, Transferable data, int action) {
		PanelDAndD src = (PanelDAndD)c;
		if(action == TransferHandler.MOVE) {
			src.remove(src.draggingLabel);
			src.revalidate();
			src.repaint();
		}
		src.draggingLabel = null;
		window.setVisible(false);
	}
}
