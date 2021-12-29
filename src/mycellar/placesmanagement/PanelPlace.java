package mycellar.placesmanagement;

import mycellar.MyCellarControl;
import mycellar.Program;
import mycellar.actions.ChooseCellAction;
import mycellar.core.IPlace;
import mycellar.core.LabelType;
import mycellar.core.MyCellarObject;
import mycellar.core.uicomponents.JModifyComboBox;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.general.XmlUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
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
 * @version 1.9
 * @since 29/12/21
 */
public final class PanelPlace extends JPanel implements IPlace {
  private static final long serialVersionUID = -2601861017578176513L;
  private static final ComboItem NONE = new ComboItem(-1, "");
  private final JModifyComboBox<Rangement> place = new JModifyComboBox<>();
  private final JModifyComboBox<ComboItem> numPlace = new JModifyComboBox<>();
  private final JModifyComboBox<ComboItem> line = new JModifyComboBox<>();
  private final JModifyComboBox<ComboItem> column = new JModifyComboBox<>();
  private final MyCellarLabel labelExist = new MyCellarLabel("");
  private final MyCellarButton preview = new MyCellarButton(LabelType.INFO, "138");
  private final MyCellarLabel labelNumPlace = new MyCellarLabel(LabelType.INFO, "082");
  private final MyCellarLabel labelLine = new MyCellarLabel(LabelType.INFO, "028");
  private final MyCellarLabel labelColumn = new MyCellarLabel(LabelType.INFO, "083");

