package mycellar;

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
import java.io.Serial;

import static mycellar.MyCellarUtils.isDefined;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.MAIN_RECENTACTIVITY;
import static mycellar.general.ResourceKey.WHATSNEW;
import static mycellar.general.ResourceKey.WHATSNEW1;
import static mycellar.general.ResourceKey.WHATSNEW2;
import static mycellar.general.ResourceKey.WHATSNEW3;

public final class PanelHistory extends JPanel {

  @Serial
  private static final long serialVersionUID = 7574553715737201783L;
  private final TableHistoryValues model;
  private final JTable table;
  private final JPanel whatNewPanel = new JPanel();
  private final MyCellarLabel label1 = new MyCellarLabel(WHATSNEW1);
  private final MyCellarLabel label2 = new MyCellarLabel(WHATSNEW2);
  private final MyCellarLabel label3 = new MyCellarLabel(WHATSNEW3);

  public PanelHistory() {
    setLayout(new MigLayout("", "[grow]", "[]"));
    model = new TableHistoryValues();
    table = new JTable(model);
    add(table, "grow, wrap");
    add(new JPanel(), "grow, push, wrap");

    whatNewPanel.setBorder(BorderFactory.createTitledBorder(getLabel(WHATSNEW)));
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

    setBorder(BorderFactory.createTitledBorder(getLabel(MAIN_RECENTACTIVITY)));
    setEnable(false);
  }

  public void refresh() {
    SwingUtilities.invokeLater(() -> {
      model.removeAll();
      if (!Program.getAbstractPlaces().isEmpty()) {
        model.setHistory(Program.getHistory());
      }
    });
  }

  public void setLabels() {
    setBorder(BorderFactory.createTitledBorder(getLabel(MAIN_RECENTACTIVITY)));
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(TableHistoryValues.ACTION - 1);
    tc.setCellRenderer(new ButtonCellRenderer());
    whatNewPanel.setBorder(BorderFactory.createTitledBorder(getLabel(WHATSNEW)));
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
