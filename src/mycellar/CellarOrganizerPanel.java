package mycellar;

import mycellar.core.IAddVin;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.1
 * @since 10/04/19
 */

public class CellarOrganizerPanel extends JPanel implements ITabListener {

	private static final long serialVersionUID = -1239228393406479587L;
	private final MouseListener handler = new Handler();
	private final LabelTransferHandler th = new LabelTransferHandler();

	private final List<JPanel[][]> places = new LinkedList<>();
	
	static final List<RangementCell> CELLS_LIST = new ArrayList<>();
	
	private final JPanel placePanel = new JPanel();
	private final LinkedList<Rangement> armoires = new LinkedList<>();
	private MyCellarComboBox<Rangement> comboRangement;
	private Rangement rangement;
	private RangementCell stock;
	private final MyCellarButton move = new MyCellarButton(new MoveAction());
	private IAddVin addvin;
	
	private final boolean cellChooser;
	private boolean updateView = false;

	public CellarOrganizerPanel() {
		cellChooser = false;
		init();
	}
	
	public CellarOrganizerPanel(boolean cellChooser, IAddVin addvin) {
		this.cellChooser = cellChooser;
		this.addvin = addvin;
		init();
	}
	
	private void init() {
		
		if(cellChooser) {
			setLayout(new MigLayout("","[grow]","[][]20px[grow]"));
		} else {
			setLayout(new MigLayout("","[grow]20px[200:200:200]","[][]20px[grow]"));
		}
		
		RangementUtils.putTabStock();
		placePanel.setLayout(new MigLayout("","grow", ""));
		comboRangement = new MyCellarComboBox<>();
		comboRangement.addItem(Program.EMPTY_PLACE);
		if(addvin == null) {
			armoires.add(Program.EMPTY_PLACE);
		}
		for(Rangement r : Program.getCave()) {
			if(addvin == null || !r.isCaisse()) {
				armoires.add(r);
				comboRangement.addItem(r);
			}
		}
		
		comboRangement.addItemListener((item) -> {
			if(item.getStateChange() == ItemEvent.SELECTED) {
				Rangement r = (Rangement)comboRangement.getSelectedItem();
				if (r != null) {
					setRangement(r);
				}
			}
		});
		
		if(!armoires.isEmpty()) {
			setRangement(armoires.getFirst());
		}
		
		stock = new RangementCell(handler, th);
		JScrollPane scrollStock = new JScrollPane(stock);
		scrollStock.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollStock.setBorder(BorderFactory.createTitledBorder(Program.getLabel("ManagePlace.Stock")));
		add(new MyCellarLabel(Program.getLabel("ManagePlace.SelectPlace")), "split 3");
		add(comboRangement, "gapleft 10px");
		add(move, "gapleft 10px, wrap");
		move.setEnabled(false);
		if(cellChooser) {
			add(new MyCellarLabel(Program.getLabel("ManagePlace.ChooseCell")),"wrap");
		} else {
			add(new MyCellarLabel(Program.getLabel("ManagePlace.StockDescription")),"wrap");
		}
		add(new JScrollPane(placePanel), "grow");
		if(!cellChooser) {
			add(scrollStock, "grow");
		}
	}

