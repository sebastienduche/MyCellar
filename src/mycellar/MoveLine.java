package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.exceptions.MyCellarException;
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
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.swing.SwingConstants.CENTER;
import static mycellar.ProgramConstants.FONT_DIALOG_BIG_BOLD;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_NOITEMSTOMOVE;
import static mycellar.general.ResourceErrorKey.ERROR_STILLITEMSONLINE;
import static mycellar.general.ResourceErrorKey.ERROR_UNABLETOMOVE;
import static mycellar.general.ResourceErrorKey.ERROR_WRONGLINENUMBER;
import static mycellar.general.ResourceErrorKey.ERROR_WRONGNEWCOLUMNNUMBER;
import static mycellar.general.ResourceKey.MAIN_CLOSE;
import static mycellar.general.ResourceKey.MAIN_MOVE;
import static mycellar.general.ResourceKey.MAIN_VALIDATE;
import static mycellar.general.ResourceKey.MOVELINE_ITEMSMOVED;
import static mycellar.general.ResourceKey.MOVELINE_MOVEFROMLINE;
import static mycellar.general.ResourceKey.MOVE_TOLINE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 4.8
 * @since 25/03/25
 */

public final class MoveLine extends JDialog {

  private final MyCellarSimpleLabel label_end = new MyCellarSimpleLabel();
  private final MyCellarComboBox<PanelPlacePosition.ComboItem> new_line_cbx = new MyCellarComboBox<>();
  private final PanelPlacePosition panelPlace = new MoveLinePanelPlacePosition();

  public MoveLine() {
    setAlwaysOnTop(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(getLabel(MAIN_MOVE));
    setLayout(new MigLayout("", "[]", "[]20px[]10px[]10px[][]20px[]10px"));
    MyCellarLabel titre = new MyCellarLabel(MAIN_MOVE);
    titre.setForeground(Color.red);
    titre.setFont(FONT_DIALOG_BIG_BOLD);
    titre.setHorizontalAlignment(CENTER);
    label_end.setForeground(Color.red);
    label_end.setHorizontalAlignment(CENTER);
    panelPlace.setModificationDetectionActive(false);

    MyCellarLabel label_new_line = new MyCellarLabel(MOVE_TOLINE);

    MyCellarButton validate = new MyCellarButton(MAIN_VALIDATE);
    MyCellarButton cancel = new MyCellarButton(MAIN_CLOSE);

    validate.addActionListener(e -> validateAndSave());
    cancel.addActionListener(e -> dispose());

    add(titre, "align center, span 3, wrap");
    add(new MyCellarLabel(MOVELINE_MOVEFROMLINE), "span 3, wrap");
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
      Erreur.showSimpleErreur(this, getError(ERROR_WRONGLINENUMBER));
      return;
    }
    nNewSelected--; // We need the 0 bse index for the next calls
    int nOldSelected = selectedPlace.getLineIndex();
    int nNumLieu = selectedPlace.getPlaceNumIndex();
    ComplexPlace complexPlace = (ComplexPlace) selectedPlace.getAbstractPlace();
    int nNbBottle = complexPlace.getNbCaseUseInLine(nNumLieu, nOldSelected);
    if (nNbBottle == 0) {
      Erreur.showSimpleErreur(this, getError(ERROR_NOITEMSTOMOVE));
      return;
    }
    int nOldColumnCount = complexPlace.getColumnCountAt(nNumLieu, nOldSelected);
    int nNewColumnCount = complexPlace.getColumnCountAt(nNumLieu, nNewSelected);
    if (nOldColumnCount > nNewColumnCount && nNbBottle > nNewColumnCount) {
      Erreur.showSimpleErreur(this, getError(ERROR_WRONGNEWCOLUMNNUMBER));
      return;
    }
    int nBottle = complexPlace.getNbCaseUseInLine(nNumLieu, nNewSelected);
    if (nBottle > 0) {
      Erreur.showSimpleErreur(this, getError(ERROR_STILLITEMSONLINE));
      return;
    }
    List<IMyCellarObject> notMoved = new ArrayList<>();
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
      String message = String.format("%s\n%s", getError(ERROR_UNABLETOMOVE), value);
      Erreur.showSimpleErreur(this, message);
      Debug("ERROR: Unable to move objects: " + value);
    } else {
      label_end.setText(getLabel(MOVELINE_ITEMSMOVED), true);
    }
    ProgramPanels.updateAllPanelsForUpdatingPlaces();
    ProgramPanels.updateCellOrganizerPanel(true);
    Debug("Validating and saving... Done");
  }

  private class MoveLinePanelPlacePosition extends PanelPlacePosition {

    private MoveLinePanelPlacePosition() {
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
