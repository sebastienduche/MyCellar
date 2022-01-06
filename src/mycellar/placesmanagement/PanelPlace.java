package mycellar.placesmanagement;

import mycellar.MyCellarControl;
import mycellar.Program;
import mycellar.actions.ChooseCellAction;
import mycellar.core.IPlace;
import mycellar.core.LabelType;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.uicomponents.JModifyComboBox;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.general.XmlUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.2
 * @since 06/01/22
 */
public class PanelPlace extends JPanel implements IPlace {
  protected static final ComboItem NONE = new ComboItem(-1, "");
  private static final long serialVersionUID = -2601861017578176513L;
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
  private final boolean columnComboVisible;
  private final boolean onlyComplexPlaces;
  private boolean listenersEnabled = true;

  public PanelPlace() {
    this(null, false, true, true, true, false);
  }

  public PanelPlace(boolean newLineForError, boolean extraActionsVisible, boolean columnComboVisible, boolean onlyComplexPlaces) {
    this(null, newLineForError, extraActionsVisible, extraActionsVisible, columnComboVisible, onlyComplexPlaces);
  }

  public PanelPlace(Rangement rangement, boolean newLineForError, boolean chooseCellVisible, boolean previewVisible, boolean columnComboVisible, boolean onlyComplexPlaces) {
    this.columnComboVisible = columnComboVisible;
    this.onlyComplexPlaces = onlyComplexPlaces;
    char previewChar = Program.getLabel("PREVIEW").charAt(0);
    preview.setMnemonic(previewChar);
    preview.addActionListener(this::preview_actionPerformed);
    chooseCell = new MyCellarButton(LabelType.INFO_OTHER, "AddVin.ChooseCell", new ChooseCellAction(this));
    setModificationDetectionActive(false);
    initPlaceCombo();
    setLayout(new MigLayout("", "[]30px[]30px[]30px[]30px[grow]30px[]", ""));
    setBorder(BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.LOWERED), Program.getLabel("Infos217")));
    add(new MyCellarLabel(LabelType.INFO, "208"));
    add(labelNumPlace);
    add(labelLine);
    if (columnComboVisible) {
      add(labelColumn, "wrap");
    } else {
      add(new JLabel(), "wrap");
    }
    add(place);
    add(numPlace);
    add(line);
    if (columnComboVisible) {
      add(column);
    }
    if (!newLineForError) {
      add(labelExist, "hidemode 3");
    } else {
      add(new JLabel());
    }
    if (chooseCellVisible) {
      add(chooseCell, "alignx right");
    }
    if (previewVisible) {
      add(preview, "alignx right, wrap");
    } else {
      add(new JLabel(), "alignx right, wrap");
    }
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
    setModificationDetectionActive(true);
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
    place.removeAllItems();
    place.addItem(Program.EMPTY_PLACE);
    if (onlyComplexPlaces) {
      Program.getCave().stream()
          .filter(Predicate.not(Rangement::isSimplePlace))
          .forEach(place::addItem);
    } else {
      Program.getCave().forEach(place::addItem);
    }
  }

  private void managePlaceCombos() {
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

  public void setBeforeObjectLabels(MyCellarObject myCellarObject) {
    setLineColumnVisible(myCellarObject.getRangement());
    previousPlaceLabel.setText(myCellarObject.getEmplacement());
    previousNumPlaceLabel.setText(Integer.toString(myCellarObject.getNumLieu()));
    previousLineLabel.setText(Integer.toString(myCellarObject.getLigne()));
    previousColumnLabel.setText(Integer.toString(myCellarObject.getColonne()));
    setBeforeLabelsVisible(true);
  }

  public void clearBeforeObjectLabels() {
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
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        setListenersEnabled(false);
        resetCombos();
        clearBeforeObjectLabels();
        labelExist.setText("");
        managePlaceCombos();
        setListenersEnabled(true);
      }
    }.execute();
  }

  private void resetCombos() {
    place.reset();
    numPlace.reset();
    line.reset();
    column.reset();
  }

  public void updateView() {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        Debug("Update view...");
        setListenersEnabled(false);
        resetCombos();
        initPlaceCombo();
        managePlaceCombos();
        setListenersEnabled(true);
        clearLabelEnd();
        Debug("Update view... Done");
      }
    }.execute();
  }

  public void selectPlace(MyCellarObject cellarObject) {
    selectPlace(cellarObject.getPlace());
  }

  @Override
  public void selectPlace(Place placeRangement) {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
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
      }
    }.execute();
  }

  private void setListeners() {
    place.addItemListener(this::lieu_itemStateChanged);
    numPlace.addItemListener(this::num_lieu_itemStateChanged);
    line.addItemListener(this::line_itemStateChanged);
    column.addItemListener(this::column_itemStateChanged);
  }

  public void setModificationDetectionActive(boolean active) {
    place.setActive(active);
    numPlace.setActive(active);
    line.setActive(active);
    column.setActive(active);
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
    XmlUtils.writeRangements(Program.getPreviewXMLFileName(), List.of((Rangement) Objects.requireNonNull(place.getSelectedItem())), false);
    Program.open(Program.getPreviewXMLFileName(), false);
    Debug("Previewing... End");
  }

  private void lieu_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled(e)) {
      return;
    }
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
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
      }
    }.execute();
  }

  private void num_lieu_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled(e)) {
      return;
    }
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        Debug("Num_lieu_itemStateChanging...");
        enableAll(false);
        int numPlaceSelectedIndex = numPlace.getSelectedIndex();
        int placeSelectedIndex = place.getSelectedIndex();

        labelExist.setText("");

        if (numPlaceSelectedIndex != 0) {
          Rangement rangement = place.getItemAt(placeSelectedIndex);
          if (!rangement.isSimplePlace()) {
            int nb_ligne = rangement.getLineCountAt(numPlaceSelectedIndex - 1);
            line.removeAllItems();
            column.removeAllItems();
            line.addItem(NONE);
            for (int i = 1; i <= nb_ligne; i++) {
              line.addItem(new ComboItem(i));
            }
          }
        } else {
          line.reset();
        }
        enableAll(true);
        Debug("Num_lieu_itemStateChanging... End");
      }
    }.execute();
  }

  private void line_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled(e)) {
      return;
    }
    if (!columnComboVisible) {
      if (line.getSelectedIndex() > 0) {
        onLineSelected(getSelectedPlace());
      }
      return;
    }
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
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
      }
    }.execute();
  }

  public void onLineSelected(Place selectedPlace) {
    // Can be overridden
  }

  private void column_itemStateChanged(ItemEvent e) {
    if (isListenersDisabled(e)) {
      return;
    }
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
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
      }
    }.execute();
  }

  public void enableSimplePlace(boolean enable) {
    place.setEnabled(enable);
    numPlace.setEnabled(enable);
  }

  public void enablePlace(boolean enable) {
    place.setEnabled(enable);
  }

  public boolean isPlaceModified() {
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
    return performValidation(isModification, null);
  }

  public boolean performValidation(boolean isModification, Component component) {
    final Place placeWithoutValidation = getSelectedPlace();
    if (!isModification) {
      if (MyCellarControl.hasInvalidPlace(placeWithoutValidation, component)) {
        return false;
      }
    }
    if (placeWithoutValidation.hasPlace()) {
      if (MyCellarControl.hasInvalidNumLieuNumber(placeWithoutValidation.getPlaceNum(), placeWithoutValidation.isSimplePlace(), component)) {
        enableAll(true);
        return false;
      }

      if (!placeWithoutValidation.isSimplePlace()) {
        if (MyCellarControl.hasInvalidLineNumber(placeWithoutValidation.getLine(), component)) {
          enableAll(true);
          return false;
        }
        if (columnComboVisible && MyCellarControl.hasInvalidColumnNumber(placeWithoutValidation.getColumn(), component)) {
          enableAll(true);
          return false;
        }
      }
    }
    return true;
  }

  public void clearLabelEnd() {
    labelExist.setText("");
  }

  public static class ComboItem {

    private final int value;
    private final String label;

    ComboItem(int value, String label) {
      this.value = value;
      this.label = label;
    }

    public ComboItem(int value) {
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
