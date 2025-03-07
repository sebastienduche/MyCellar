package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.frame.MainFrame;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.PanelPlacePosition;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.PlacePosition;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static mycellar.ProgramConstants.FONT_DIALOG_BIG_BOLD;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 4.3
 * @since 25/12/23
 */

public final class MoveLine extends JDialog {

  private final MyCellarSimpleLabel label_end = new MyCellarSimpleLabel();
  private final MyCellarComboBox<PanelPlacePosition.ComboItem> new_line_cbx = new MyCellarComboBox<>();
  private final PanelPlacePosition panelPlace = new MoveLinePanelPlacePosition();

  public MoveLine() {
    setAlwaysOnTop(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(getLabel("Main.Move"));
    setLayout(new MigLayout("", "[]", "[]20px[]10px[]10px[][]20px[]10px"));
    MyCellarLabel titre = new MyCellarLabel("Main.Move");
    titre.setForeground(Color.red);
    titre.setFont(FONT_DIALOG_BIG_BOLD);
    titre.setHorizontalAlignment(SwingConstants.CENTER);
    label_end.setForeground(Color.red);
    label_end.setHorizontalAlignment(SwingConstants.CENTER);
    panelPlace.setModificationDetectionActive(false);

    MyCellarLabel label_new_line = new MyCellarLabel("Move.ToLine");

    MyCellarButton validate = new MyCellarButton("Main.Validate");
    MyCellarButton cancel = new MyCellarButton("Main.Close");

    validate.addActionListener((e) -> validateAndSave());
    cancel.addActionListener((e) -> dispose());

    add(titre, "align center, span 3, wrap");
    add(new MyCellarLabel("MoveLine.MoveFromLine", LabelProperty.PLURAL), "span 3, wrap");
    add(panelPlace, "wrap");
    add(label_new_line, "wrap");
    add(new_line_cbx, "wrap");
    add(label_end, "align center, span 3,wrap");
    add(validate, "align center, span 3, split");
    add(cancel);

    pack();
    setResizable(true);
    if (MyCellarImage.ICON != null) {
      setIconImage(MyCellarImage.ICON.getImage());
    }
    setLocationRelativeTo(MainFrame.getInstance());
    setVisible(true);
  }

  private static void Debug(String text) {
    Program.Debug("MoveLine: " + text);
  }

  private void validateAndSave() {
    Debug("Validating and saving...");
    if (!panelPlace.performValidation(false, this)) {
      Debug("Validating and saving... Failed");
      return;
    }
    final PlacePosition selectedPlace = panelPlace.getSelectedPlacePosition();
    int nNewSelected = new_line_cbx.getSelectedIndex();
    if (selectedPlace.getLine() == nNewSelected || nNewSelected == 0) {
      Erreur.showSimpleErreur(this, getError("Error.wrongLineNumber"));
      return;
    }
    nNewSelected--; // We need the 0 bse index for the next calls
    int nOldSelected = selectedPlace.getLineIndex();
    int nNumLieu = selectedPlace.getPlaceNumIndex();
    ComplexPlace complexPlace = (ComplexPlace) selectedPlace.getAbstractPlace();
    int nNbBottle = complexPlace.getNbCaseUseInLine(nNumLieu, nOldSelected);
    if (nNbBottle == 0) {
      Erreur.showSimpleErreur(this, getError("Error.noItemsToMove", LabelProperty.PLURAL));
      return;
    }
    int nOldColumnCount = complexPlace.getColumnCountAt(nNumLieu, nOldSelected);
    int nNewColumnCount = complexPlace.getColumnCountAt(nNumLieu, nNewSelected);
    if (nOldColumnCount > nNewColumnCount && nNbBottle > nNewColumnCount) {
      Erreur.showSimpleErreur(this, getError("Error.wrongNewColumnNumber"));
      return;
    }
    int nBottle = complexPlace.getNbCaseUseInLine(nNumLieu, nNewSelected);
    if (nBottle > 0) {
      Erreur.showSimpleErreur(this, getError("Error.stillItemsOnLine", LabelProperty.PLURAL));
      return;
    }
    List<MyCellarObject> notMoved = new ArrayList<>();
    for (int i = 0; i < nOldColumnCount; i++) {
      complexPlace.getObject(new PlacePosition.PlacePositionBuilderZeroBased(complexPlace)
          .withNumPlace(nNumLieu)
          .withLine(nOldSelected)
          .withColumn(i)
          .build()).ifPresent(myCellarObject -> {
        Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);
        try {
          complexPlace.moveToLine(myCellarObject, new_line_cbx.getSelectedIndex());
        } catch (MyCellarException myCellarException) {
          notMoved.add(myCellarObject);
        }
      });
    }
    if (!notMoved.isEmpty()) {
      final String value = notMoved.stream().map(IMyCellarObject::getNom).collect(Collectors.joining(", "));
      Erreur.showSimpleErreur(this, getError("MoveLine.UnableToMove", LabelProperty.PLURAL), value);
      Debug("ERROR: Unable to move objects: " + value);
    } else {
      label_end.setText(getLabelWithProperty("MoveLine.ItemsMoved", LabelProperty.THE_PLURAL.withCapital()), true);
    }
    ProgramPanels.updateAllPanelsForUpdatingPlaces();
    ProgramPanels.updateCellOrganizerPanel(true);
    Debug("Validating and saving... Done");
  }

  class MoveLinePanelPlacePosition extends PanelPlacePosition {

    public MoveLinePanelPlacePosition() {
      super(false, false, false, true);
    }

    @Override
    public void onLineSelected(PlacePosition selectedPlace) {
      new MyCellarSwingWorker() {
        @Override
        protected void done() {
          int lineCount = ((ComplexPlace) selectedPlace.getAbstractPlace()).getLineCountAt(selectedPlace.getPlaceNumIndex());
          new_line_cbx.removeAllItems();
          new_line_cbx.addItem(NONE);
          for (int i = 1; i <= lineCount; i++) {
            new_line_cbx.addItem(new ComboItem(i));
          }
        }
      }.execute();
    }
  }
}
