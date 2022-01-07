package mycellar;

import mycellar.core.LabelType;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.tablecomponents.DateCellRenderer;
import mycellar.core.uicomponents.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import static mycellar.MyCellarUtils.isDefined;

public final class PanelHistory extends JPanel {

  private static final long serialVersionUID = 7574553715737201783L;
  private final TableHistoryValues model;
  private final JTable table;
  private final JPanel whatNewPanel = new JPanel();
  private final MyCellarLabel label1 = new MyCellarLabel(LabelType.INFO_OTHER, "WhatsNew1");
  private final MyCellarLabel label2 = new MyCellarLabel(LabelType.INFO_OTHER, "WhatsNew2");
  private final MyCellarLabel label3 = new MyCellarLabel(LabelType.INFO_OTHER, "WhatsNew3");

  public PanelHistory() {
    setLayout(new MigLayout("", "[grow]", "[]"));
    model = new TableHistoryValues(false);
    table = new JTable(model);
    add(table, "grow, wrap");
    add(new JPanel(), "grow, push, wrap");

    whatNewPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("WhatsNew")));
    whatNewPanel.setLayout(new MigLayout());
    whatNewPanel.add(label1, "wrap");
    if (isDefined(label2.getText())) {
      whatNewPanel.add(label2, "");
    }
    if (isDefined(label3.getText())) {
      whatNewPanel.add(label3, "newline");
    }
    add(whatNewPanel, "growx");
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(TableHistoryValues.ACTION - 1);
    tc.setCellRenderer(new ButtonCellRenderer());
    tc.setCellEditor(new ButtonCellEditor());
    tc.setMinWidth(100);
    tc.setMaxWidth(100);
    tc = tcm.getColumn(TableHistoryValues.TYPE - 1);
    tc.setMinWidth(100);
    tc.setMaxWidth(100);
    tc = tcm.getColumn(0);
    tc.setMinWidth(100);
    tc.setMaxWidth(100);
    tc = tcm.getColumn(TableHistoryValues.DATE - 1);
    tc.setCellRenderer(new DateCellRenderer());

    setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos407")));
    setEnable(false);
  }

  public void refresh() {
    SwingUtilities.invokeLater(() -> {
      model.removeAll();
      if (!Program.getCave().isEmpty()) {
        model.setHistory(Program.getHistory());
      }
    });
  }

  public void setLabels() {
    setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos407")));
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(TableHistoryValues.ACTION - 1);
    tc.setCellRenderer(new ButtonCellRenderer());
    whatNewPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("WhatsNew")));
    label1.updateText();
    label2.updateText();
    label3.updateText();
  }

  public void setEnable(boolean b) {
    setEnabled(b);
    whatNewPanel.setEnabled(b);
    label1.setEnabled(b);
    label2.setEnabled(b);
    label3.setEnabled(b);
  }
}