	private void setRangement(final Rangement rangement) {
		this.rangement = rangement;
		move.setEnabled(rangement.isCaisse());
		SwingUtilities.invokeLater(() -> {
			CELLS_LIST.clear();
			placePanel.removeAll();
			places.clear();
			if(rangement.equals(Program.EMPTY_PLACE)) {
				placePanel.updateUI();
				return;
			}
			if(rangement.isCaisse()) {
				HashMap<Integer, Integer> mapEmplSize = new HashMap<>();
				for(int i = 0; i<rangement.getNbEmplacements(); i++){
					int empl = i + rangement.getStartCaisse();
					mapEmplSize.put(empl, 0);
					JPanel[][] place;
					int nb = rangement.getNbCaseUse(i);
					if(nb == 0) {
						nb = 1;
					}
					places.add(place = new JPanel[nb][1]);
					JPanel panelCellar = new JPanel(new MigLayout("", "grow"));
					
					for(int k=0; k<place.length; k++) {
						JPanel panel;
						RangementCell cell;
						if(cellChooser) {
							cell = new RangementCell(rangement, empl, k, 0, panelCellar);
						} else {
							cell = new RangementCell(handler, th, rangement, empl, k, 0, panelCellar);
						}
						place[k][0] = panel = cell;
						CELLS_LIST.add(cell);
						panelCellar.add(panel, "growx, wrap");
					}
					placePanel.add(new MyCellarLabel(Program.getLabel("Infos029")+" "+(empl)), i>0 ? "newline, gaptop 30, wrap" : "wrap");
					placePanel.add(panelCellar, "grow");
				}

				for(Bouteille b : Program.getStorage().getAllList()) {
					if(b.getEmplacement().endsWith(rangement.getNom())) {
						JPanel[][] place = places.get(b.getNumLieu() - rangement.getStartCaisse());
						int line = mapEmplSize.get(b.getNumLieu());
						((RangementCell)place[line++][0]).addBottle(new BouteilleLabel(b));
						mapEmplSize.put(b.getNumLieu(), line);
					}
				}
			}
			else {
				for(int i = 0; i<rangement.getNbEmplacements(); i++){
					JPanel[][] place;
					places.add(place = new JPanel[rangement.getNbLignes(i)][rangement.getNbColonnesMax(i)]);
					JPanel panelCellar = new JPanel(new GridLayout(rangement.getNbLignes(i),rangement.getNbColonnesStock()));
					
					for(int k=0; k<place.length; k++) {
						for(int j=0; j<place[k].length; j++) {
							JPanel panel;
							if(rangement.isExistingCell(i, k, j)) {
								RangementCell cell;
								if(cellChooser)
									cell = new RangementCell(rangement, i, k, j, panelCellar);
								else
									cell = new RangementCell(handler, th, rangement, i, k, j, panelCellar);
								place[k][j] = panel = cell;
								CELLS_LIST.add(cell);
							} else
								place[k][j] = panel = new JPanel();
							panelCellar.add(panel);
						}
					}
					placePanel.add(new MyCellarLabel(Program.getLabel("Infos029")+" "+(i+1)), i>0 ? "newline, gaptop 30, wrap" : "wrap");
					placePanel.add(panelCellar, "grow");
				}

				Program.getStorage().getAllList().stream().
					filter(bouteille -> bouteille.getEmplacement().endsWith(rangement.getNom())).
					forEach(bouteille -> {
						JPanel[][] place = places.get(bouteille.getNumLieu() - 1);
						((RangementCell)place[bouteille.getLigne()-1][bouteille.getColonne()-1]).addBottle(new BouteilleLabel(bouteille));
					});
			}
			
			if(cellChooser) {
				for(RangementCell cell : CELLS_LIST) {
					cell.initButton();
				}
			}
			placePanel.updateUI();
		});
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		RangementUtils.putTabStock();
		if(stock.getComponentCount() > 0) {
			if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, Program.getError("ManageStock.ConfirmLost"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))
				return false;
			else {
				for(int i=0; i<stock.getComponentCount(); i++) {
					 Component c = stock.getComponent(i);
					 if(c instanceof BouteilleLabel){
						 Program.getStorage().addHistory(History.DEL, ((BouteilleLabel)c).getBouteille());
						 Program.getStorage().getAllList().remove(((BouteilleLabel)c).getBouteille());
						 Program.setToTrash(((BouteilleLabel)c).getBouteille());
					 }
				}
			}	
		}
		if(cellChooser) {
			int count = 0;
			RangementCell selectedCell = null;
			for(RangementCell cell : CELLS_LIST) {
				if(cell.isToggle()) {
					selectedCell = cell;
					count++;
				}
			}
			if(count > 1) {
				JOptionPane.showMessageDialog(null, Program.getError("ManageStock.TooManySelected"), Program.getLabel("Infos049"), JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(selectedCell != null) {
				addvin.selectPlace(rangement, selectedCell.getPlaceNum(), selectedCell.getRow(), selectedCell.getColumn());
			}
			Program.chooseCell = null;
		}
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
		CELLS_LIST.clear();
		stock.removeAll();
		places.clear();
		placePanel.removeAll();
	}
	
	void setAddVin(IAddVin addvin) {
		this.addvin = addvin;
	}
	
	void setUpdateView(){
		updateView = true;
	}
	
	public void updateView() {
		if(!updateView) {
			return;
		}
		SwingUtilities.invokeLater(() -> {
			armoires.clear();
			comboRangement.removeAllItems();
			comboRangement.addItem(Program.EMPTY_PLACE);
			for(Rangement r : Program.getCave()) {
				if(addvin == null || !r.isCaisse()) {
					armoires.add(r);
					comboRangement.addItem(r);
				}
			}
		});
			setRangement(rangement);
	}
	
	private class MoveAction extends AbstractAction {

		private static final long serialVersionUID = 6973442058662866086L;
		
		private MoveAction() {
			super(Program.getLabel("ManageStock.MoveAll"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for(RangementCell cell: CELLS_LIST) {
				BouteilleLabel bottle = cell.getBottleLabel();
				if(bottle != null && bottle.getBouteille() != null) {
					bottle.getBouteille().setEmplacement(Program.TEMP_PLACE);
					stock.addBottle(bottle);
				}
				cell.removeBottle();
			}
			RangementUtils.putTabStock();
			placePanel.updateUI();
			stock.updateUI();
		}
		
	}
}


class RangementCell extends JPanel {
	BouteilleLabel draggingLabel;
	private BouteilleLabel bottle;
	private final boolean stock;
	private int placeNum, row, column;
	private Rangement place;
	private JPanel parent;
	private final JToggleButton select = new JToggleButton();
	private static final long serialVersionUID = -3180057277279430308L;

	RangementCell(MouseListener listener, TransferHandler handler, Rangement place, int placeNum, int row, int column, JPanel parent) {
		stock = false;
		addMouseListener(listener);
		setTransferHandler(handler);
		setBorder(BorderFactory.createEtchedBorder());
		int width = 100;
		if(place.isCaisse()) {
			width = 400;
		}
		setLayout(new MigLayout("","0px[align left, ::"+width+", grow]0px","0px[align center, 20::, grow]0px"));
		this.row = row;
		this.column = column;
		this.placeNum = placeNum;
		this.place = place;
		this.parent = parent;
	}

	RangementCell(MouseListener listener, TransferHandler handler) {
		stock = true;
		addMouseListener(listener);
		setTransferHandler(handler);
		setLayout(new MigLayout("","[align left, 200:200:200]","0px[]"));
	}
	
	RangementCell(Rangement place, int placeNum, int row, int column, JPanel parent) {
		stock = false;
		setBorder(BorderFactory.createEtchedBorder());
		int width = 100;
		if(place.isCaisse()) {
			width = 400;
		}
		setLayout(new MigLayout("","0px[align left, ::"+width+", grow]0px","0px[align center, 20::, grow]0px"));
		this.row = row;
		this.column = column;
		this.place = place;
		this.placeNum = placeNum;
		this.parent = parent;
		select.setText(Program.getLabel("ManagePlace.Select"));
	}

	public boolean isStock() {
		return stock;
	}
	
	void initButton() {
		add(select, "newline");
	}
	
	boolean isToggle() {
		return select.isSelected();
	}

	void addBottle(BouteilleLabel bouteille) {
		if(!stock) {
			bottle = bouteille;
		}
		add(bouteille, stock ? "wrap" : "grow, gapright 0px");
	}
	
	void removeBottle() {
		if(!stock) {
			bottle = null;
		}
	}
	
	BouteilleLabel getBottleLabel() {
		return bottle;
	}

	public int getRow() {
		if(place != null && place.isCaisse()) {
			return -1;
		}
		return row;
	}

	public int getColumn() {
		if(place != null && place.isCaisse()) {
			return -1;
		}
		return column;
	}
	
	int getPlaceNum() {
		if(place != null && place.isCaisse()) {
			return placeNum-1;
		}
		return placeNum;
	}
	
	public Rangement getPlace() {
		return place;
	}

	String getPlaceName() {
		return place != null ? place.getNom() : "";
	}

	public void setPlace(Rangement place) {
		this.place = place;
	}
	
	RangementCell createNewCell() {
		RangementCell rangementCell = new RangementCell(getMouseListeners()[0], getTransferHandler(), place, placeNum, row+1, 0, parent);
		parent.add(rangementCell, "newline, growx");
		return rangementCell;
	}
	
	public boolean isCaisse() {
		return place != null ? place.isCaisse() : false;
	}

	@Override
	public String toString() {
		return "RangementCell [draggingLabel=" + draggingLabel + ", bottle=" + bottle + ", stock=" + stock + ", placeNum=" + placeNum + ", row=" + row
				+ ", column=" + column + ", place=" + place + ", parent=" + (parent != null ? "Yes" : "No") + "]";
	}

	boolean canImport() {
		return getBottleLabel() == null || getBottleLabel().getBouteille() == null;
	}
	
}

class BouteilleLabel extends JPanel {

	private static final long serialVersionUID = -3982812616929975895L;
	private Bouteille bouteille;
	private final MyCellarLabel label = new MyCellarLabel();

	BouteilleLabel(final Bouteille bouteille) {
		super();
		int width = 100;
		Rangement r = bouteille.getRangement();
		if(r != null && r.isCaisse()) {
			width = 400;
		}
		setLayout(new MigLayout("","5px["+width+":"+width+":"+width+"][10:10:10]0px","0px[align center, grow]0px"));
		this.bouteille = bouteille;
		if(bouteille.isWhiteWine()) {
			label.setIcon(MyCellarImage.WHITEWINE);
		} else if(bouteille.isPinkWine()) {
			label.setIcon(MyCellarImage.PINKWINE);
		} else {
			label.setIcon(MyCellarImage.BLACKWINE);
		}
		label.setText("<html>"+bouteille.getNom()+"</html>");
		add(label, "grow");
		add(new PanelCloseButton(){
			private static final long serialVersionUID = 3495975676025406824L;

			@Override
			void actionPerformed() {
				String mess = MessageFormat.format(Program.getLabel("Main.DeleteWine"), bouteille.getNom());
				if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), mess, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
					Component parent = BouteilleLabel.this.getParent();
					if(parent instanceof RangementCell) {
						((RangementCell)parent).remove(BouteilleLabel.this);
						((RangementCell) parent).updateUI();
						Program.getStorage().addHistory(History.DEL, bouteille);
						Program.getStorage().deleteWine(bouteille);
						Program.setToTrash(bouteille);
					}
				}
			}
		});
	}

	public String getText() {
		return label.getText();
	}

	public Icon getIcon() {
		return label.getIcon();
	}

	public Bouteille getBouteille() {
		return bouteille;
	}
	
	void removeBouteille() {
		bouteille = null;
	}

	@Override
	public String toString() {
		return "BouteilleLabel [bouteille=" + bouteille + "]";
	}
	
	
}

class Handler extends MouseAdapter {
	@Override
	public void mousePressed(MouseEvent e) {
		final RangementCell p = (RangementCell)e.getSource();
		Component c = SwingUtilities.getDeepestComponentAt(p, e.getX(), e.getY());
		if(c != null && c.getParent() instanceof BouteilleLabel) {
			p.draggingLabel = (BouteilleLabel)c.getParent();
			p.getTransferHandler().exportAsDrag(p, e, TransferHandler.MOVE);
		}
	}
}
class LabelTransferHandler extends TransferHandler {
	private static final long serialVersionUID = -4338469857987642038L;
	private final DataFlavor localObjectFlavor;
	private final MyCellarLabel label = new MyCellarLabel() {
		private static final long serialVersionUID = 5065631180392050633L;

		@Override 
		public boolean contains(int x, int y) {
			return false;
		}
	};
	private final JWindow window = new JWindow();
	
