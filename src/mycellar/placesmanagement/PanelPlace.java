package mycellar.placesmanagement;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.actions.ChooseCellAction;
import mycellar.core.IPlace;
import mycellar.core.JModifyComboBox;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import java.awt.event.ItemEvent;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 16/03/21
 */
public final class PanelPlace extends JPanel implements IPlace {
  private static final long serialVersionUID = -2601861017578176513L;
  private final MyCellarLabel labelNumPlace = new MyCellarLabel(LabelType.INFO, "082");
  private final MyCellarLabel labelLine = new MyCellarLabel(LabelType.INFO, "028");
  private final MyCellarLabel labelColumn = new MyCellarLabel(LabelType.INFO, "083");
  protected final JModifyComboBox<Rangement> place = new JModifyComboBox<>();
  protected final JModifyComboBox<String> numPlace = new JModifyComboBox<>();
  protected final JModifyComboBox<String> line = new JModifyComboBox<>();
  protected final JModifyComboBox<String> column = new JModifyComboBox<>();
  protected final MyCellarLabel labelExist = new MyCellarLabel();
  private final MyCellarLabel before1 = new MyCellarLabel(LabelType.INFO, "091"); // Pour la Modification
  private final MyCellarLabel before2 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarLabel before3 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarLabel before4 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarLabel before5 = new MyCellarLabel(); // Pour la Modification
  protected MyCellarButton chooseCell;
  protected final MyCellarButton preview = new MyCellarButton(LabelType.INFO, "138");
  private boolean listenersEnabled = true;

  public PanelPlace() {
    this(null, false);
  }

