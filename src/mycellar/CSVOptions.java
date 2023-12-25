package mycellar;

import mycellar.core.MyCellarSettings;
import mycellar.core.common.MyCellarFields;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.frame.MainFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import static mycellar.ProgramConstants.COLUMNS_SEPARATOR;
import static mycellar.ProgramConstants.COMMA;
import static mycellar.ProgramConstants.DOUBLE_DOT;
import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.ProgramConstants.SLASH;
import static mycellar.ProgramConstants.isVK_ENTER;
import static mycellar.ProgramConstants.isVK_O;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2004
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.0
 * @since 25/12/23
 */
final class CSVOptions extends JDialog {
  private final MyCellarCheckBox[] export;
  private final MyCellarComboBox<SeparatorType> separator = new MyCellarComboBox<>();
  private final int nb_colonnes;
  private final List<MyCellarFields> listColumns;

  CSVOptions() {
    Debug("Constructor");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setModal(true);
    setTitle(getLabel("CSVOptions.Title"));

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
    MyCellarLabel info_separator = new MyCellarLabel("Import.Separator");
    listColumns = MyCellarFields.getFieldsList();
    nb_colonnes = listColumns.size();
    export = new MyCellarCheckBox[nb_colonnes];
    final MyCellarSimpleLabel[] colonnes = new MyCellarSimpleLabel[nb_colonnes];
    for (int i = 0; i < nb_colonnes; i++) {
      export[i] = new MyCellarCheckBox("Main.Exported");
      export[i].setSelected(Program.getCaveConfigInt("SIZE_COL" + i + "EXPORT_CSV", 0) == 1);
      colonnes[i] = new MyCellarSimpleLabel(listColumns.get(i).toString());
    }
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new MigLayout("", "[grow][grow]", ""));
    jPanel2.setFont(FONT_PANEL);
    MyCellarButton valider = new MyCellarButton("Main.OK");
    separator.addItem(SeparatorType.COMMA);
    separator.addItem(SeparatorType.COLUMNS);
    separator.addItem(SeparatorType.DOUBLE_DOT);
    separator.addItem(SeparatorType.SLASH);
    String key = Program.getCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, COLUMNS_SEPARATOR);
    SeparatorType separatorType = SeparatorType.fromValue(key);
    separator.setSelectedItem(separatorType);

    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton("Main.Cancel");
    annuler.addActionListener((e) -> dispose());

    add(info_separator, "split 2");
    add(separator, "wrap");

    JScrollPane scrollPane = new JScrollPane(jPanel2);
    scrollPane.setBorder(BorderFactory.createTitledBorder(getLabel("Options.TableColumns")));
    add(scrollPane, "grow, gaptop 15px, wrap");
    for (int i = 0; i < nb_colonnes; i++) {
      jPanel2.add(colonnes[i], "grow");
      jPanel2.add(export[i], "wrap");
    }

    add(valider, "gaptop 15px, split 2, center");
    add(annuler);
    pack();
    setLocationRelativeTo(MainFrame.getInstance());
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
    switch (separator.getSelectedItem()) {
      case SeparatorType.COMMA:
        Program.putCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, COMMA);
        break;
      case SeparatorType.COLUMNS:
        Program.putCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, COLUMNS_SEPARATOR);
        break;
      case SeparatorType.DOUBLE_DOT:
        Program.putCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, DOUBLE_DOT);
        break;
      case SeparatorType.SLASH:
        Program.putCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, SLASH);
        break;
      case null:
        throw new IllegalStateException("Unexpected null value ");
      default:
        throw new IllegalStateException("Unexpected value: " + separator.getSelectedItem());
    }
    dispose();
  }

  private enum SeparatorType {
    COMMA(ProgramConstants.COMMA, getLabel("CSV.SeparatorComma")),
    DOUBLE_DOT(ProgramConstants.DOUBLE_DOT, getLabel("CSV.SeparatorDoubleDot")),
    SLASH(ProgramConstants.SLASH, getLabel("CSV.SeparatorSlash")),
    COLUMNS(COLUMNS_SEPARATOR, getLabel("CSV.SeparatorDotComma"));
    private final String separator;
    private final String label;

    SeparatorType(String separator, String label) {
      this.separator = separator;
      this.label = label;
    }

    public String getSeparator() {
      return separator;
    }

    @Override
    public String toString() {
      return label;
    }

    public static SeparatorType fromValue(String value) {
      return Arrays.stream(values()).filter(separatorType -> separatorType.getSeparator().equals(value))
          .findFirst()
          .orElse(DOUBLE_DOT);
    }
  }
}
