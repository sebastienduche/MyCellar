package mycellar;

import mycellar.core.LabelType;
import mycellar.core.MyCellarSettings;
import mycellar.core.common.MyCellarFields;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import static mycellar.ProgramConstants.COLUMNS_SEPARATOR;
import static mycellar.ProgramConstants.COMMA;
import static mycellar.ProgramConstants.DOUBLE_DOT;
import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.ProgramConstants.SLASH;
import static mycellar.ProgramConstants.isVK_ENTER;
import static mycellar.ProgramConstants.isVK_O;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.6
 * @since 16/12/21
 */
final class CSVOptions extends JDialog {
  static final long serialVersionUID = 230705;
  private final MyCellarCheckBox[] export;
  private final MyCellarComboBox<String> separator = new MyCellarComboBox<>();
  private final int nb_colonnes;
  private final List<MyCellarFields> listColumns;

  CSVOptions() {
    Debug("Constructor");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setModal(true);
    setTitle(Program.getLabel("Infos269"));

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (isVK_O(e) || isVK_ENTER(e)) {
          valider_actionPerformed(null);
        }
      }
    });

    setLayout(new MigLayout("", "grow", ""));
    setResizable(false);
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createEtchedBorder());
    panel.setLayout(new MigLayout("", "grow", ""));
    panel.setFont(FONT_PANEL);
    MyCellarLabel info_separator = new MyCellarLabel(LabelType.INFO, "034"); //Separateur
    listColumns = MyCellarFields.getFieldsList();
    nb_colonnes = listColumns.size();
    export = new MyCellarCheckBox[nb_colonnes];
    final MyCellarLabel[] colonnes = new MyCellarLabel[nb_colonnes];
    for (int i = 0; i < nb_colonnes; i++) {
      export[i] = new MyCellarCheckBox(LabelType.INFO, "261");
      export[i].setSelected(Program.getCaveConfigInt("SIZE_COL" + i + "EXPORT_CSV", 0) == 1);
      colonnes[i] = new MyCellarLabel(listColumns.get(i).toString());
    }
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new MigLayout("", "[grow][grow]", ""));
    jPanel2.setFont(FONT_PANEL);
    MyCellarButton valider = new MyCellarButton(LabelType.INFO_OTHER, "Main.OK");
    separator.addItem(Program.getLabel("Infos002"));
    separator.addItem(Program.getLabel("Infos042"));
    separator.addItem(Program.getLabel("Infos043"));
    separator.addItem(Program.getLabel("Infos044"));
    String key = Program.getCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, COLUMNS_SEPARATOR);
    switch (key) {
      case COLUMNS_SEPARATOR:
        separator.setSelectedIndex(1);
        break;
      case DOUBLE_DOT:
        separator.setSelectedIndex(2);
        break;
      case SLASH:
        separator.setSelectedIndex(3);
        break;
      default:
        Debug("ERROR: Unknown separator");
        break;
    }
    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton(LabelType.INFO, "055");
    annuler.addActionListener((e) -> dispose());

    add(info_separator, "split 2");
    add(separator, "wrap");

    JScrollPane jScrollPane1 = new JScrollPane(jPanel2);
    jScrollPane1.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos258")));
    add(jScrollPane1, "grow, gaptop 15px, wrap");
    for (int i = 0; i < nb_colonnes; i++) {
      jPanel2.add(colonnes[i], "grow");
      jPanel2.add(export[i], "wrap");
    }

    add(valider, "gaptop 15px, split 2, center");
    add(annuler);
    pack();
    setLocationRelativeTo(Start.getInstance());
    Debug("Constructor Done");
  }

  public static void Debug(String text) {
    Program.Debug("CSVOptions: " + text);
  }

  private void valider_actionPerformed(ActionEvent e) {
    Debug("valider_actionPerforming...");
    for (int i = 0; i < nb_colonnes; i++) {
      Program.putCaveConfigBool(MyCellarSettings.EXPORT_CSV + listColumns.get(i).name(), export[i].isSelected());
    }
    int separ_select = separator.getSelectedIndex();
    switch (separ_select) {
      case 0:
        Program.putCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, COMMA);
        break;
      case 1:
        Program.putCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, COLUMNS_SEPARATOR);
        break;
      case 2:
        Program.putCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, DOUBLE_DOT);
        break;
      case 3:
        Program.putCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, SLASH);
        break;
    }
    dispose();
  }

}
