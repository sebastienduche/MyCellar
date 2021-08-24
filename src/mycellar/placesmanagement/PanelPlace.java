package mycellar.placesmanagement;

import mycellar.Bouteille;
import mycellar.MyCellarControl;
import mycellar.Program;
import mycellar.actions.ChooseCellAction;
import mycellar.core.IPlace;
import mycellar.core.JModifyComboBox;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarObject;
import mycellar.general.XmlUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.9
 * @since 23/08/21
 */
public final class PanelPlace extends JPanel implements IPlace {
  private static final long serialVersionUID = -2601861017578176513L;
  private static final ComboItem NONE = new ComboItem(-1, "");
  private final JModifyComboBox<Rangement> place = new JModifyComboBox<>();
  private final JModifyComboBox<ComboItem> numPlace = new JModifyComboBox<>();
  private final JModifyComboBox<ComboItem> line = new JModifyComboBox<>();
  private final JModifyComboBox<ComboItem> column = new JModifyComboBox<>();
  private final MyCellarLabel labelExist = new MyCellarLabel();
  private final MyCellarButton preview = new MyCellarButton(LabelType.INFO, "138");
  private final MyCellarLabel labelNumPlace = new MyCellarLabel(LabelType.INFO, "082");
  private final MyCellarLabel labelLine = new MyCellarLabel(LabelType.INFO, "028");
  private final MyCellarLabel labelColumn = new MyCellarLabel(LabelType.INFO, "083");
  private final MyCellarLabel before1 = new MyCellarLabel(LabelType.INFO, "091"); // Pour la Modification
  private final MyCellarLabel before2 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarLabel before3 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarLabel before4 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarLabel before5 = new MyCellarLabel(); // Pour la Modification
  private final MyCellarButton chooseCell;
  private boolean listenersEnabled = true;

  public PanelPlace() {
    this(null, false, true);
  }