	LabelTransferHandler() {
		localObjectFlavor = new ActivationDataFlavor(
				RangementCell.class, DataFlavor.javaJVMLocalObjectMimeType, "JPanel");
		window.add(label);
		window.setAlwaysOnTop(true);
		window.setBackground(new Color(0,true));
		DragSource.getDefaultDragSource().addDragSourceMotionListener( (dsde) -> {
			Point pt = dsde.getLocation();
			pt.translate(5, 5); // offset
			window.setLocation(pt);
		});
	}
	@Override
	protected Transferable createTransferable(JComponent c) {
		final RangementCell rangementCell = (RangementCell)c;
		final BouteilleLabel l = rangementCell.draggingLabel;
		String text = l.getText();
		final DataHandler dh = new DataHandler(c, localObjectFlavor.getMimeType());
		if(text == null) {
			return dh;
		}
		final StringSelection ss = new StringSelection(text+"\n");
		return new Transferable() {
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				ArrayList<DataFlavor> list = new ArrayList<>();
				Collections.addAll(list, ss.getTransferDataFlavors());
				Collections.addAll(list, dh.getTransferDataFlavors());
				return list.toArray(dh.getTransferDataFlavors());
			}
			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				for (DataFlavor f: getTransferDataFlavors()) {
					if (flavor.equals(f)) {
						return true;
					}
				}
				return false;
			}
			@Override
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
		if(!support.isDrop()) {
			return false;
		}
		if(support.getComponent() instanceof RangementCell) {
			final RangementCell cell = (RangementCell)support.getComponent();
			if(!cell.isStock() && !cell.canImport()) {
				return false;
			}
		}
		return true;
	}
	
