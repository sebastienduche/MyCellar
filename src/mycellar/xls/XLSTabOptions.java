package mycellar.xls;

import mycellar.Program;
import mycellar.Start;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.ProgramConstants.isVK_ENTER;
import static mycellar.ProgramConstants.isVK_O;
import static mycellar.core.MyCellarSettings.BOLD_TAB_XLS;
import static mycellar.core.MyCellarSettings.COLUMN_TAB_WIDTH_XLS;
import static mycellar.core.MyCellarSettings.EMPTY_LINE_PART_XLS;
import static mycellar.core.MyCellarSettings.EMPTY_LINE_PLACE_XLS;
import static mycellar.core.MyCellarSettings.ONE_PER_SHEET_XLS;
import static mycellar.core.MyCellarSettings.TEXT_TAB_SIZE_XLS;
import static mycellar.core.MyCellarSettings.TITLE_TAB_SIZE_XLS;
import static mycellar.core.MyCellarSettings.XLSTAB_COL;
import static mycellar.core.MyCellarSettings.XLSTAB_COL0;
import static mycellar.core.MyCellarSettings.XLSTAB_COL1;
import static mycellar.core.MyCellarSettings.XLSTAB_COL2;
import static mycellar.core.MyCellarSettings.XLSTAB_COL3;
import static mycellar.core.MyCellarSettings.XLS_TAB_TITLE;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2004
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.1
 * @since 25/05/22
 */
public final class XLSTabOptions extends JDialog {
  static final long serialVersionUID = 260706;
  private static final int LARGEUR = 480;
  private static final int HAUTEUR = 550;
  private final MyCellarSpinner title_size = new MyCellarSpinner(1, 99);
  private final MyCellarCheckBox boldTitleCheckBox = new MyCellarCheckBox("Options.Bold");
  private final MyCellarCheckBox onePlacePerSheetCheckBox = new MyCellarCheckBox("XLSOptions.OnePlacePerSheet");
  private final JTextField pdf_title = new JTextField();
  private final MyCellarSpinner text_size = new MyCellarSpinner(1, 99);
  private final MyCellarSpinner column_size = new MyCellarSpinner(1, 99);
  private final MyCellarSpinner empty_line_part = new MyCellarSpinner(1, 99);
  private final MyCellarSpinner empty_line_place = new MyCellarSpinner(1, 99);
  private final MyCellarLabel empty_line_place_label;
  private final XLSOptionsValues xlsOptionsTableValues;

