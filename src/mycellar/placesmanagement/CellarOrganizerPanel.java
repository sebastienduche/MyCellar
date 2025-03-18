package mycellar.placesmanagement;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.core.IMyCellar;
import mycellar.core.IPlacePosition;
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
import mycellar.frame.MainFrame;
import mycellar.general.ProgramPanels;
import mycellar.general.ResourceErrorKey;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.placesmanagement.places.SimplePlace;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static mycellar.ProgramConstants.TEMP_PLACE;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getErrorWithProperty;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;
import static mycellar.general.ResourceErrorKey.MANAGESTOCK_CONFIRMLOST;
import static mycellar.general.ResourceKey.MAIN_ASKCONFIRMATION;
import static mycellar.general.ResourceKey.MAIN_DELETEWINE;
import static mycellar.general.ResourceKey.MANAGEPLACE_CHOOSECELL;
import static mycellar.general.ResourceKey.MANAGEPLACE_SELECT;
import static mycellar.general.ResourceKey.MANAGEPLACE_SELECTPLACE;
import static mycellar.general.ResourceKey.MANAGEPLACE_STOCK;
import static mycellar.general.ResourceKey.MANAGEPLACE_STOCKDESCRIPTION;
import static mycellar.general.ResourceKey.MANAGESTOCK_MOVEALL;
import static mycellar.general.ResourceKey.STORAGE_SHELVENUMBER;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2014
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 6.6
 * @since 18/03/25
 */

