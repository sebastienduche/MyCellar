package mycellar;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import mycellar.core.IAddVin;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.3
 * @since 17/05/17
 */

public class CellarOrganizerPanel extends JPanel implements ITabListener {

	private static final long serialVersionUID = -1239228393406479587L;
	private MouseListener handler = new Handler();
	private LabelTransferHandler th = new LabelTransferHandler();

	private LinkedList<JPanel[][]> places = new LinkedList<JPanel[][]>();
	
	private ArrayList<RangementCell> cellsList = new ArrayList<RangementCell>();
	
	private JPanel placePanel = new JPanel();
	private LinkedList<Rangement> armoires = new LinkedList<Rangement>();
	private MyCellarComboBox<Rangement> comboRangement;
	private Rangement rangement;
	private RangementCell stock;
	private MyCellarButton move = new MyCellarButton(new MoveAction());
	private IAddVin addvin;
	
	private boolean cellChooser = false;
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
	
	public void init() {
		
		if(cellChooser)
			setLayout(new MigLayout("","[grow]","[][]20px[grow]"));
		else
			setLayout(new MigLayout("","[grow]20px[200:200:200]","[][]20px[grow]"));
		
		placePanel.setLayout(new MigLayout("","grow", ""));
		comboRangement = new MyCellarComboBox<Rangement>();
		for(Rangement r : Program.getCave()) {
			if(addvin == null || !r.isCaisse()) {
				armoires.add(r);
				comboRangement.addItem(r);
			}
		}
		
		comboRangement.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					rangement.putTabStock();
					Rangement r = (Rangement)comboRangement.getSelectedItem();
					setRangement(r);
				}
			}
		});
		
		if(!armoires.isEmpty())
			setRangement(armoires.getFirst());
		
		stock = new RangementCell(handler, th);
		JScrollPane scrollStock = new JScrollPane(stock);
		scrollStock.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollStock.setBorder(BorderFactory.createTitledBorder(Program.getLabel("ManagePlace.Stock")));
		add(new MyCellarLabel(Program.getLabel("ManagePlace.SelectPlace")), "split 3");
		add(comboRangement, "gapleft 10px");
		add(move, "gapleft 10px, wrap");
		move.setEnabled(false);
		if(cellChooser)
			add(new MyCellarLabel(Program.getLabel("ManagePlace.ChooseCell")),"wrap");
		else
			add(new MyCellarLabel(Program.getLabel("ManagePlace.StockDescription")),"wrap");
		add(new JScrollPane(placePanel), "grow");
		if(!cellChooser)
			add(scrollStock, "grow");
	}

	private void setRangement(final Rangement rangement) {
		this.rangement = rangement;
		move.setEnabled(rangement.isCaisse());
		SwingUtilities.invokeLater(() -> {
			cellsList.clear();
			placePanel.removeAll();
			places.clear();
			if(rangement.isCaisse()) {
				HashMap<Integer, Integer> mapEmplSize = new HashMap<Integer, Integer>();
				for(int i = 0; i<rangement.getNbEmplacements(); i++){
					int empl = i + rangement.getStartCaisse();
					mapEmplSize.put(empl, 0);
					JPanel[][] place;
					int nb = rangement.getNbCaseUseCaisse(empl);
					places.add(place = new JPanel[nb][1]);
					JPanel panelCellar = new JPanel(new GridLayout(nb,1));
					
					for(int k=0; k<place.length; k++) {
						JPanel panel;
						RangementCell cell;
						if(cellChooser)
							cell = new RangementCell(rangement.getNom(), empl, k, 0);
						else
							cell = new RangementCell(handler, th, rangement.getNom(), empl, k, 0);
						place[k][0] = panel = cell;
						cellsList.add(cell);
						panelCellar.add(panel);
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
									cell = new RangementCell(rangement.getNom(), i, k, j);
								else
									cell = new RangementCell(handler, th, rangement.getNom(), i, k, j);
								place[k][j] = panel = cell;
								cellsList.add(cell);
							} else
								place[k][j] = panel = new JPanel();
							panelCellar.add(panel);
						}
					}
					placePanel.add(new MyCellarLabel(Program.getLabel("Infos029")+" "+(i+1)), i>0 ? "newline, gaptop 30, wrap" : "wrap");
					placePanel.add(panelCellar, "grow");
				}

				for(Bouteille b : Program.getStorage().getAllList()) {
					if(b.getEmplacement().endsWith(rangement.getNom())) {
						JPanel[][] place = places.get(b.getNumLieu() - 1);
						((RangementCell)place[b.getLigne()-1][b.getColonne()-1]).addBottle(new BouteilleLabel(b));
					}
				}
			}
			
			if(cellChooser) {
				for(RangementCell cell : cellsList) {
					cell.initButton();
				}
			}
			placePanel.updateUI();
		});
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		if(rangement != null)
			rangement.putTabStock();
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
				for(Rangement rangement : Program.getCave())
					rangement.putTabStock();
			}	
		}
		if(cellChooser) {
			int count = 0;
			RangementCell selectedCell = null;
			for(RangementCell cell : cellsList) {
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
				addvin.selectPlace(rangement, selectedCell.getPlace(), selectedCell.getRow(), selectedCell.getColumn());
			}
			Program.chooseCell = null;
		}
		return true;
	}

	@Override
	public void tabClosed() {
		Start.updateMainPanel();
	}
	
	public void setAddVin(IAddVin addvin) {
		this.addvin = addvin;
	}
	
	public void setUpdateView(){
		updateView = true;
	}
	
	public void updateView() {
		if(!updateView)
			return;
		SwingUtilities.invokeLater(() -> {
			armoires.clear();
			comboRangement.removeAllItems();
			for(Rangement r : Program.getCave()) {
				if(addvin == null || !r.isCaisse()) {
					armoires.add(r);
					comboRangement.addItem(r);
				}
			}
		});
			setRangement(rangement);
	}
	
	class MoveAction extends AbstractAction {

		private static final long serialVersionUID = 6973442058662866086L;
		
		public MoveAction() {
			super(Program.getLabel("ManageStock.MoveAll"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for(RangementCell cell: cellsList) {
				BouteilleLabel bottle = cell.getBottleLabel();
				bottle.getBouteille().setEmplacement("");
				stock.addBottle(bottle);
			}
			rangement.putTabStock();
			placePanel.updateUI();
			stock.updateUI();
		}
		
	}
}


class RangementCell extends JPanel {
	public BouteilleLabel draggingLabel;
	private BouteilleLabel bottle;
	private boolean stock;
	private int place, row, column;
	private String placeName;
	private JToggleButton select = new JToggleButton();
	private static final long serialVersionUID = -3180057277279430308L;

	public RangementCell(MouseListener listener, TransferHandler handler, String placeName, int place, int row, int column) {
		stock = false;
		addMouseListener(listener);
		setTransferHandler(handler);
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new MigLayout("","0px[align left, ::100, grow]0px","0px[align center, 20::, grow]0px"));
		this.row = row;
		this.column = column;
		this.place = place;
		this.placeName = placeName;
	}

	public RangementCell(MouseListener listener, TransferHandler handler) {
		stock = true;
		this.placeName = "";
		addMouseListener(listener);
		setTransferHandler(handler);
		setLayout(new MigLayout("","[align left, 200:200:200]","0px[]"));
	}
	
	public RangementCell(String placeName, int place, int row, int column) {
		stock = false;
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new MigLayout("","0px[align left, ::100, grow]0px","0px[align center, 20::, grow]0px"));
		this.row = row;
		this.column = column;
		this.place = place;
		this.placeName = placeName;
		select.setText(Program.getLabel("ManagePlace.Select"));
	}

	public boolean isStock() {
		return stock;
	}
	
	public void initButton() {
		add(select, "newline");
	}
	
	public boolean isToggle() {
		return select.isSelected();
	}
	
	public void clearToggle() {
		select.setSelected(false);
	}

	public void addBottle(BouteilleLabel comp) {
		if(!stock)
			bottle = comp;
		add(comp, stock ? "wrap" : "grow, gapright 0px");
	}
	
	public BouteilleLabel getBottleLabel() {
		return bottle;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
	public int getPlace() {
		return place;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
}

class BouteilleLabel extends JPanel {

	private static final long serialVersionUID = -3982812616929975895L;
	private Bouteille bouteille;
	private MyCellarLabel label = new MyCellarLabel();

	public BouteilleLabel(final Bouteille bouteille) {
		super();
		setLayout(new MigLayout("","5px[100:100:100][10:10:10]0px","0px[align center, grow]0px"));
		this.bouteille = bouteille;
		if(bouteille.isWhiteWine())
			label.setIcon(MyCellarImage.WHITEWINE);
		else if(bouteille.isPinkWine())
			label.setIcon(MyCellarImage.PINKWINE);
		else
			label.setIcon(MyCellarImage.BLACKWINE);
		label.setText("<html>"+bouteille.getNom()+"</html>");
		add(label, "grow");
		add(new PanelCloseButton(){
			private static final long serialVersionUID = 3495975676025406824L;

			void actionPerformed() {
				String mess = MessageFormat.format(Program.getLabel("Main.DeleteWine"), bouteille.getNom());
				if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, mess)) {
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
}

class Handler extends MouseAdapter {
	@Override
	public void mousePressed(MouseEvent e) {
		RangementCell p = (RangementCell)e.getSource();
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
	
	public LabelTransferHandler() {
		localObjectFlavor = new ActivationDataFlavor(
				RangementCell.class, DataFlavor.javaJVMLocalObjectMimeType, "JPanel");
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
		RangementCell p = (RangementCell)c;
		BouteilleLabel l = p.draggingLabel;
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
		if(!support.isDrop()) {
			return false;
		}
		if(support.getComponent() instanceof RangementCell) {
			if(!((RangementCell)support.getComponent()).isStock() && ((RangementCell)support.getComponent()).getComponentCount() > 0)
				return false;
		}
		return true;
	}
	
	@Override 
	public int getSourceActions(JComponent c) {
		RangementCell p = (RangementCell)c;
		label.setIcon(p.draggingLabel.getIcon());
		label.setText(p.draggingLabel.getText());
		window.pack();
		Point pt = p.draggingLabel.getLocation();
		SwingUtilities.convertPointToScreen(pt, p);
		window.setLocation(pt);
		window.setVisible(true);
		return MOVE;
	}
	
	@Override 
	public boolean importData(TransferSupport support) {
		if(!canImport(support))
			return false;
		RangementCell target = (RangementCell)support.getComponent();
		if(target.getComponentCount() > 0 && !target.isStock())
			return false;
		try {
			RangementCell src = (RangementCell)support.getTransferable().getTransferData(localObjectFlavor);
			BouteilleLabel l = new BouteilleLabel(src.draggingLabel.getBouteille());
			l.getBouteille().setLigne(target.getRow()+1);
			l.getBouteille().setColonne(target.getColumn()+1);
			l.getBouteille().setNumLieu(target.getPlace()+1);
			l.getBouteille().setEmplacement(target.getPlaceName());
			target.addBottle(l);
			target.revalidate();
			if(!target.isStock())
				Program.getStorage().addHistory(History.MODIFY, l.getBouteille());
			return true;
		} catch(UnsupportedFlavorException ufe) {
		} catch(java.io.IOException ioe) {
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