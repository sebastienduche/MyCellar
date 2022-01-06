package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.PanelPlace;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static mycellar.ProgramConstants.FONT_DIALOG;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.0
 * @since 06/01/22
 */

final class MoveLine extends JDialog {

  private static final long serialVersionUID = 40508;
  private final MyCellarLabel label_end = new MyCellarLabel();
  private final MyCellarComboBox<PanelPlace.ComboItem> new_line_cbx = new MyCellarComboBox<>();
  private final PanelPlace panelPlace = new MoveLinePanelPlace();

  MoveLine() {
    setAlwaysOnTop(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(Program.getLabel("Infos363"));
    setLayout(new MigLayout("", "[]", "[]20px[]10px[]10px[][]20px[]10px"));
    MyCellarLabel titre = new MyCellarLabel(LabelType.INFO, "363");
    titre.setForeground(Color.red);
    titre.setFont(FONT_DIALOG);
    titre.setHorizontalAlignment(SwingConstants.CENTER);
    label_end.setForeground(Color.red);
    label_end.setHorizontalAlignment(SwingConstants.CENTER);
    panelPlace.setModificationDetectionActive(false);

    MyCellarLabel label_new_line = new MyCellarLabel(LabelType.INFO, "362");

    MyCellarButton validate = new MyCellarButton(LabelType.INFO, "315");
    MyCellarButton cancel = new MyCellarButton(LabelType.INFO, "019");

    validate.addActionListener((e) -> validateAndSave());
    cancel.addActionListener((e) -> dispose());

    add(titre, "align center, span 3, wrap");
    add(new MyCellarLabel(LabelType.INFO_OTHER, "MoveLine.moveFromLine", LabelProperty.PLURAL), "span 3, wrap");
    add(panelPlace, "wrap");
    add(label_new_line, "wrap");
    add(new_line_cbx, "wrap");
    add(label_end, "align center, span 3,wrap");
    add(validate, "align center, span 3, split");
    add(cancel);

    pack();
    setResizable(true);
    setIconImage(MyCellarImage.ICON.getImage());
    setLocationRelativeTo(Start.getInstance());
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
    final Place selectedPlace = panelPlace.getSelectedPlace();
    int nNewSelected = new_line_cbx.getSelectedIndex();
    if (selectedPlace.getLine() == nNewSelected || nNewSelected == 0) {
      Erreur.showSimpleErreur(this, Program.getError("Error192"));
      return;
    }
    nNewSelected--; // We need the o bse index for the next calls
    int nOldSelected = selectedPlace.getLineIndex();
    int nNumLieu = selectedPlace.getPlaceNumIndex();
    Rangement rangement = selectedPlace.getRangement();
    int nNbBottle = rangement.getNbCaseUseInLine(nNumLieu, nOldSelected);
    if (nNbBottle == 0) {
      Erreur.showSimpleErreur(this, Program.getError("Error195", LabelProperty.PLURAL));
      return;
    }
    int nOldColumnCount = rangement.getColumnCountAt(nNumLieu, nOldSelected);
    int nNewColumnCount = rangement.getColumnCountAt(nNumLieu, nNewSelected);
    if (nOldColumnCount > nNewColumnCount && nNbBottle > nNewColumnCount) {
      Erreur.showSimpleErreur(this, Program.getError("Error194"));
      return;
    }
    int nBottle = rangement.getNbCaseUseInLine(nNumLieu, nNewSelected);
    if (nBottle > 0) {
      Erreur.showSimpleErreur(this, Program.getError("Error193", LabelProperty.PLURAL));
      return;
    }
    List<MyCellarObject> notMoved = new ArrayList<>();
    for (int i = 0; i < nOldColumnCount; i++) {
      rangement.getObject(nNumLieu, nOldSelected, i).ifPresent(myCellarObject -> {
        Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);
        try {
          rangement.moveToLine(myCellarObject, new_line_cbx.getSelectedIndex());
        } catch (MyCellarException myCellarException) {
          notMoved.add(myCellarObject);
        }
      });
    }
    if (!notMoved.isEmpty()) {
      final String value = notMoved.stream().map(IMyCellarObject::getNom).collect(Collectors.joining(", "));
      Erreur.showSimpleErreur(this, Program.getError("MoveLine.UnableToMove", LabelProperty.PLURAL), value);
      Debug("ERROR: Unable to move objects: " + value);
    } else {
      label_end.setText(Program.getLabel("MoveLine.ItemsMoved", LabelProperty.THE_PLURAL.withCapital()), true);
    }
    ProgramPanels.updateAllPanelsForUpdatingPlaces();
    ProgramPanels.updateCellOrganizerPanel(true);
    Debug("Validating and saving... Done");
  }

  class MoveLinePanelPlace extends PanelPlace {
    public MoveLinePanelPlace() {
      super(false, false, false, true);
    }

    @Override
    public void onLineSelected(Place selectedPlace) {
      new MyCellarSwingWorker() {
        @Override
        protected void done() {
          int lineCount = selectedPlace.getRangement().getLineCountAt(selectedPlace.getPlaceNumIndex());
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