  private final MyCellarLabel beforeLabel = new MyCellarLabel(LabelType.INFO, "091"); // Pour la Modification
  private final MyCellarLabel previousPlaceLabel = new MyCellarLabel(""); // Pour la Modification
  private final MyCellarLabel previousNumPlaceLabel = new MyCellarLabel(""); // Pour la Modification
  private final MyCellarLabel previousLineLabel = new MyCellarLabel(""); // Pour la Modification
  private final MyCellarLabel previousColumnLabel = new MyCellarLabel(""); // Pour la Modification
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
    initPlaceCombo();
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
    add(beforeLabel, "hidemode 3,split 2");
    add(previousPlaceLabel, "hidemode 3");
    add(previousNumPlaceLabel, "hidemode 3");
    add(previousLineLabel, "hidemode 3");
    add(previousColumnLabel, "hidemode 3");
    setListeners();
    setListenersEnabled(true);
    setBeforeLabelsVisible(false);
    place.setEnabled(false);
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
    enableAll(false);
    preview.setEnabled(false);
    if (place.getItemCount() == 2) {
      if (place.getSelectedIndex() == 0) {
        place.setSelectedIndex(1);
      }
    }
    Rangement rangement = null;
    if (place.getSelectedIndex() > 0) {
      rangement = (Rangement) place.getSelectedItem();
      if (numPlace.getItemCount() == 2) {
        if (numPlace.getSelectedIndex() == 0) {
          numPlace.setSelectedIndex(1);
        }
      }
    }
    setLineColumnVisible(rangement);
    enableAll(true);
  }

  private void setLineColumnVisible(Rangement r) {
    boolean visible = r != null && !r.isSimplePlace();
    line.setVisible(visible);
    column.setVisible(visible);
    labelLine.setVisible(visible);
    labelColumn.setVisible(visible);
    if (!previousLineLabel.getText().isBlank()) {
      previousLineLabel.setVisible(visible);
    }
    if (!previousColumnLabel.getText().isBlank()) {
      previousColumnLabel.setVisible(visible);
    }
  }

  public void setBeforeBottle(MyCellarObject myCellarObject) {
    setLineColumnVisible(myCellarObject.getRangement());
    previousPlaceLabel.setText(myCellarObject.getEmplacement());
    previousNumPlaceLabel.setText(Integer.toString(myCellarObject.getNumLieu()));
    previousLineLabel.setText(Integer.toString(myCellarObject.getLigne()));
    previousColumnLabel.setText(Integer.toString(myCellarObject.getColonne()));
    setBeforeLabelsVisible(true);
  }

  public void clearBeforeBottle() {
    previousPlaceLabel.setText("");
    previousNumPlaceLabel.setText("");
    previousLineLabel.setText("");
    previousColumnLabel.setText("");
    setBeforeLabelsVisible(false);
  }

  public void setBeforeLabelsVisible(boolean b) {
    beforeLabel.setVisible(b);
    previousPlaceLabel.setVisible(b);
    previousNumPlaceLabel.setVisible(b);
    previousLineLabel.setVisible(b && labelLine.isVisible());
    previousColumnLabel.setVisible(b && labelColumn.isVisible());
  }

  public void enableAll(boolean enable) {
    place.setEnabled(enable && (place.getItemCount() > 2 || place.getSelectedIndex() != 1));
    numPlace.setEnabled(enable && place.getSelectedIndex() > 0 && (numPlace.getItemCount() > 2 || numPlace.getSelectedIndex() != 1 || !((Rangement) Objects.requireNonNull(place.getSelectedItem())).isSimplePlace()));
    line.setEnabled(enable && numPlace.getSelectedIndex() > 0);
    column.setEnabled(enable && line.getSelectedIndex() > 0);
    if (chooseCell != null) {
      chooseCell.setEnabled(enable && Program.hasComplexPlace());
    }
  }

  public void resetValues() {
    SwingUtilities.invokeLater(() -> {
      setListenersEnabled(false);
      place.setSelectedIndex(0);
      clearBeforeBottle();
      labelExist.setText("");
      managePlaceCombos();
      setListenersEnabled(true);
    });
  }

  public void updateView() {
    Debug("Update view...");
    setListenersEnabled(false);
    place.removeAllItems();
    setListenersEnabled(true);
    initPlaceCombo();
    managePlaceCombos();
    Debug("Update view... Done");
  }

  public void selectPlace(MyCellarObject cellarObject) {
    selectPlace(cellarObject.getPlace());
  }

  @Override
  public void selectPlace(Place placeRangement) {
    SwingUtilities.invokeLater(() -> {
      Debug("Select Place...");
      enableAll(false);
      setListenersEnabled(false);
      final Rangement rangement = placeRangement.getRangement();
      place.setSelectedItem(rangement);
      labelExist.setText("");

      preview.setEnabled(!rangement.isSimplePlace());
      numPlace.removeAllItems();
      column.removeAllItems();
      line.removeAllItems();
      numPlace.addItem(NONE);
      line.addItem(NONE);
      column.addItem(NONE);
      for (int i = rangement.getFirstPartNumber(); i < rangement.getLastPartNumber(); i++) {
        numPlace.addItem(new ComboItem(i));
      }
      if (!rangement.isSimplePlace()) { // Need the last place number for complex places
        numPlace.addItem(new ComboItem(rangement.getLastPartNumber()));
      }
      numPlace.setSelectedItem(new ComboItem(placeRangement.getPlaceNum()));

      if (!rangement.isSimplePlace()) {
        int nbLine = rangement.getLineCountAt(placeRangement.getPlaceNumIndex());
        int nbColumn = rangement.getColumnCountAt(placeRangement.getPlaceNumIndex(), placeRangement.getLineIndex());
        for (int i = 1; i <= nbLine; i++) {
          line.addItem(new ComboItem(i));
        }
        for (int i = 1; i <= nbColumn; i++) {
          column.addItem(new ComboItem(i));
        }
        line.setSelectedItem(new ComboItem(placeRangement.getLine()));
        column.setSelectedItem(new ComboItem(placeRangement.getColumn()));
      }
      enableAll(true);

      boolean simplePlace = rangement.isSimplePlace();
      labelLine.setVisible(!simplePlace);
      labelColumn.setVisible(!simplePlace);
      line.setVisible(!simplePlace);
      column.setVisible(!simplePlace);
      setListenersEnabled(true);
      Debug("Select Place... Done");
    });
  }

  private void setListeners() {
    place.addItemListener(this::lieu_itemStateChanged);
    numPlace.addItemListener(this::num_lieu_itemStateChanged);
    line.addItemListener(this::line_itemStateChanged);
    column.addItemListener(this::column_itemStateChanged);
  }

  private boolean isListenersDisabled(ItemEvent e) {
    return !listenersEnabled || e.getStateChange() == ItemEvent.DESELECTED;
  }

  public void setListenersEnabled(boolean listenersEnabled) {
    this.listenersEnabled = listenersEnabled;
    place.setListenerEnable(listenersEnabled);
    numPlace.setListenerEnable(listenersEnabled);
    line.setListenerEnable(listenersEnabled);
    column.setListenerEnable(listenersEnabled);
  }

  private void preview_actionPerformed(ActionEvent e) {
    Debug("Previewing...");
    RangementUtils.putTabStock();
    XmlUtils.writeRangements(Program.getPreviewXMLFileName(), List.of((Rangement) Objects.requireNonNull(place.getSelectedItem())), false);
    Program.open(Program.getPreviewXMLFileName(), false);
    Debug("Previewing... End");
  }

  private void lieu_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled(e)) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      Debug("Lieu_itemStateChanging...");
      Rangement rangement = (Rangement) place.getSelectedItem();
      Objects.requireNonNull(rangement);
      enableAll(false);
      labelExist.setText("");

      preview.setEnabled(!rangement.isSimplePlace());

      numPlace.removeAllItems();
      numPlace.addItem(NONE);
      line.removeAllItems();
      column.removeAllItems();
      for (int i = rangement.getFirstPartNumber(); i < rangement.getLastPartNumber(); i++) {
        numPlace.addItem(new ComboItem(i));
      }

      if (rangement.isSimplePlace()) {
        labelNumPlace.setText(Program.getLabel("Infos158")); //"Numero de caisse
        if (rangement.getNbParts() == 1) {
          numPlace.setSelectedIndex(1);
        }
      } else {
        // Need the last place number for complex places
        numPlace.addItem(new ComboItem(rangement.getLastPartNumber()));
        labelNumPlace.setText(Program.getLabel("Infos082")); //"Numero du lieu
      }
      setLineColumnVisible(rangement);
      enableAll(true);
      Debug("Lieu_itemStateChanging... End");
    });
  }

  private void num_lieu_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled(e)) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      Debug("Num_lieu_itemStateChanging...");
      enableAll(false);
      int num_select = numPlace.getSelectedIndex();
      int lieu_select = place.getSelectedIndex();

      labelExist.setText("");

      if (num_select != 0) {
        Rangement rangement = place.getItemAt(lieu_select);
        if (!rangement.isSimplePlace()) {
          int nb_ligne = rangement.getLineCountAt(num_select - 1);
          line.removeAllItems();
          column.removeAllItems();
          line.addItem(NONE);
          for (int i = 1; i <= nb_ligne; i++) {
            line.addItem(new ComboItem(i));
          }
        }
      }
      enableAll(true);
      Debug("Num_lieu_itemStateChanging... End");
    });
  }

  private void line_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled(e)) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      Debug("Line_itemStateChanging...");
      enableAll(false);
      int num_select = line.getSelectedIndex();
      int emplacement = numPlace.getSelectedIndex();
      int lieu_select = place.getSelectedIndex();
      labelExist.setText("");
      column.setEnabled(num_select != 0);
      int nb_col = 0;
      if (num_select > 0) {
        Rangement cave = place.getItemAt(lieu_select);
        nb_col = cave.getColumnCountAt(emplacement - 1, num_select - 1);
      }
      column.removeAllItems();
      column.addItem(NONE);
      for (int i = 1; i <= nb_col; i++) {
        column.addItem(new ComboItem(i));
      }
      enableAll(true);
      Debug("Line_itemStateChanging... End");
    });
  }

  private void column_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled(e)) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      Debug("Column_itemStateChanging...");
      int nPlace = place.getSelectedIndex();
      int nNumLieu = numPlace.getSelectedIndex();
      int nLine = line.getSelectedIndex();
      int nColumn = column.getSelectedIndex();

      labelExist.setText("");
      if (nPlace < 1 || nNumLieu < 1 || nLine < 1 || nColumn < 1) {
        return;
      }

      Rangement cave = place.getItemAt(nPlace);
      cave.getObject(nNumLieu - 1, nLine - 1, nColumn - 1)
          .ifPresent(myCellarObject -> labelExist.setText(MessageFormat.format(Program.getLabel("Infos329"), Program.convertStringFromHTMLString(myCellarObject.getNom()))));
      Debug("Column_itemStateChanging... End");
    });
  }

  public void setModifyActive(boolean enable) {
    place.setActive(enable);
    numPlace.setActive(enable);
    line.setActive(enable);
    column.setActive(enable);
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

  public void resetLabelEnd() {
    labelExist.setText("");
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
