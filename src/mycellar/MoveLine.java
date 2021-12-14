package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarException;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarObject;
import mycellar.core.datas.history.HistoryState;
import mycellar.placesmanagement.Rangement;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static mycellar.ProgramConstants.FONT_DIALOG;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.9
 * @since 14/10/21
 */

final class MoveLine extends JDialog {

  static final long serialVersionUID = 40508;
  private final MyCellarLabel label_end = new MyCellarLabel();
  private final MyCellarComboBox<Rangement> place_cbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> num_place_cbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> old_line_cbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> new_line_cbx = new MyCellarComboBox<>();

  MoveLine() {
    setAlwaysOnTop(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(Program.getLabel("Infos363"));
    setLayout(new MigLayout("", "[]", "[]20px[]10px[][]10px[][]20px[]10px[]"));
    MyCellarLabel titre = new MyCellarLabel(LabelType.INFO, "363");
    titre.setForeground(Color.red);
    label_end.setForeground(Color.red);
    titre.setFont(FONT_DIALOG);
    titre.setHorizontalAlignment(SwingConstants.CENTER);
    label_end.setHorizontalAlignment(SwingConstants.CENTER);

    MyCellarLabel label_title = new MyCellarLabel(LabelType.INFO_OTHER, "MoveLine.moveFromLine", LabelProperty.PLURAL);
    MyCellarLabel label_place = new MyCellarLabel(LabelType.INFO, "081");
    MyCellarLabel label_num_place = new MyCellarLabel(LabelType.INFO, "082");
    MyCellarLabel label_old_line = new MyCellarLabel(LabelType.INFO, "028");
    MyCellarLabel label_new_line = new MyCellarLabel(LabelType.INFO, "362");

    MyCellarButton validate = new MyCellarButton(LabelType.INFO, "315");
    MyCellarButton cancel = new MyCellarButton(LabelType.INFO, "019");

    place_cbx.addItem(Program.EMPTY_PLACE);
    Program.getCave().stream().filter(Predicate.not(Rangement::isSimplePlace)).forEach(place_cbx::addItem);
    num_place_cbx.setEnabled(false);
    old_line_cbx.setEnabled(false);
    new_line_cbx.setEnabled(false);

    validate.addActionListener((e) -> {
      int nOldSelected = old_line_cbx.getSelectedIndex();
      int nNewSelected = new_line_cbx.getSelectedIndex();
      if (nNewSelected == 0 || nOldSelected == nNewSelected) {
        Erreur.showSimpleErreur(this, Program.getError("Error192"));
        return;
      }
      int nNumLieu = num_place_cbx.getSelectedIndex();
      Rangement r = (Rangement) place_cbx.getSelectedItem();
      if (r != null && !Program.EMPTY_PLACE.equals(r)) {
        int nBottle = r.getNbCaseUseInLine(nNumLieu - 1, nNewSelected - 1);
        int nNbBottle = r.getNbCaseUseInLine(nNumLieu - 1, nOldSelected - 1);

        int nOldColumnCount = r.getColumnCountAt(nNumLieu - 1, nOldSelected - 1);
        int nNewColumnCount = r.getColumnCountAt(nNumLieu - 1, nNewSelected - 1);
        if (nOldColumnCount > nNewColumnCount && nNbBottle > nNewColumnCount) {
          Erreur.showSimpleErreur(this, Program.getError("Error194"));
          return;
        }
        if (nNbBottle == 0) {
          Erreur.showSimpleErreur(this, Program.getError("Error195", LabelProperty.PLURAL));
          return;
        }
        if (nBottle > 0) {
          Erreur.showSimpleErreur(this, Program.getError("Error193", LabelProperty.PLURAL));
          return;
        }
        List<MyCellarObject> notMoved = new ArrayList<>();
        for (int i = 1; i <= r.getColumnCountAt(nNumLieu - 1, nOldSelected - 1); i++) {
          r.getObject(nNumLieu - 1, nOldSelected - 1, i - 1).ifPresent(myCellarObject -> {
            Program.getStorage().addHistory(HistoryState.MODIFY, myCellarObject);
            try {
              r.moveLine(myCellarObject, nNewSelected);
            } catch (MyCellarException myCellarException) {
              notMoved.add(myCellarObject);
            }
          });
        }
        if (!notMoved.isEmpty()) {
          final String value = notMoved.stream().map(IMyCellarObject::getNom).collect(Collectors.joining(", "));
          Erreur.showSimpleErreur(Program.getError("MoveLine.UnableToMove", LabelProperty.PLURAL), value);
        }
        label_end.setText(Program.getLabel("MoveLine.ItemsMoved", LabelProperty.THE_PLURAL.withCapital()));
      }
    });
    cancel.addActionListener((e) -> close());
    place_cbx.addItemListener(this::lieu_itemStateChanged);
    num_place_cbx.addItemListener(this::num_lieu_itemStateChanged);
    old_line_cbx.addItemListener(this::old_line_itemStateChanged);

    add(titre, "align center, span 3, wrap");
    add(label_title, "span 3, wrap");
    add(label_place);
    add(label_num_place);
    add(label_old_line, "wrap");
    add(place_cbx);
    add(num_place_cbx);
    add(old_line_cbx, "wrap");
    add(label_new_line, "wrap");
    add(new_line_cbx, "wrap");
    add(label_end, "span 3,wrap");
    add(validate, "span 3, split, align center");
    add(cancel);

    setSize(320, 280);
    setResizable(true);
    setIconImage(MyCellarImage.ICON.getImage());
    setLocationRelativeTo(Start.getInstance());
    setVisible(true);
  }

  public static void Debug(String sText) {
    Program.Debug("MoveLine: " + sText);
  }

  private void close() {
    dispose();
  }

  /**
   * lieu_itemStateChanged: Fonction pour la liste des lieux.
   *
   * @param e ItemEvent
   */
  private void lieu_itemStateChanged(ItemEvent e) {
    Debug("Lieu_itemStateChanging...");
    label_end.setText("");

    if (place_cbx.getSelectedIndex() == 0) {
      num_place_cbx.setEnabled(false);
      old_line_cbx.setEnabled(false);
      new_line_cbx.setEnabled(false);
      return;
    }

    num_place_cbx.setEnabled(true);
    boolean bIsCaisse = false;
    Rangement r;
    int nb_emplacement = 0;
    if ((r = (Rangement) place_cbx.getSelectedItem()) != null) {
      nb_emplacement = r.getNbParts();
      bIsCaisse = r.isSimplePlace();
    }
    if (bIsCaisse) { //Type caisse
      num_place_cbx.setEnabled(false);
      old_line_cbx.setEnabled(false);
      new_line_cbx.setEnabled(false);
    }

    num_place_cbx.removeAllItems();
    old_line_cbx.removeAllItems();
    new_line_cbx.removeAllItems();
    num_place_cbx.addItem("");
    for (int i = 1; i <= nb_emplacement; i++) {
      num_place_cbx.addItem(Integer.toString(i));
    }
    num_place_cbx.setVisible(true);
    old_line_cbx.setVisible(true);
    new_line_cbx.setVisible(true);
  }

  /**
   * old_line_itemStateChanged: Fonction pour la liste des lignes.
   *
   * @param e ItemEvent
   */
  private void old_line_itemStateChanged(ItemEvent e) {
    Debug("old_line_itemStateChanging...");
    label_end.setText("");
    new_line_cbx.setEnabled(old_line_cbx.getSelectedIndex() != 0);
  }

  private void num_lieu_itemStateChanged(ItemEvent e) {
    Debug("Num_lieu_itemStateChanging...");
    label_end.setText("");
    int num_select = num_place_cbx.getSelectedIndex();

    if (place_cbx.getSelectedIndex() == 0) {
      old_line_cbx.setEnabled(false);
      new_line_cbx.setEnabled(false);
      return;
    }
    old_line_cbx.setEnabled(true);
    Rangement r;
    if ((r = (Rangement) place_cbx.getSelectedItem()) != null) {
      if (!r.isSimplePlace()) {
        int nb_ligne = r.getLineCountAt(num_select - 1);
        old_line_cbx.removeAllItems();
        new_line_cbx.removeAllItems();
        old_line_cbx.addItem("");
        new_line_cbx.addItem("");
        for (int i = 1; i <= nb_ligne; i++) {
          old_line_cbx.addItem(Integer.toString(i));
          new_line_cbx.addItem(Integer.toString(i));
        }
      }
    }
  }
}