	@Override 
	public int getSourceActions(JComponent c) {
		final RangementCell rangementCell = (RangementCell)c;
		label.setIcon(rangementCell.draggingLabel.getIcon());
		label.setText(rangementCell.draggingLabel.getText());
		window.pack();
		final Point pt = rangementCell.draggingLabel.getLocation();
		SwingUtilities.convertPointToScreen(pt, rangementCell);
		window.setLocation(pt);
		window.setVisible(true);
		return MOVE;
	}
	
	@Override 
	public boolean importData(TransferSupport support) {
		if(!canImport(support)) {
			return false;
		}
		final RangementCell target = (RangementCell)support.getComponent();
		if(!target.isStock() && !target.canImport()) {
			return false;
		}
		try {
			final RangementCell src = (RangementCell)support.getTransferable().getTransferData(localObjectFlavor);
			final BouteilleLabel bouteilleLabel = new BouteilleLabel(src.draggingLabel.getBouteille());
			final Bouteille bouteille = bouteilleLabel.getBouteille();
			bouteille.setLigne(target.getRow()+1);
			bouteille.setColonne(target.getColumn()+1);
			bouteille.setNumLieu(target.getPlaceNum()+1);
			bouteille.setEmplacement(target.getPlaceName());
			target.addBottle(bouteilleLabel);
			src.draggingLabel.removeBouteille();
			RangementUtils.putTabStock();
			if(target.isCaisse()) {
				CellarOrganizerPanel.CELLS_LIST.add(target.createNewCell());
			}
			target.revalidate();
			if(!target.isStock()) {
				Program.getStorage().addHistory(History.MODIFY, bouteille);
			}
			return true;
		} catch(UnsupportedFlavorException | IOException ignored) {
		}
		return false;
	}
	
	@Override 
	protected void exportDone(JComponent c, Transferable data, int action) {
		RangementCell src = (RangementCell)c;
		if(action == TransferHandler.MOVE) {
			src.remove(src.draggingLabel);
			src.revalidate();
			src.repaint();
		}
		src.draggingLabel = null;
		window.setVisible(false);
	}
	
}
