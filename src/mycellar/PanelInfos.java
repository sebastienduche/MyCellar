package mycellar;

import mycellar.core.DateCellRenderer;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSettings;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2013</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.4
 * @since 17/12/20
 */
final class PanelInfos extends JPanel {

	private static final long serialVersionUID = 7993820887000979660L;
	private final PanelStats panelStats;
	private final PanelHistory panelHistory;

	public PanelInfos() {
		panelStats = new PanelStats();
		panelHistory = new PanelHistory();
		setLayout(new MigLayout("","[grow][grow]","[grow]"));
		add(panelStats, "grow");
		add(panelHistory, "grow");
	}

	void refresh() {
		Debug("Refreshing...");
		panelStats.refresh();
		panelHistory.refresh();
	}

	void setLabels() {
		Debug("setLabels...");
		panelStats.setLabels();
		panelHistory.setLabels();
	}

	void setEnable(boolean b) {
		panelStats.setEnable(b);
		panelHistory.setEnable(b);
	}

	private static void Debug(String sText) {
		Program.Debug("PanelInfos: " + sText );
	}
}

final class PanelStats extends JPanel {

	private static final long serialVersionUID = 7438892143990782047L;
	private final MyCellarLabel bottles = new MyCellarLabel(LabelType.INFO, "405", LabelProperty.PLURAL);
	private final MyCellarLabel cellarValue = new MyCellarLabel(LabelType.INFO, "406");
	private final MyCellarLabel bottlesNb = new MyCellarLabel();
	private final MyCellarLabel cellarTotal = new MyCellarLabel();
	private final PanelStatsModel model = new PanelStatsModel();
	private final JTable table;

	PanelStats() {
		bottlesNb.setFont(Program.FONT_LABEL_BOLD);
		cellarTotal.setFont(Program.FONT_LABEL_BOLD);
		table = new JTable(model);
		table.getColumnModel().getColumn(1).setMinWidth(40);
		table.getColumnModel().getColumn(1).setMaxWidth(40);
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc = tcm.getColumn(2);
		tc.setCellRenderer(new StateButtonRenderer());
		tc.setCellEditor(new StateButtonEditor());
		tc.setMinWidth(100);
		tc.setMaxWidth(100);
		setLayout(new MigLayout("","[grow]","[]"));
		add(bottles, "gaptop 20px, split 2");
		add(bottlesNb, "gapleft 10px, wrap");
		add(cellarValue, "split 2");
		add(cellarTotal, "gapleft 10px, wrap");
		add(table,"gaptop 20px, grow, wrap");
		bottles.setEnabled(false);
		bottlesNb.setEnabled(false);
		cellarValue.setEnabled(false);
		cellarTotal.setEnabled(false);
		setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos404")));
		setEnabled(false);
	}

	void refresh() {
		SwingUtilities.invokeLater(() -> {
			model.clearRows();
			int nbBottles = 0;
			if (!Program.getCave().isEmpty()) {
				for (Rangement r: Program.getCave()) {
					nbBottles += r.getNbCaseUseAll();
					model.addRow(r, r.getNbCaseUseAll());
				}
			}
			cellarTotal.setText(Program.getCellarValue() + " " + Program.getCaveConfigString(MyCellarSettings.DEVISE,""));
			bottlesNb.setText(Integer.toString(nbBottles));
		});
	}

	void setLabels() {
		setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos404")));
		bottles.updateText();
		cellarValue.updateText();
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc = tcm.getColumn(2);
		tc.setCellRenderer(new StateButtonRenderer());
	}

	void setEnable(boolean b){
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
				return names.get(row).getNom();
			}
			else if (column == 1) {
				return values.get(row);
			}
			return Boolean.FALSE;
		}

		@Override
		public void setValueAt(Object arg0, int row, int column) {
			if (column == 2) {
				Rangement rangement = names.get(row);
				RangementUtils.putTabStock();
				MyXmlDom.writeRangements("", List.of(rangement), false);
				Program.open(new File(Program.getPreviewXMLFileName()));
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

final class PanelHistory extends JPanel {

	private static final long serialVersionUID = 7574553715737201783L;
	private final TableHistoryValues model;
	private final JTable table;
	private final JPanel whatNewPanel = new JPanel();
	private final MyCellarLabel label1 = new MyCellarLabel(LabelType.INFO_OTHER, "WhatsNew1");
	private final MyCellarLabel label2 = new MyCellarLabel(LabelType.INFO_OTHER, "WhatsNew2");

	PanelHistory() {
		setLayout(new MigLayout("","[grow]","[]"));
		model = new TableHistoryValues(false);
		table = new JTable(model);
		add(table, "grow, wrap");
		add(new JPanel(), "grow, push, wrap");

		whatNewPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("WhatsNew")));
		whatNewPanel.setLayout(new MigLayout());
		whatNewPanel.add(label1, "wrap");
		whatNewPanel.add(label2, "");
		add(whatNewPanel, "growx");
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc = tcm.getColumn(TableHistoryValues.ACTION - 1);
		tc.setCellRenderer(new StateButtonRenderer());
		tc.setCellEditor(new StateButtonEditor());
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

	void refresh() {
		SwingUtilities.invokeLater(() -> {
			model.removeAll();
			if (!Program.getCave().isEmpty()) {
				model.setHistory(Program.getHistory());
			}
		});
	}

	void setLabels() {
		setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos407")));
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc = tcm.getColumn(TableHistoryValues.ACTION - 1);
		tc.setCellRenderer(new StateButtonRenderer());
		whatNewPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("WhatsNew")));
		label1.updateText();
		label2.updateText();
	}

	void setEnable(boolean b) {
		setEnabled(b);
		whatNewPanel.setEnabled(b);
		label1.setEnabled(b);
		label2.setEnabled(b);
	}
}