public class CellarOrganizerPanel extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  protected final List<RangementCell> rangementCells = new ArrayList<>();
  private final MouseListener handler = new Handler();
  private final List<JPanel[][]> places = new LinkedList<>();
  private final JPanel placePanel = new JPanel();
  private final LinkedList<AbstractPlace> complexPlaces = new LinkedList<>();
  private final MyCellarButton moveAllButton = new MyCellarButton(MANAGESTOCK_MOVEALL, LabelProperty.PLURAL, new MoveAction());
  private final boolean cellChooser;
  private LabelTransferHandler labelTransferHandler;
  private MyCellarComboBox<AbstractPlace> abstractPlaceCombo;
  private AbstractPlace abstractPlace;
  private RangementCell stock;
  private IPlacePosition iPlace;

  private boolean updateView = false;
  private UpdateViewType updateViewType;

  public CellarOrganizerPanel() {
    cellChooser = false;
    init();
  }

  public CellarOrganizerPanel(IPlacePosition iPlace) {
    cellChooser = true;
    this.iPlace = iPlace;
    init();
  }

  AbstractPlace getAbstractPlace() {
    return abstractPlace;
  }

  void setAbstractPlace(final AbstractPlace abstractPlace) {
    if (abstractPlace == null) {
      return;
    }
    this.abstractPlace = abstractPlace;
    moveAllButton.setEnabled(abstractPlace.isSimplePlace());
    SwingUtilities.invokeLater(() -> {
      rangementCells.clear();
      placePanel.removeAll();
      places.clear();
      if (Program.EMPTY_PLACE.equals(abstractPlace)) {
        placePanel.updateUI();
        return;
      }
      if (abstractPlace.isSimplePlace()) {
        SimplePlace simplePlace = (SimplePlace) abstractPlace;
        HashMap<Integer, Integer> mapEmplSize = new HashMap<>();
        for (int i = 0; i < simplePlace.getPartCount(); i++) {
          int empl = i + simplePlace.getPartNumberIncrement();
          mapEmplSize.put(empl, 0);
          int nb = simplePlace.getCountCellUsed(i);
          if (nb == 0) {
            nb = 1;
          }
          JPanel[][] place;
          places.add(place = new JPanel[nb][1]);
          JPanel panelCellar = new JPanel(new MigLayout("", "grow"));

          for (int k = 0; k < place.length; k++) {
            RangementCell cell;
            if (cellChooser) {
              cell = new RangementCell(simplePlace, empl, k, 0, panelCellar);
            } else {
              cell = new RangementCell(handler, labelTransferHandler, simplePlace, empl, k, 0, panelCellar, this);
            }
            place[k][0] = cell;
            rangementCells.add(cell);
            panelCellar.add(cell, "growx, wrap");
          }
          placePanel.add(new MyCellarSimpleLabel(getLabel(STORAGE_SHELVENUMBER, empl)), i > 0 ? "newline, gaptop 30, wrap" : "wrap");
          placePanel.add(panelCellar, "grow");
        }

        Program.getStorage().getAllList().stream()
            .filter(bouteille -> bouteille.getEmplacement().endsWith(simplePlace.getName())).toList()
            .forEach(b -> {
              JPanel[][] place = places.get(b.getNumLieu() - simplePlace.getPartNumberIncrement());
              int line = mapEmplSize.get(b.getNumLieu());
              if (line >= place.length) {
                throw new RuntimeException("Unable to add a bottle at index [" + line + "]: " + b);
              }
              ((RangementCell) place[line++][0]).addBottle(new MyCellarObjectDraggingLabel(b));
              mapEmplSize.put(b.getNumLieu(), line);
            });
      } else {
        ComplexPlace complexPlace = (ComplexPlace) abstractPlace;
        for (int i = 0; i < complexPlace.getPartCount(); i++) {
          JPanel[][] place;
          places.add(place = new JPanel[complexPlace.getLineCountAt(i)][complexPlace.getMaxColumCountAt(i)]);
          JPanel panelCellar = new JPanel(new GridLayout(complexPlace.getLineCountAt(i), complexPlace.getColumnCount()));

          for (int k = 0; k < place.length; k++) {
            for (int j = 0; j < place[k].length; j++) {
              JPanel panel;
              if (complexPlace.isExistingCell(i, k, j)) {
                RangementCell cell;
                if (cellChooser) {
                  cell = new RangementCell(complexPlace, i, k, j, panelCellar);
                } else {
                  cell = new RangementCell(handler, labelTransferHandler, complexPlace, i, k, j, panelCellar, this);
                }
                place[k][j] = panel = cell;
                rangementCells.add(cell);
              } else {
                place[k][j] = panel = new JPanel();
              }
              panelCellar.add(panel);
            }
          }
          placePanel.add(new MyCellarSimpleLabel(getLabel(STORAGE_SHELVENUMBER, i + 1)), i > 0 ? "newline, gaptop 30, wrap" : "wrap");
          placePanel.add(panelCellar, "grow");
        }

        Program.getStorage().getAllList().stream()
            .filter(bouteille -> bouteille.getEmplacement().endsWith(complexPlace.getName()))
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
    abstractPlaceCombo = new MyCellarComboBox<>();
    abstractPlaceCombo.addItem(Program.EMPTY_PLACE);
    if (iPlace == null) {
      complexPlaces.add(Program.EMPTY_PLACE);
    }
    initPlacesCombo();

    abstractPlaceCombo.addItemListener((item) -> {
      if (item.getStateChange() == ItemEvent.SELECTED) {
        setAbstractPlace((AbstractPlace) abstractPlaceCombo.getSelectedItem());
      }
    });

    if (!complexPlaces.isEmpty()) {
      final AbstractPlace r = complexPlaces.getFirst();
      abstractPlaceCombo.setSelectedItem(r);
      setAbstractPlace(r);
    }

    stock = new RangementCell(handler, labelTransferHandler);
    JScrollPane scrollStock = new JScrollPane(stock);
    scrollStock.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollStock.setBorder(BorderFactory.createTitledBorder(getLabel(MANAGEPLACE_STOCK)));
    add(new MyCellarLabel(MANAGEPLACE_SELECTPLACE), "split 3");
    add(abstractPlaceCombo, "gapleft 10px");
    add(moveAllButton, "gapleft 10px, wrap");
    moveAllButton.setEnabled(false);
    if (cellChooser) {
      add(new MyCellarLabel(MANAGEPLACE_CHOOSECELL, LabelProperty.THE_SINGLE, ""), "wrap");
    } else {
      add(new MyCellarLabel(MANAGEPLACE_STOCKDESCRIPTION, LabelProperty.PLURAL, ""), "wrap");
    }
    add(new JScrollPane(placePanel), "grow");
    if (!cellChooser) {
      add(scrollStock, "grow");
    }
  }

  private void initPlacesCombo() {
    for (AbstractPlace place : Program.getAbstractPlaces()) {
      if (iPlace == null || !place.isSimplePlace()) {
        complexPlaces.add(place);
        abstractPlaceCombo.addItem(place);
      }
    }
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    if (stock.getComponentCount() > 0) {
      if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), getError(MANAGESTOCK_CONFIRMLOST), getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
        return false;
      }
      if (stock.getComponentCount() > 0) {
        ProgramPanels.updateAllPanels();
      }
      for (int i = 0; i < stock.getComponentCount(); i++) {
        Component c = stock.getComponent(i);
        if (c instanceof MyCellarObjectDraggingLabel label) {
          final MyCellarObject myCellarObject = label.getMyCellarObject();
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
        Erreur.showSimpleErreur(getError(ResourceErrorKey.MANAGESTOCK_TOOMANYSELECTED));
        return false;
      }
      if (selectedCell != null) {
        iPlace.selectPlace(new PlacePosition.PlacePositionBuilder(abstractPlace)
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
    rangementCells.clear();
    stock.removeAll();
    places.clear();
    placePanel.removeAll();
  }

  public IPlacePosition getIPlace() {
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
      abstractPlaceCombo.removeAllItems();
      abstractPlaceCombo.addItem(Program.EMPTY_PLACE);
      initPlacesCombo();
    });
    setAbstractPlace(abstractPlace);
  }

  private class MoveAction extends AbstractAction {

    private MoveAction() {
      super(getLabel(MANAGESTOCK_MOVEALL));
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
  private static final int WIDTH_SIMPLE_PLACE = 400;
  private static final int WIDTH_COMPLEX_PLACE = 100;
  private final boolean stock;
  private final JToggleButton select = new JToggleButton();
  private final int placeNum;
  private final int row;
  private final int column;
  MyCellarObjectDraggingLabel draggingLabel;
  private MyCellarObjectDraggingLabel bottleLabel;
  private AbstractPlace place;
  private JPanel parent;
  private CellarOrganizerPanel cellarOrganizerPanel;

  RangementCell(MouseListener listener, TransferHandler handler, AbstractPlace place, int placeNum, int row, int column, JPanel parent, CellarOrganizerPanel cellarOrganizerPanel) {
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

  RangementCell(AbstractPlace place, int placeNum, int row, int column, JPanel parent) {
    this.place = place;
    this.placeNum = placeNum;
    this.row = row;
    this.column = column;
    this.parent = parent;
    stock = false;
    setBorder(BorderFactory.createEtchedBorder());
    int width = place.isSimplePlace() ? WIDTH_SIMPLE_PLACE : WIDTH_COMPLEX_PLACE;
    setLayout(new MigLayout("", "0px[align left, ::" + width + ", grow]0px", "0px[align center, 20::, grow]0px"));
    select.setText(getLabel(MANAGEPLACE_SELECT));
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
      return placeNum - ((SimplePlace) place).getPartNumberIncrement();
    }
    return placeNum + 1;
  }

  public AbstractPlace getPlace() {
    return place;
  }

  public void setPlace(AbstractPlace place) {
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
    cellarOrganizerPanel.setAbstractPlace(cellarOrganizerPanel.getAbstractPlace());
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

  private final MyCellarSimpleLabel label = new MyCellarSimpleLabel();
  private MyCellarObject myCellarObject;

  MyCellarObjectDraggingLabel(final MyCellarObject myCellarObject) {
    super();
    this.myCellarObject = myCellarObject;
    int width = myCellarObject.getAbstractPlace().isSimplePlace() ? 400 : 100;
    setLayout(new MigLayout("", "5px[" + width + ":" + width + ":" + width + "][10:10:10]0px", "0px[align center, grow]0px"));
    if (myCellarObject instanceof Bouteille bouteille) {
      if (bouteille.isWhiteWine()) {
        label.setIcon(MyCellarImage.WHITEWINE);
      } else if (bouteille.isPinkWine()) {
        label.setIcon(MyCellarImage.PINKWINE);
      } else {
        label.setIcon(MyCellarImage.BLACKWINE);
      }
    }
    label.setText("<html>" + myCellarObject.getNom() + "</html>");
    add(label, "grow");
    add(new PanelCloseButton() {
      @Override
      public void perform() {
        String mess = MyCellarLabelManagement.getLabel(MAIN_DELETEWINE, myCellarObject.getNom());
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), mess, MyCellarLabelManagement.getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION)) {
          Component parent = MyCellarObjectDraggingLabel.this.getParent();
          if (parent instanceof RangementCell rangementCell) {
            rangementCell.remove(MyCellarObjectDraggingLabel.this);
            rangementCell.updateUI();
            Program.getStorage().addHistory(HistoryState.DEL, myCellarObject);
            try {
              final AbstractPlace abstractPlace = myCellarObject.getAbstractPlace();
              abstractPlace.removeObject(myCellarObject);
              ProgramPanels.getSearch().ifPresent(search -> search.removeObject(myCellarObject));
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
  private final DataFlavor localObjectFlavor;
  private final MyCellarSimpleLabel label = new MyCellarSimpleLabel() {
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
      target.setPlace(PlaceUtils.getPlaceByName(target.getPlaceName()));
      bouteille.setEmplacement(target.getPlaceName());
      bouteille.setLigne(target.getRow());
      bouteille.setColonne(target.getColumn());
      bouteille.setNumLieu(target.getPlaceNum());
      bouteille.updateStatus();
      target.addBottle(bouteilleLabel);
      src.draggingLabel.removeObject();
      PlaceUtils.putTabStock();
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
