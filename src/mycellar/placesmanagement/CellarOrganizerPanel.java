package mycellar.placesmanagement;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.core.IMyCellar;
import mycellar.core.IPlace;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarObject;
import mycellar.core.PanelCloseButton;
import mycellar.core.UpdateViewType;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.TabEvent;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.places.IAbstractPlace;
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
import java.util.stream.Collectors;

import static mycellar.ProgramConstants.SPACE;
import static mycellar.ProgramConstants.TEMP_PLACE;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2014
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 5.3
 * @since 27/05/22
 */

public class CellarOrganizerPanel extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  private static final long serialVersionUID = -1239228393406479587L;
  protected final List<RangementCell> rangementCells = new ArrayList<>();
  private final MouseListener handler = new Handler();
  private final List<JPanel[][]> places = new LinkedList<>();
  private final JPanel placePanel = new JPanel();
  private final LinkedList<Rangement> complexPlaces = new LinkedList<>();
  private final MyCellarButton moveAllButton = new MyCellarButton("ManageStock.MoveAll", LabelProperty.PLURAL, new MoveAction());
  private final boolean cellChooser;
  private LabelTransferHandler labelTransferHandler;
  private MyCellarComboBox<Rangement> comboRangement;
  private Rangement rangement;
  private RangementCell stock;
  private IPlace iPlace;

  private boolean updateView = false;
  private UpdateViewType updateViewType;

  public CellarOrganizerPanel() {
    cellChooser = false;
    init();
  }

  public CellarOrganizerPanel(IPlace iPlace) {
    cellChooser = true;
    this.iPlace = iPlace;
    init();
  }

  Rangement getRangement() {
    return rangement;
  }

  void setRangement(final Rangement rangement) {
    if (rangement == null) {
      return;
    }
    this.rangement = rangement;
    moveAllButton.setEnabled(rangement.isSimplePlace());
    SwingUtilities.invokeLater(() -> {
      rangementCells.clear();
      placePanel.removeAll();
      places.clear();
      if (Program.EMPTY_PLACE.equals(rangement)) {
        placePanel.updateUI();
        return;
      }
      if (rangement.isSimplePlace()) {
        HashMap<Integer, Integer> mapEmplSize = new HashMap<>();
        for (int i = 0; i < rangement.getNbParts(); i++) {
          int empl = i + rangement.getStartSimplePlace();
          mapEmplSize.put(empl, 0);
          int nb = rangement.getTotalCellUsed(i);
          if (nb == 0) {
            nb = 1;
          }
          JPanel[][] place;
          places.add(place = new JPanel[nb][1]);
          JPanel panelCellar = new JPanel(new MigLayout("", "grow"));

          for (int k = 0; k < place.length; k++) {
            RangementCell cell;
            if (cellChooser) {
              cell = new RangementCell(rangement, empl, k, 0, panelCellar);
            } else {
              cell = new RangementCell(handler, labelTransferHandler, rangement, empl, k, 0, panelCellar, this);
            }
            place[k][0] = cell;
            rangementCells.add(cell);
            panelCellar.add(cell, "growx, wrap");
          }
          placePanel.add(new MyCellarSimpleLabel(getLabel("Storage.Shelve") + SPACE + empl), i > 0 ? "newline, gaptop 30, wrap" : "wrap");
          placePanel.add(panelCellar, "grow");
        }

        Program.getStorage().getAllList().stream()
            .filter(bouteille -> bouteille.getEmplacement().endsWith(rangement.getName())).collect(Collectors.toList())
            .forEach(b -> {
              JPanel[][] place = places.get(b.getNumLieu() - rangement.getStartSimplePlace());
              int line = mapEmplSize.get(b.getNumLieu());
              ((RangementCell) place[line++][0]).addBottle(new MyCellarObjectDraggingLabel(b));
              mapEmplSize.put(b.getNumLieu(), line);
            });
      } else {
        for (int i = 0; i < rangement.getNbParts(); i++) {
          JPanel[][] place;
          places.add(place = new JPanel[rangement.getLineCountAt(i)][rangement.getMaxColumCountAt(i)]);
          JPanel panelCellar = new JPanel(new GridLayout(rangement.getLineCountAt(i), rangement.getNbColumnsStock()));

          for (int k = 0; k < place.length; k++) {
            for (int j = 0; j < place[k].length; j++) {
              JPanel panel;
              if (rangement.isExistingCell(i, k, j)) {
                RangementCell cell;
                if (cellChooser) {
                  cell = new RangementCell(rangement, i, k, j, panelCellar);
                } else {
                  cell = new RangementCell(handler, labelTransferHandler, rangement, i, k, j, panelCellar, this);
                }
                place[k][j] = panel = cell;
                rangementCells.add(cell);
              } else {
                place[k][j] = panel = new JPanel();
              }
              panelCellar.add(panel);
            }
          }
          placePanel.add(new MyCellarSimpleLabel(getLabel("Storage.Shelve") + SPACE + (i + 1)), i > 0 ? "newline, gaptop 30, wrap" : "wrap");
          placePanel.add(panelCellar, "grow");
        }

        Program.getStorage().getAllList().stream()
            .filter(bouteille -> bouteille.getEmplacement().endsWith(rangement.getName()))
            .forEach(bouteille -> {
              JPanel[][] place = places.get(bouteille.getNumLieu() - 1);
              ((RangementCell) place[bouteille.getLigne() - 1][bouteille.getColonne() - 1]).addBottle(new MyCellarObjectDraggingLabel(bouteille));
            });
      }

      if (cellChooser) {
        rangementCells.forEach(RangementCell::initButton);
      }
      placePanel.updateUI();
    });
  }

  private void init() {
    labelTransferHandler = new LabelTransferHandler(this);
    if (cellChooser) {
      setLayout(new MigLayout("", "[grow]", "[][]20px[grow]"));
    } else {
      setLayout(new MigLayout("", "[grow]20px[200:200:200]", "[][]20px[grow]"));
    }

    placePanel.setLayout(new MigLayout("", "grow", ""));
    comboRangement = new MyCellarComboBox<>();
    comboRangement.addItem(Program.EMPTY_PLACE);
    if (iPlace == null) {
      complexPlaces.add(Program.EMPTY_PLACE);
    }
    initPlacesCombo();

    comboRangement.addItemListener((item) -> {
      if (item.getStateChange() == ItemEvent.SELECTED) {
        setRangement((Rangement) comboRangement.getSelectedItem());
      }
    });

    if (!complexPlaces.isEmpty()) {
      final Rangement r = complexPlaces.getFirst();
      comboRangement.setSelectedItem(r);
      setRangement(r);
    }

    stock = new RangementCell(handler, labelTransferHandler);
    JScrollPane scrollStock = new JScrollPane(stock);
    scrollStock.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollStock.setBorder(BorderFactory.createTitledBorder(getLabel("ManagePlace.Stock")));
    add(new MyCellarLabel("ManagePlace.SelectPlace"), "split 3");
    add(comboRangement, "gapleft 10px");
    add(moveAllButton, "gapleft 10px, wrap");
    moveAllButton.setEnabled(false);
    if (cellChooser) {
      add(new MyCellarLabel("ManagePlace.ChooseCell", LabelProperty.THE_SINGLE), "wrap");
    } else {
      add(new MyCellarLabel("ManagePlace.StockDescription", LabelProperty.PLURAL), "wrap");
    }
    add(new JScrollPane(placePanel), "grow");
    if (!cellChooser) {
      add(scrollStock, "grow");
    }
  }

  private void initPlacesCombo() {
    for (Rangement rangement1 : Program.getPlaces()) {
      if (iPlace == null || !rangement1.isSimplePlace()) {
        complexPlaces.add(rangement1);
        comboRangement.addItem(rangement1);
      }
    }
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    if (stock.getComponentCount() > 0) {
      if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), getError("ManageStock.ConfirmLost", LabelProperty.PLURAL), getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
        return false;
      }
      if (stock.getComponentCount() > 0) {
        ProgramPanels.updateAllPanels();
      }
      for (int i = 0; i < stock.getComponentCount(); i++) {
        Component c = stock.getComponent(i);
        if (c instanceof MyCellarObjectDraggingLabel) {
          final MyCellarObject myCellarObject = ((MyCellarObjectDraggingLabel) c).getMyCellarObject();
          Program.getStorage().addHistory(HistoryState.DEL, myCellarObject);
          Program.getStorage().getAllList().remove(myCellarObject);
          Program.setToTrash(myCellarObject);
          ProgramPanels.removeObjectTab(myCellarObject);
        }
      }
    }
    if (cellChooser) {
      int count = 0;
      RangementCell selectedCell = null;
      for (RangementCell cell : rangementCells) {
        if (cell.isToggle()) {
          selectedCell = cell;
          count++;
        }
      }
      if (count > 1) {
        Erreur.showSimpleErreur(getError("ManageStock.TooManySelected"));
        return false;
      }
      if (selectedCell != null) {
        iPlace.selectPlace(new Place.PlaceBuilder(rangement)
            .withNumPlace(selectedCell.getPlaceNum())
            .withLine(selectedCell.getRow())
            .withColumn(selectedCell.getColumn())
            .build());
      }
      ProgramPanels.deleteChooseCellPanel(iPlace);
    }
    return true;
  }

  @Override
  public void tabClosed() {
    Start.getInstance().updateMainPanel();
    rangementCells.clear();
    stock.removeAll();
    places.clear();
    placePanel.removeAll();
  }

  public IPlace getIPlace() {
    return iPlace;
  }

  @Override
  public void setUpdateViewType(UpdateViewType updateViewType) {
    updateView = true;
    this.updateViewType = updateViewType;
  }

  @Override
  public void updateView() {
    if (!updateView) {
      return;
    }
    updateView = false;
    if (updateViewType != UpdateViewType.PLACE && updateViewType != UpdateViewType.ALL) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      complexPlaces.clear();
      comboRangement.removeAllItems();
      comboRangement.addItem(Program.EMPTY_PLACE);
      initPlacesCombo();
    });
    setRangement(rangement);
  }

  private class MoveAction extends AbstractAction {

    private static final long serialVersionUID = 6973442058662866086L;

    private MoveAction() {
      super(getLabel("ManageStock.MoveAll", LabelProperty.PLURAL));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      for (RangementCell cell : rangementCells) {
        MyCellarObjectDraggingLabel bottleLabel = cell.getBottleLabel();
        if (bottleLabel != null) {
          final MyCellarObject myCellarObject = bottleLabel.getMyCellarObject();
          if (myCellarObject != null) {
            myCellarObject.setEmplacement(TEMP_PLACE);
            myCellarObject.setNumLieu(1);
            myCellarObject.updateStatus();
            stock.addBottle(bottleLabel);
          }
        }
        cell.removeBottle();
      }
      placePanel.updateUI();
      stock.updateUI();
    }
  }
}


