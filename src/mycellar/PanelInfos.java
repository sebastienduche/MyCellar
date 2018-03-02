package mycellar;

import mycellar.core.MyCellarLabel;
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

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2013</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.4
 * @since 02/03/18
 */
public class PanelInfos extends JPanel {

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

	public static void Debug(String sText) {
		Program.Debug("PanelInfos: " + sText );
	}
}

class PanelStats extends JPanel {

	private static final long serialVersionUID = 7438892143990782047L;
	private final MyCellarLabel bottles = new MyCellarLabel(Program.getLabel("Infos405"));
	private final MyCellarLabel cellarValue = new MyCellarLabel(Program.getLabel("Infos406"));
	private final MyCellarLabel bottlesNb = new MyCellarLabel();
	private final MyCellarLabel cellarTotal = new MyCellarLabel();
	private final PanelStatsModel model = new PanelStatsModel();
	PanelStats(){
		bottlesNb.setFont(Program.font_label_bold);
		cellarTotal.setFont(Program.font_label_bold);
		JTable table = new JTable(model);
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
	public void refresh() {
		int nbBottles = 0;
		model.clearRows();
		if(!Program.getCave().isEmpty()) {
			for(Rangement r: Program.getCave() ) {
				nbBottles += r.getNbCaseUseAll();
				model.addRow(r, r.getNbCaseUseAll());
			}
		}
		bottlesNb.setText(Integer.toString(nbBottles));
		cellarTotal.setText(Program.getCellarValue() + " " + Program.getCaveConfigString("DEVISE",""));
	}

	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos404")));
		bottles.setText(Program.getLabel("Infos405"));
		cellarValue.setText(Program.getLabel("Infos406"));
	}

	public void setEnable(boolean b){
		setEnabled(b);
		bottles.setEnabled(b);
		bottlesNb.setEnabled(b);
		cellarValue.setEnabled(b);
		cellarTotal.setEnabled(b);
	}

	class PanelStatsModel extends DefaultTableModel{
		private static final long serialVersionUID = -3683870571523007857L;
		private final LinkedList<Rangement> names;
		private final LinkedList<String> values;
		private boolean isInit = false;
		private PanelStatsModel(){
			names = new LinkedList<>();
			values = new LinkedList<>();
			isInit = true;
		}

		private void addRow(Rangement name, int value){
			names.add(name);
			values.add(Integer.toString(value));
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			if (isInit)
				return values.size();
			return 0;
		}

		@Override
		public Object getValueAt(int row, int column) {
			if(column == 0)
				return names.get(row).getNom();
			else if(column == 1)
				return values.get(row);
			return Boolean.FALSE;
		}



		@Override
		public void setValueAt(Object arg0, int row, int column) {
			if(column == 2) {
				Rangement r = names.get(row);
				RangementUtils.putTabStock();
				LinkedList<Rangement> rangements = new LinkedList<>();
				rangements.add(r);
				MyXmlDom.writeRangements("", rangements, false);
				Program.open( new File(Program.getPreviewXMLFileName()) );
			}
		}

		private void clearRows(){
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

class PanelHistory extends JPanel {

	private static final long serialVersionUID = 7574553715737201783L;
	private final TableHistoryValues model;

	PanelHistory() {
		setLayout(new MigLayout("","[grow]","[]"));
		model = new TableHistoryValues(false);
		JTable table = new JTable(model);
		add(table, "grow");
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

		setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos407")));
		setEnabled(false);
	}

	public void refresh(){
		SwingUtilities.invokeLater(() -> {
			model.removeAll();
			if(!Program.getCave().isEmpty())
				model.setHistory(Program.getHistory());
		});
	}

	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos407")));
	}

	public void setEnable(boolean b){
		setEnabled(b);
	}
}