  public PanelPlace(Rangement rangement, boolean newLineForError, boolean chooseCellVisible) {
    char previewChar = Program.getLabel("PREVIEW").charAt(0);
    preview.setMnemonic(previewChar);
    preview.addActionListener(this::preview_actionPerformed);
    chooseCell = new MyCellarButton(LabelType.INFO_OTHER, "AddVin.ChooseCell", new ChooseCellAction(this));
    setLayout(new MigLayout("", "[]30px[]30px[]30px[]30px[grow]30px[]", ""));
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
    if (chooseCellVisible) {
      add(chooseCell, "alignx right");
    }
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

  private static void Debug(String sText) {
    Program.Debug("PanelPlace: " + sText);
  }

  public Place getSelectedPlace() {
    final Rangement rangement = (Rangement) place.getSelectedItem();
    Objects.requireNonNull(rangement);

    return new Place.PlaceBuilder(rangement)
        .withNumPlace(numPlace.getItemCount() > 0 ? ((ComboItem) Objects.requireNonNull(numPlace.getSelectedItem())).getValue() : -1)
        .withLine(line.getSelectedIndex())
        .withColumn(column.getSelectedIndex())
        .build();
  }

  private void initPlaceCombo() {
    place.addItem(Program.EMPTY_PLACE);
    Program.getCave().forEach(place::addItem);
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
    if (!before4.getText().isBlank()) {
      before4.setVisible(visible);
    }
    if (!before5.getText().isBlank()) {
      before5.setVisible(visible);
    }
  }

  public void setBeforeBottle(MyCellarObject bottle) {
    before2.setText(bottle.getEmplacement());
    before3.setText(Integer.toString(bottle.getNumLieu()));
    before4.setText(Integer.toString(bottle.getLigne()));
    before5.setText(Integer.toString(bottle.getColonne()));
    setBeforeLabelsVisible(true);
  }

  public void clearBeforeBottle() {
    before2.setText("");
    before3.setText("");
    before4.setText("");
    before5.setText("");
    setBeforeLabelsVisible(false);
  }

  public void setBottle(Bouteille bottle) {
    selectPlace(bottle);
  }

  public void setBeforeLabelsVisible(boolean b) {
    before1.setVisible(b);
    before2.setVisible(b);
    before3.setVisible(b);
    before4.setVisible(b && labelLine.isVisible());
    before5.setVisible(b && labelColumn.isVisible());
  }

  public void enableAll(boolean enable) {
    place.setEnabled(enable && (place.getItemCount() > 2 || place.getSelectedIndex() != 1));
    numPlace.setEnabled(enable && place.getSelectedIndex() > 0 && (numPlace.getItemCount() > 2 || numPlace.getSelectedIndex() != 1 || !((Rangement) place.getSelectedItem()).isCaisse()));
    line.setEnabled(enable && numPlace.getSelectedIndex() > 0);
    column.setEnabled(enable && line.getSelectedIndex() > 0);
    if (chooseCell != null) {
      chooseCell.setEnabled(enable && Program.hasComplexPlace());
    }
  }

  public void resetValues() {
    place.setSelectedIndex(0);
    clearBeforeBottle();
    labelExist.setText("");
    managePlaceCombos();
  }

  public void updateView() {
    setListenersEnabled(false);
    place.removeAllItems();
    setListenersEnabled(true);
    initPlaceCombo();
    managePlaceCombos();
  }

  public void selectPlace(MyCellarObject cellarObject) {
    selectPlace(cellarObject.getPlace());
  }

  @Override
  public void selectPlace(Place placeRangement) {
    setListenersEnabled(false);
    final Rangement rangement = placeRangement.getRangement();
    place.setSelectedItem(rangement);
    labelExist.setText("");

    preview.setEnabled(!rangement.isCaisse());
    numPlace.removeAllItems();
    column.removeAllItems();
    line.removeAllItems();
    numPlace.addItem(NONE);
    line.addItem(NONE);
    column.addItem(NONE);
    numPlace.setEnabled(true);
    line.setEnabled(true);
    column.setEnabled(true);
    for (int i = rangement.getFirstNumEmplacement(); i < rangement.getLastNumEmplacement(); i++) {
      numPlace.addItem(new ComboItem(i));
    }
    if (!rangement.isCaisse()) { // Need the last place number for complex places
      numPlace.addItem(new ComboItem(rangement.getLastNumEmplacement()));
    }
    numPlace.setSelectedItem(new ComboItem(placeRangement.getPlaceNum()));

    if (!rangement.isCaisse()) {
      int nbLine = rangement.getNbLignes(placeRangement.getPlaceNumIndex());
      int nbColumn = rangement.getNbColonnes(placeRangement.getPlaceNumIndex(), placeRangement.getLineIndex());
      for (int i = 1; i <= nbLine; i++) {
        line.addItem(new ComboItem(i));
      }
      for (int i = 1; i <= nbColumn; i++) {
        column.addItem(new ComboItem(i));
      }
      line.setSelectedItem(new ComboItem(placeRangement.getLine()));
      column.setSelectedItem(new ComboItem(placeRangement.getColumn()));
    }

    boolean simplePlace = rangement.isCaisse();
    labelLine.setVisible(!simplePlace);
    labelColumn.setVisible(!simplePlace);
    line.setVisible(!simplePlace);
    column.setVisible(!simplePlace);
    setListenersEnabled(true);
  }

  private void setListeners() {
    place.addItemListener(this::lieu_itemStateChanged);
    numPlace.addItemListener(this::num_lieu_itemStateChanged);
    line.addItemListener(this::line_itemStateChanged);
    column.addItemListener(this::column_itemStateChanged);
  }

  private boolean isListenersDisabled() {
    return !listenersEnabled;
  }

  public void setListenersEnabled(boolean listenersEnabled) {
    this.listenersEnabled = listenersEnabled;
  }

  private void preview_actionPerformed(ActionEvent e) {
    Debug("Previewing...");
    RangementUtils.putTabStock();
    XmlUtils.writeRangements(Program.getPreviewXMLFileName(), List.of((Rangement) Objects.requireNonNull(place.getSelectedItem())), false);
    Program.open(new File(Program.getPreviewXMLFileName()));
    Debug("Previewing... End");
  }

  private void lieu_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled()) {
      return;
    }
    Debug("Lieu_itemStateChanging...");
    int lieu_select = place.getSelectedIndex();
    Rangement rangement = (Rangement) place.getSelectedItem();
    Objects.requireNonNull(rangement);

    labelExist.setText("");

    labelNumPlace.setVisible(true);
    numPlace.setVisible(true);
    boolean caisse = false;
    if (lieu_select == 0) {
      numPlace.setEnabled(false);
      line.setEnabled(false);
      column.setEnabled(false);
    } else {
      numPlace.setEnabled(true);
      caisse = rangement.isCaisse();
    }
    preview.setEnabled(!caisse);