  public XLSTabOptions() {
    setModal(true);
    xlsOptionsTableValues = new XLSOptionsValues();
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle(getLabel("XLSOptions.Title"));
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
    });
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        dispose();
      }
    });

    setSize(LARGEUR, HAUTEUR);

    final JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createEtchedBorder());
    panel.setFont(FONT_PANEL);
    panel.setBorder(BorderFactory.createTitledBorder(getLabel("XLSOptions.FileTitle")));
    MyCellarLabel titleLabel = new MyCellarLabel("XLSOptions.XLSTitle");
    String xls_title = Program.getCaveConfigString(XLS_TAB_TITLE, "");
    pdf_title.setText(xls_title);
    MyCellarLabel MyCellarLabel3 = new MyCellarLabel("Options.TextSize");

    onePlacePerSheetCheckBox.addActionListener(e -> updatePlaceSettings(onePlacePerSheetCheckBox.isSelected()));

    title_size.setValue(Program.getCaveConfigInt(TITLE_TAB_SIZE_XLS, 10));
    text_size.setValue(Program.getCaveConfigInt(TEXT_TAB_SIZE_XLS, 10));
    column_size.setValue(Program.getCaveConfigInt(COLUMN_TAB_WIDTH_XLS, 10));
    empty_line_part.setValue(Program.getCaveConfigInt(EMPTY_LINE_PART_XLS, 1));
    empty_line_place.setValue(Program.getCaveConfigInt(EMPTY_LINE_PLACE_XLS, 3));

    if (Program.getCaveConfigBool(BOLD_TAB_XLS, false)) {
      boldTitleCheckBox.setSelected(true);
    }

    JTable table = new JTable(xlsOptionsTableValues);
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(XLSOptionsValues.ETAT);
    tc.setCellRenderer(new CheckboxCellRenderer());
    tc.setCellEditor(new CheckboxCellEditor());
    table.getColumnModel().getColumn(0).setMinWidth(20);
    table.getColumnModel().getColumn(0).setMaxWidth(20);
    table.getColumnModel().getColumn(1).setWidth(440);
    table.setSize(460, 100);
    JScrollPane scrollPane = new JScrollPane(table);

    xlsOptionsTableValues.addString(getLabel("Main.Name"), Program.getCaveConfigBool(XLSTAB_COL0, true));
    xlsOptionsTableValues.addString(getLabel("Main.Year"), Program.getCaveConfigBool(XLSTAB_COL1, false));
    xlsOptionsTableValues.addString(getLabel("Main.CapacityOrSupport"), Program.getCaveConfigBool(XLSTAB_COL2, false));
    xlsOptionsTableValues.addString(getLabel("Main.Price"), Program.getCaveConfigBool(XLSTAB_COL3, false));

    final JPanel panel1 = new JPanel();
    panel1.setFont(FONT_PANEL);
    panel1.setBorder(BorderFactory.createTitledBorder(getLabel("XLSOptions.StorageProperties")));
    MyCellarButton valider = new MyCellarButton("Main.OK");
    valider.addActionListener(this::valider_actionPerformed);
    MyCellarButton annuler = new MyCellarButton("Main.Cancel");
    annuler.addActionListener((e) -> dispose());
    MyCellarSimpleLabel pt_label1 = new MyCellarSimpleLabel("pt");
    MyCellarLabel MyCellarLabel7 = new MyCellarLabel("Options.TextSize");
    MyCellarSimpleLabel pt_label2 = new MyCellarSimpleLabel("pt");
    MyCellarLabel column_size_label = new MyCellarLabel("XLSOptions.ColumnsWidth");
    MyCellarSimpleLabel pt_label3 = new MyCellarSimpleLabel("px");
    MyCellarLabel empty_line_part_label = new MyCellarLabel("XLSOptions.NbLinesBetweenPart");
    empty_line_place_label = new MyCellarLabel("XLSOptions.NbLinesBetweenPlace");
    MyCellarLabel column_label = new MyCellarLabel("XLSOptions.ColumnsToUse");

    if (Program.getCaveConfigBool(ONE_PER_SHEET_XLS, false)) {
      updatePlaceSettings(true);
    }

    setLayout(new MigLayout("", "grow", ""));
    panel.setLayout(new MigLayout("", "[][grow]", "[][]"));
    panel.add(titleLabel);
    panel.add(pdf_title, "grow, wrap");
    panel.add(MyCellarLabel3);
    panel.add(title_size, "split 3");
    panel.add(pt_label1, "grow");
    panel.add(boldTitleCheckBox, "align right");

    add(panel, "grow, wrap");

    panel1.setLayout(new MigLayout("", "[][]", ""));
    panel1.add(MyCellarLabel7);
    panel1.add(text_size, "split 2");
    panel1.add(pt_label2, "wrap");
    panel1.add(column_size_label);
    panel1.add(column_size, "split 2");
    panel1.add(pt_label3, "wrap");
    panel1.add(empty_line_part_label);
    panel1.add(empty_line_part, "wrap");
    panel1.add(onePlacePerSheetCheckBox, "wrap");
    panel1.add(empty_line_place_label);
    panel1.add(empty_line_place);
    add(panel1, "grow, wrap");
    add(column_label, "gaptop 10px, wrap");
    add(scrollPane, "grow, wrap");
    add(valider, "split 2, center, gaptop 15px");
    add(annuler);
    setLocationRelativeTo(Start.getInstance());
  }

  private void updatePlaceSettings(boolean b) {
    onePlacePerSheetCheckBox.setSelected(b);
    empty_line_place_label.setVisible(!b);
    empty_line_place.setVisible(!b);
  }

  private void valider_actionPerformed(ActionEvent e) {
    Program.putCaveConfigString(XLS_TAB_TITLE, pdf_title.getText());
    Program.putCaveConfigString(TITLE_TAB_SIZE_XLS, title_size.getValue().toString());
    Program.putCaveConfigString(TEXT_TAB_SIZE_XLS, text_size.getValue().toString());
    Program.putCaveConfigString(COLUMN_TAB_WIDTH_XLS, column_size.getValue().toString());
    Program.putCaveConfigString(EMPTY_LINE_PART_XLS, empty_line_part.getValue().toString());
    Program.putCaveConfigString(EMPTY_LINE_PLACE_XLS, empty_line_place.getValue().toString());
    Program.putCaveConfigBool(BOLD_TAB_XLS, boldTitleCheckBox.isSelected());
    Program.putCaveConfigBool(ONE_PER_SHEET_XLS, onePlacePerSheetCheckBox.isSelected());

    // Options des colonnes
    for (int i = 0; i < xlsOptionsTableValues.getRowCount(); i++) {
      Program.putCaveConfigBool(XLSTAB_COL + i, ((Boolean) xlsOptionsTableValues.getValueAt(i, XLSOptionsValues.ETAT)));
    }
    dispose();
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (isVK_O(e) || isVK_ENTER(e)) {
      valider_actionPerformed(null);
    }
  }
}