final class RangementCell extends JPanel {
  private static final long serialVersionUID = -3180057277279430308L;
  public static final int WIDTH_SIMPLE_PLACE = 400;
  public static final int WIDTH_COMPLEX_PLACE = 100;
  private final boolean stock;
  private final JToggleButton select = new JToggleButton();
  private final int placeNum;
  private final int row;
  private final int column;
  MyCellarObjectDraggingLabel draggingLabel;
  private MyCellarObjectDraggingLabel bottleLabel;
  private IAbstractPlace place;
  private JPanel parent;
  private CellarOrganizerPanel cellarOrganizerPanel;

  RangementCell(MouseListener listener, TransferHandler handler, IAbstractPlace place, int placeNum, int row, int column, JPanel parent, CellarOrganizerPanel cellarOrganizerPanel) {
    this.place = place;
    this.placeNum = placeNum;
    this.row = row;
    this.column = column;
    this.parent = parent;
    this.cellarOrganizerPanel = cellarOrganizerPanel;
    stock = false;
    addMouseListener(listener);
    setTransferHandler(handler);
    setBorder(BorderFactory.createEtchedBorder());
    int width = place.isSimplePlace() ? WIDTH_SIMPLE_PLACE : WIDTH_COMPLEX_PLACE;
    setLayout(new MigLayout("", "0px[align left, ::" + width + ", grow]0px", "0px[align center, 20::, grow]0px"));
  }