  public PanelPlace(Rangement rangement, boolean newLineForError) {
    chooseCell = new MyCellarButton(LabelType.INFO_OTHER, "AddVin.ChooseCell", new ChooseCellAction(this));
    setLayout(new MigLayout("","[]30px[]30px[]30px[]30px[grow]30px[]",""));
    setBorder(BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.LOWERED), Program.getLabel("Infos217")));
    add(new MyCellarLabel(LabelType.INFO, "208"));
    add(labelNumPlace);
    add(labelLine);
    add(labelColumn, "wrap");
    add(place);
    add(numPlace);
    add(line);
    add(column);
    if (!newLineForError) {
      add(labelExist, "hidemode 3");
    } else {
      add(new JLabel());
    }
    add(chooseCell, "alignx right");
    add(preview, "alignx right, wrap");
    if (newLineForError) {
      add(labelExist, "hidemode 3, span 6, wrap");
    }
    add(before1, "hidemode 3,split 2");
    add(before2, "hidemode 3");
    add(before3, "hidemode 3");
    add(before4, "hidemode 3");
    add(before5, "hidemode 3");
    initPlaceCombo();
    setListeners();
    setBeforeLabelsVisible(false);
    if (rangement != null) {
      place.setSelectedItem(rangement);
    }
    managePlaceCombos();
  }

  public Optional<Place> getSelectedPlace() {
    final Rangement rangement = (Rangement) place.getSelectedItem();
    Objects.requireNonNull(rangement);
    if (Program.EMPTY_PLACE.equals(rangement)) {
      return Optional.empty();
    }

    return new Place.PlaceBuilder(rangement)
        .withNumPlace(numPlace.getSelectedIndex())
        .withLine(line.getSelectedIndex())
        .withColumn(column.getSelectedIndex())
        .build();
  }

  protected void initPlaceCombo() {
    place.addItem(Program.EMPTY_PLACE);
    Program.getCave()
        .forEach(place::addItem);
    chooseCell.setEnabled(Program.hasComplexPlace());
  }

  public void managePlaceCombos() {
    place.setEnabled(true);
    preview.setEnabled(false);
    if (place.getItemCount() == 2) {
      if (place.getSelectedIndex() == 0) {
        place.setSelectedIndex(1);
      }
      place.setEnabled(false);
      Rangement r = (Rangement) place.getSelectedItem();
      if (numPlace.getItemCount() == 2) {
        if (numPlace.getSelectedIndex() == 0) {
          numPlace.setSelectedIndex(1);
        }
        numPlace.setEnabled(false);
      }
      setLineColumnVisible(r);
    } else {
      place.setEnabled(true);
      numPlace.setEnabled(false);
      line.setVisible(false);
      column.setVisible(false);
      labelLine.setVisible(false);
      labelColumn.setVisible(false);
      if (place.getSelectedIndex() > 0) {
        numPlace.setEnabled(true);
        Rangement r = (Rangement) place.getSelectedItem();
        if (numPlace.getItemCount() == 2) {
          if (numPlace.getSelectedIndex() == 0) {
            numPlace.setSelectedIndex(1);
          }
          numPlace.setEnabled(false);
        }
        setLineColumnVisible(r);
      }
    }
  }

  private void setLineColumnVisible(Rangement r) {
    if (r == null) {
      return;
    }
    boolean visible = !r.isCaisse();
    line.setVisible(visible);
    column.setVisible(visible);
    labelLine.setVisible(visible);
    labelColumn.setVisible(visible);
  }

  public void setBeforeBottle(Bouteille bottle) {
    before2.setText(bottle.getEmplacement());
    before3.setText(Integer.toString(bottle.getNumLieu()));
    before4.setText(Integer.toString(bottle.getLigne()));
    before5.setText(Integer.toString(bottle.getColonne()));
    setBeforeLabelsVisible(true);
  }

  public void clearBeforeBottle() {
    setBeforeLabelsVisible(false);
  }

  public void setBottle(Bouteille bottle) {
    selectPlace(bottle);
  }

  private void setBeforeLabelsVisible(boolean b) {
    before1.setVisible(b);
    before2.setVisible(b);
    before3.setVisible(b);
    before4.setVisible(b);
    before5.setVisible(b);
  }

  public void enableAll(boolean enable) {
    place.setEnabled(place.getItemCount() > 2 || place.getSelectedIndex() != 1);
    numPlace.setEnabled(enable && place.getSelectedIndex() > 0 && (numPlace.getItemCount() > 2 || numPlace.getSelectedIndex() != 1));
    line.setEnabled(enable && numPlace.getSelectedIndex() > 0);
    column.setEnabled(enable && line.getSelectedIndex() > 0);
    if (chooseCell != null) {
      chooseCell.setEnabled(enable && Program.hasComplexPlace());
    }
  }

  public void clear() {
    place.setSelectedIndex(0);
  }

  public void updateView() {
    setListenersEnabled(false);
    place.removeAllItems();
    setListenersEnabled(true);
    initPlaceCombo();
    managePlaceCombos();
  }

  public void selectPlace(Bouteille bouteille) {
    bouteille.getPlace().ifPresent(this::selectPlace);
  }

  @Override
  public void selectPlace(Place placeRangement) {
    setListenersEnabled(false);
    final Rangement rangement = placeRangement.getRangement();
    place.setSelectedItem(rangement);
//    for (int i=0; i<place.getItemCount(); i++) {
//      if (rangement.equals(place.getItemAt(i))) {
//        place.setSelectedIndex(i);
//        break;
//      }
//    }
    labelExist.setText("");

    preview.setEnabled(!rangement.isCaisse());
    int nbLine = rangement.getNbLignes(placeRangement.getPlaceNumIndex());
    int nbColumn = rangement.getNbColonnes(placeRangement.getPlaceNumIndex(), placeRangement.getLineIndex());
    numPlace.removeAllItems();
    column.removeAllItems();
    line.removeAllItems();
    numPlace.addItem("");
    line.addItem("");
    column.addItem("");
    numPlace.setEnabled(true);
    line.setEnabled(true);
    column.setEnabled(true);
    for (int i = rangement.getFirstNumEmplacement(); i < rangement.getLastNumEmplacement(); i++) {
      numPlace.addItem(Integer.toString(i));
    }
    for (int i = 1; i <= nbLine; i++) {
      line.addItem(Integer.toString(i));
    }
    for (int i = 1; i <= nbColumn; i++) {
      column.addItem(Integer.toString(i));
    }
    numPlace.setSelectedIndex(placeRangement.getPlaceNumIndexForCombo());
    line.setSelectedIndex(placeRangement.getLine());
    column.setSelectedIndex(placeRangement.getColumn());

    boolean simplePlace = rangement.isCaisse();
    labelLine.setVisible(!simplePlace);
    labelColumn.setVisible(!simplePlace);
    line.setVisible(!simplePlace);
    column.setVisible(!simplePlace);
    setListenersEnabled(true);
  }

  protected void setListeners() {
    place.addItemListener(this::lieu_itemStateChanged);
    numPlace.addItemListener(this::num_lieu_itemStateChanged);
    line.addItemListener(this::line_itemStateChanged);
    column.addItemListener(this::column_itemStateChanged);
  }

  protected boolean isListenersDisabled() {
    return !listenersEnabled;
  }

  public void setListenersEnabled(boolean listenersEnabled) {
    this.listenersEnabled = listenersEnabled;
  }

  protected void lieu_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled()) {
      return;
    }
    Debug("Lieu_itemStateChanging...");
    int lieu_select = place.getSelectedIndex();
    Rangement rangement = (Rangement) place.getSelectedItem();
    Objects.requireNonNull(rangement);

    labelExist.setText("");

    boolean caisse = false;
    labelNumPlace.setVisible(true);
    numPlace.setVisible(true);
    if (lieu_select == 0) {
      numPlace.setEnabled(false);
      line.setEnabled(false);
      column.setEnabled(false);
    }	else {
      numPlace.setEnabled(true);
      caisse = rangement.isCaisse();
    }
    preview.setEnabled(!caisse);

    numPlace.removeAllItems();
    numPlace.addItem("");
    line.removeAllItems();
    column.removeAllItems();
    for (int i = rangement.getFirstNumEmplacement(); i < rangement.getLastNumEmplacement(); i++) {
      numPlace.addItem(Integer.toString(i));
    }

    if (caisse) {
      labelNumPlace.setText(Program.getLabel("Infos158")); //"Numero de caisse");
      if (rangement.getNbEmplacements() == 1) {
        numPlace.setSelectedIndex(1);
      }
    }	else {
      labelNumPlace.setText(Program.getLabel("Infos082")); //"Numero du lieu");
    }
    boolean visible = lieu_select > 0;
    visible &= !caisse;
    line.setVisible(visible);
    column.setVisible(visible);
    labelLine.setVisible(visible);
    labelColumn.setVisible(visible);
    Debug("Lieu_itemStateChanging... Done");
  }

  private void num_lieu_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled()) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      Debug("Num_lieu_itemStateChanging...");
      int num_select = numPlace.getSelectedIndex();
      int lieu_select = place.getSelectedIndex();

      labelExist.setText("");

      if (num_select == 0) {
        line.setEnabled(false);
        column.setEnabled(false);
      }	else {
        line.setEnabled(true);
        Rangement rangement = place.getItemAt(lieu_select);
        if (!rangement.isCaisse()) {
          int nb_ligne = rangement.getNbLignes(num_select - 1);
          line.removeAllItems();
          column.removeAllItems();
          line.addItem("");
          for (int i = 1; i <= nb_ligne; i++) {
            line.addItem(Integer.toString(i));
          }
        }
      }
      setVisible(true);
      Debug("Num_lieu_itemStateChanging... End");
    });
  }

  protected void line_itemStateChanged(ItemEvent e) {
    int num_select = line.getSelectedIndex();
    int emplacement = numPlace.getSelectedIndex();
    int lieu_select = place.getSelectedIndex();
    labelExist.setText("");
    column.setEnabled(num_select != 0);
    int nb_col = 0;
    if (num_select > 0) {
      Rangement cave = place.getItemAt(lieu_select);
      nb_col = cave.getNbColonnes(emplacement - 1, num_select - 1);
    }
    column.removeAllItems();
    column.addItem("");
    for (int i = 1; i <= nb_col; i++) {
      column.addItem(Integer.toString(i));
    }
  }

  private void column_itemStateChanged(ItemEvent e) {
    SwingUtilities.invokeLater(() -> {
      Debug("Column_itemStateChanging...");
      int nPlace = place.getSelectedIndex();
      int nNumLieu = numPlace.getSelectedIndex();
      int nLine = line.getSelectedIndex();
      int nColumn = column.getSelectedIndex();

      if (nPlace < 1 || nNumLieu < 1 || nLine < 1 || nColumn < 1) {
        return;
      }

      Rangement cave = place.getItemAt(nPlace);
      Optional<Bouteille> b = cave.getBouteille(nNumLieu - 1, nLine - 1, nColumn - 1);
      if (b.isPresent()) {
        labelExist.setText(MessageFormat.format(Program.getLabel("Infos329"), Program.convertStringFromHTMLString(b.get().getNom())));
      } else {
        labelExist.setText("");
      }
      Debug("Column_itemStateChanging... End");
    });
  }

  protected static void Debug(String sText) {
    Program.Debug("PanelPlace: " + sText);
  }
}