    numPlace.removeAllItems();
    numPlace.addItem(NONE);
    line.removeAllItems();
    column.removeAllItems();
    for (int i = rangement.getFirstNumEmplacement(); i < rangement.getLastNumEmplacement(); i++) {
      numPlace.addItem(new ComboItem(i));
    }

    if (caisse) {
      labelNumPlace.setText(Program.getLabel("Infos158")); //"Numero de caisse");
      if (rangement.getNbEmplacements() == 1) {
        numPlace.setSelectedIndex(1);
      }
    } else {
      // Need the last place number for complex places
      numPlace.addItem(new ComboItem(rangement.getLastNumEmplacement()));
      labelNumPlace.setText(Program.getLabel("Infos082")); //"Numero du lieu");
    }
    setLineColumnVisible(rangement);
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
      } else {
        line.setEnabled(true);
        Rangement rangement = place.getItemAt(lieu_select);
        if (!rangement.isCaisse()) {
          int nb_ligne = rangement.getNbLignes(num_select - 1);
          line.removeAllItems();
          column.removeAllItems();
          line.addItem(NONE);
          for (int i = 1; i <= nb_ligne; i++) {
            line.addItem(new ComboItem(i));
          }
        }
      }
      setVisible(true);
      Debug("Num_lieu_itemStateChanging... End");
    });
  }

  private void line_itemStateChanged(ItemEvent e) {
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
    column.addItem(NONE);
    for (int i = 1; i <= nb_col; i++) {
      column.addItem(new ComboItem(i));
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
      labelExist.setText("");
      cave.getBouteille(nNumLieu - 1, nLine - 1, nColumn - 1)
          .ifPresent(myCellarObject -> labelExist.setText(MessageFormat.format(Program.getLabel("Infos329"), Program.convertStringFromHTMLString(myCellarObject.getNom()))));
      Debug("Column_itemStateChanging... End");
    });
  }

  public void setModifyActive(boolean enable) {
    place.setModifyActive(enable);
    numPlace.setModifyActive(enable);
    line.setModifyActive(enable);
    column.setModifyActive(enable);
  }

  public void enableSimplePlace(boolean enable) {
    place.setEnabled(enable);
    numPlace.setEnabled(enable);
  }

  public void enablePlace(boolean enable) {
    place.setEnabled(enable);
  }

  public boolean hasSelecedPlace() {
    return place.getSelectedIndex() > 0;
  }

  public void clearModified() {
    place.setModified(false);
    numPlace.setModified(false);
    line.setModified(false);
    column.setModified(false);
  }

  public boolean isModified() {
    boolean modified = place.isModified();
    modified |= numPlace.isModified();
    modified |= line.isModified();
    modified |= column.isModified();
    return modified;
  }

  public boolean performValidation(boolean isModification) {
    final Place placeWithoutValidation = getSelectedPlace();
    if (!isModification) {
      if (MyCellarControl.hasInvalidPlace(placeWithoutValidation)) {
        return false;
      }
    }
    if (placeWithoutValidation.hasPlace()) {
      if (MyCellarControl.hasInvalidNumLieuNumber(placeWithoutValidation.getPlaceNum(), placeWithoutValidation.isSimplePlace())) {
        enableAll(true);
        return false;
      }

      if (!placeWithoutValidation.isSimplePlace()) {
        if (MyCellarControl.hasInvalidLineNumber(placeWithoutValidation.getLine())) {
          enableAll(true);
          return false;
        }
        if (MyCellarControl.hasInvalidColumnNumber(placeWithoutValidation.getColumn())) {
          enableAll(true);
          return false;
        }
      }
    }
    return true;
  }

  public boolean isPlaceModified() {
    return place.getSelectedIndex() > 0;
  }

  private static class ComboItem {

    private final int value;
    private final String label;

    ComboItem(int value, String label) {
      this.value = value;
      this.label = label;
    }

    ComboItem(int value) {
      this.value = value;
      label = Integer.toString(value);
    }

    public int getValue() {
      return value;
    }

    @Override
    public String toString() {
      return label;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || !Objects.equals(getClass(), o.getClass())) {
        return false;
      }
      ComboItem comboItem = (ComboItem) o;
      return value == comboItem.value;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }
}