  RangementCell(MouseListener listener, TransferHandler handler) {
    stock = true;
    placeNum = 1;
    row = -1;
    column = -1;
    addMouseListener(listener);
    setTransferHandler(handler);
    setLayout(new MigLayout("", "[align left, 200:200:200]", "0px[]"));
  }

  RangementCell(Rangement place, int placeNum, int row, int column, JPanel parent) {
    this.place = place;
    this.placeNum = placeNum;
    this.row = row;
    this.column = column;
    this.parent = parent;
    stock = false;
    setBorder(BorderFactory.createEtchedBorder());
    int width = place.isSimplePlace() ? WIDTH_SIMPLE_PLACE : WIDTH_COMPLEX_PLACE;
    setLayout(new MigLayout("", "0px[align left, ::" + width + ", grow]0px", "0px[align center, 20::, grow]0px"));
    select.setText(getLabel("ManagePlace.Select"));
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

  void addBottle(MyCellarObjectDraggingLabel bouteille) {
    if (!stock) {
      bottleLabel = bouteille;
    }
    add(bouteille, stock ? "wrap" : "grow, gapright 0px");
  }

  void removeBottle() {
    if (!stock) {
      bottleLabel = null;
    }
  }

  MyCellarObjectDraggingLabel getBottleLabel() {
    return bottleLabel;
  }

  public int getRow() {
    if (isTemporaryPlace() || (place != null && place.isSimplePlace())) {
      return -1;
    }
    return row + 1;
  }

  public int getColumn() {
    if (isTemporaryPlace() || (place != null && place.isSimplePlace())) {
      return -1;
    }
    return column + 1;
  }

  int getPlaceNum() {
    if (isTemporaryPlace()) {
      return placeNum - 1;
    }
    if (place != null && place.isSimplePlace()) {
      return placeNum - place.getStartSimplePlace();
    }
    return placeNum + 1;
  }

  public IAbstractPlace getPlace() {
    return place;
  }

  public void setPlace(IAbstractPlace place) {
    this.place = place;
  }

  String getPlaceName() {
    return place != null ? place.getName() : TEMP_PLACE;
  }

  public boolean isTemporaryPlace() {
    return TEMP_PLACE.equalsIgnoreCase(getPlaceName());
  }

  RangementCell createNewCell() {
    RangementCell rangementCell = new RangementCell(getMouseListeners()[0], getTransferHandler(), place, placeNum, row + 1, 0, parent, cellarOrganizerPanel);
    parent.add(rangementCell, "newline, growx");
    return rangementCell;
  }

  void updateParent() {
    cellarOrganizerPanel.setRangement(cellarOrganizerPanel.getRangement());
  }

  public boolean isCaisse() {
    return place != null && place.isSimplePlace();
  }

  @Override
  public String toString() {
    return "RangementCell [draggingLabel=" + draggingLabel + ", bottle=" + bottleLabel + ", stock=" + stock + ", placeNum=" + placeNum + ", row=" + row
        + ", column=" + column + ", place=" + place + ", parent=" + (parent != null ? "Yes" : "No") + "]";
  }

  boolean canImport() {
    return getBottleLabel() == null || getBottleLabel().getMyCellarObject() == null;
  }

}

final class MyCellarObjectDraggingLabel extends JPanel {

