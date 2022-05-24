package mycellar.general;

import mycellar.PanelHistory;
import mycellar.Program;
import mycellar.core.MyCellarSettings;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.LabelType;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.placesmanagement.Rangement;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.LinkedList;
import java.util.List;

import static mycellar.ProgramConstants.EURO;
import static mycellar.ProgramConstants.FONT_LABEL_BOLD;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2013
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.0
 * @since 24/05/22
 */
public final class PanelInfos extends JPanel {

  private static final long serialVersionUID = 7993820887000979660L;
  private final PanelStats panelStats;
  private final PanelHistory panelHistory;

  public PanelInfos() {
    panelStats = new PanelStats();
    panelHistory = new PanelHistory();
    setLayout(new MigLayout("", "[grow][grow]", "[grow]"));
    add(panelStats, "grow");
    add(panelHistory, "grow");
  }

  private static void Debug(String sText) {
    Program.Debug("PanelInfos: " + sText);
  }

  public void refresh() {
    Debug("Refreshing...");
    panelStats.refresh();
    panelHistory.refresh();
  }

  public void setLabels() {
    Debug("setLabels...");
    panelStats.setLabels();
    panelHistory.setLabels();
  }

  public void setEnable(boolean b) {
    panelStats.setEnable(b);
    panelHistory.setEnable(b);
  }
}

final class PanelStats extends JPanel {

  private static final long serialVersionUID = 7438892143990782047L;
  private final MyCellarLabel bottles = new MyCellarLabel("Main.NumberOfItems", LabelProperty.PLURAL, "");
  private final MyCellarLabel cellarValue = new MyCellarLabel(LabelType.INFO_OTHER, "Main.GlobalValue");
  private final MyCellarLabel bottlesNb = new MyCellarLabel();
  private final MyCellarLabel cellarTotal = new MyCellarLabel();
  private final PanelStatsModel model = new PanelStatsModel();
  private final JTable table;

  PanelStats() {
    bottlesNb.setFont(FONT_LABEL_BOLD);
    cellarTotal.setFont(FONT_LABEL_BOLD);
    table = new JTable(model);
    table.getColumnModel().getColumn(1).setMinWidth(40);
    table.getColumnModel().getColumn(1).setMaxWidth(40);
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(2);
    tc.setCellRenderer(new ButtonCellRenderer());
    tc.setCellEditor(new ButtonCellEditor());
    tc.setMinWidth(100);
    tc.setMaxWidth(100);
    setLayout(new MigLayout("", "[grow]", "[]"));
    add(bottles, "gaptop 20px, split 2");
    add(bottlesNb, "gapleft 10px, wrap");
    add(cellarValue, "split 2");
    add(cellarTotal, "gapleft 10px, wrap");
    add(table, "gaptop 20px, grow, wrap");
    bottles.setEnabled(false);
    bottlesNb.setEnabled(false);
    cellarValue.setEnabled(false);
    cellarTotal.setEnabled(false);
    setBorder(BorderFactory.createTitledBorder(getLabel("Infos.Stats")));
    setEnabled(false);
  }

  void refresh() {
    SwingUtilities.invokeLater(() -> {
      model.clearRows();
      int nbBottles = 0;
      for (Rangement rangement : Program.getPlaces()) {
        nbBottles += rangement.getTotalCountCellUsed();
        model.addRow(rangement, rangement.getTotalCountCellUsed());
      }
      String devise = EURO;
      if (Program.hasConfigCaveKey(MyCellarSettings.DEVISE)) {
        devise = Program.getCaveConfigString(MyCellarSettings.DEVISE, EURO);
      }
      cellarTotal.setText(Program.sumAllPrices() + SPACE + devise);
      bottlesNb.setText(Integer.toString(nbBottles));
    });
  }

  void setLabels() {
    setBorder(BorderFactory.createTitledBorder(getLabel("Infos.Stats")));
    bottles.updateText();
    cellarValue.updateText();
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(2);
    tc.setCellRenderer(new ButtonCellRenderer());
  }

  void setEnable(boolean b) {
    setEnabled(b);
    bottles.setEnabled(b);
    bottlesNb.setEnabled(b);
    cellarValue.setEnabled(b);
    cellarTotal.setEnabled(b);
  }

  private static class PanelStatsModel extends DefaultTableModel {
    private static final long serialVersionUID = -3683870571523007857L;
    private final LinkedList<Rangement> names;
    private final LinkedList<String> values;
    private final boolean isInit;

    private PanelStatsModel() {
      names = new LinkedList<>();
      values = new LinkedList<>();
      isInit = true;
    }

    private void addRow(Rangement name, int value) {
      names.add(name);
      values.add(Integer.toString(value));
    }

    @Override
    public int getColumnCount() {
      return 3;
    }

    @Override
    public int getRowCount() {
      if (isInit) {
        return values.size();
      }
      return 0;
    }

    @Override
    public Object getValueAt(int row, int column) {
      if (column == 0) {
        return names.get(row).getName();
      } else if (column == 1) {
        return values.get(row);
      }
      return Boolean.FALSE;
    }

    @Override
    public void setValueAt(Object arg0, int row, int column) {
      if (column == 2) {
        Rangement rangement = names.get(row);
        XmlUtils.writePlacesToHTML("", List.of(rangement), false);
        Program.open(Program.getPreviewHTMLFileName(), false);
      }
    }

    private void clearRows() {
      names.clear();
      values.clear();
      fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
      return col == 2;
    }
  }
}