  private static final long serialVersionUID = -3982812616929975895L;
  private final MyCellarSimpleLabel label = new MyCellarSimpleLabel();
  private MyCellarObject myCellarObject;

  MyCellarObjectDraggingLabel(final MyCellarObject myCellarObject) {
    super();
    this.myCellarObject = myCellarObject;
    int width = 100;
    Rangement r = (Rangement)myCellarObject.getRangement();
    if (r != null && r.isSimplePlace()) {
      width = 400;
    }
    setLayout(new MigLayout("", "5px[" + width + ":" + width + ":" + width + "][10:10:10]0px", "0px[align center, grow]0px"));
    if (myCellarObject instanceof Bouteille) {
      if (((Bouteille) myCellarObject).isWhiteWine()) {
        label.setIcon(MyCellarImage.WHITEWINE);
      } else if (((Bouteille) myCellarObject).isPinkWine()) {
        label.setIcon(MyCellarImage.PINKWINE);
      } else {
        label.setIcon(MyCellarImage.BLACKWINE);
      }
    }
    label.setText("<html>" + myCellarObject.getNom() + "</html>");
    add(label, "grow");
    add(new PanelCloseButton() {
      private static final long serialVersionUID = 3495975676025406824L;

      @Override
      public void perform() {
        String mess = MessageFormat.format(MyCellarLabelManagement.getLabel("Main.DeleteWine", LabelProperty.THE_SINGLE), myCellarObject.getNom());
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), mess, MyCellarLabelManagement.getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION)) {
          Component parent = MyCellarObjectDraggingLabel.this.getParent();
          if (parent instanceof RangementCell) {
            ((RangementCell) parent).remove(MyCellarObjectDraggingLabel.this);
            ((RangementCell) parent).updateUI();
            Program.getStorage().addHistory(HistoryState.DEL, myCellarObject);
            try {
              final Rangement rangement = (Rangement)myCellarObject.getRangement();
              if (rangement != null) {
                rangement.removeObject(myCellarObject);
              } else {
                Program.getStorage().deleteWine(myCellarObject);
              }
              ProgramPanels.getSearch().ifPresent(search -> {
                search.removeObject(myCellarObject);
                search.updateTable();
              });
              ProgramPanels.updateAllPanels();
            } catch (MyCellarException e) {
              Program.showException(e);
            }
            Program.setToTrash(myCellarObject);
            ProgramPanels.removeObjectTab(myCellarObject);
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

  public MyCellarObject getMyCellarObject() {
    return myCellarObject;
  }

  void removeObject() {
    myCellarObject = null;
  }

  @Override
  public String toString() {
    return "BouteilleLabel [object=" + myCellarObject + "]";
  }
}

class Handler extends MouseAdapter {
  @Override
  public void mousePressed(MouseEvent e) {
    final RangementCell rangementCell = (RangementCell) e.getSource();
    Component c = SwingUtilities.getDeepestComponentAt(rangementCell, e.getX(), e.getY());
    if (c != null && c.getParent() instanceof MyCellarObjectDraggingLabel) {
      rangementCell.draggingLabel = (MyCellarObjectDraggingLabel) c.getParent();
      rangementCell.getTransferHandler().exportAsDrag(rangementCell, e, TransferHandler.MOVE);
    }
  }
}

class LabelTransferHandler extends TransferHandler {
  private static final long serialVersionUID = -4338469857987642038L;
  private final DataFlavor localObjectFlavor;
  private final MyCellarSimpleLabel label = new MyCellarSimpleLabel() {
    private static final long serialVersionUID = 5065631180392050633L;

    @Override
    public boolean contains(int x, int y) {
      return false;
    }
  };
  private final JWindow window = new JWindow();
  private final CellarOrganizerPanel cellarOrganizerPanel;

  LabelTransferHandler(CellarOrganizerPanel cellarOrganizerPanel) {
    this.cellarOrganizerPanel = cellarOrganizerPanel;
    localObjectFlavor = new ActivationDataFlavor(
        RangementCell.class, DataFlavor.javaJVMLocalObjectMimeType, "JPanel");
    window.add(label);
    window.setAlwaysOnTop(true);
    window.setBackground(new Color(0, true));
    DragSource.getDefaultDragSource().addDragSourceMotionListener((dsde) -> {
      Point pt = dsde.getLocation();
      pt.translate(5, 5); // offset
      window.setLocation(pt);
    });
  }

  @Override
  protected Transferable createTransferable(JComponent c) {
    final RangementCell rangementCell = (RangementCell) c;
    final MyCellarObjectDraggingLabel bouteilleLabel = rangementCell.draggingLabel;
    String text = bouteilleLabel.getText();
    final DataHandler dh = new DataHandler(c, localObjectFlavor.getMimeType());
    if (text == null) {
      return dh;
    }
    final StringSelection ss = new StringSelection(text + "\n");
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
        for (DataFlavor f : getTransferDataFlavors()) {
          if (flavor.equals(f)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(localObjectFlavor)) {
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
    final RangementCell rangementCell = (RangementCell) c;
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
    if (!canImport(support)) {
      final RangementCell cell = (RangementCell) support.getComponent();
      cell.updateParent();
      return false;
    }
    final RangementCell target = (RangementCell) support.getComponent();
    if (!target.isStock() && !target.canImport()) {
      target.updateParent();
      return false;
    }
    try {
      final RangementCell src = (RangementCell) support.getTransferable().getTransferData(localObjectFlavor);
      final MyCellarObjectDraggingLabel bouteilleLabel = new MyCellarObjectDraggingLabel(src.draggingLabel.getMyCellarObject());
      final MyCellarObject bouteille = bouteilleLabel.getMyCellarObject();
      target.setPlace(Program.getPlaceByName(target.getPlaceName()));
      bouteille.setEmplacement(target.getPlaceName());
      bouteille.setLigne(target.getRow());
      bouteille.setColonne(target.getColumn());
      bouteille.setNumLieu(target.getPlaceNum());
      bouteille.updateStatus();
      target.addBottle(bouteilleLabel);
      src.draggingLabel.removeObject();
      RangementUtils.putTabStock();
      if (target.isCaisse()) {
        cellarOrganizerPanel.rangementCells.add(target.createNewCell());
      }
      target.revalidate();
      if (!target.isStock()) {
        Program.getStorage().addHistory(HistoryState.MODIFY, bouteille);
      }
      return true;
    } catch (UnsupportedFlavorException | IOException ignored) {
    }
    return false;
  }

  @Override
  protected void exportDone(JComponent c, Transferable data, int action) {
    RangementCell src = (RangementCell) c;
    if (action != TransferHandler.COPY) {
      src.remove(src.draggingLabel);
      src.revalidate();
      src.repaint();
    }
    src.draggingLabel = null;
    window.setVisible(false);
  }

}
