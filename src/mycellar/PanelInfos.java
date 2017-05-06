package mycellar;

import java.io.File;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2013</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.8
 * @since 13/11/16
 */
public class PanelInfos extends JPanel {

	private static final long serialVersionUID = 7993820887000979660L;
	private static PanelStats panelStats;
	private static PanelHistory panelHistory;

	public PanelInfos()
	{
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
	private MyCellarLabel bottles = new MyCellarLabel(Program.getLabel("Infos405"));
	private MyCellarLabel cellarValue = new MyCellarLabel(Program.getLabel("Infos406"));
	private MyCellarLabel bottlesNb = new MyCellarLabel();
	private MyCellarLabel cellarTotal = new MyCellarLabel();
	private JTable table;
	private int nbBottles = 0;
	private PanelStatsModel model = new PanelStatsModel();
	public PanelStats(){
		bottlesNb.setFont(Program.font_label_bold);
		cellarTotal.setFont(Program.font_label_bold);
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
	public void refresh() {
		nbBottles = 0;
		model.clearRows();
		double price = 0;
		if(!Program.getCave().isEmpty())
		{
			for(Rangement r: Program.getCave() )
			{
				nbBottles += r.getNbCaseUseAll();
				model.addRow(r, r.getNbCaseUseAll());
			}
			for(Bouteille b: Program.getStorage().getAllList())
				price += b.getPriceDouble();
		}
		bottlesNb.setText(Integer.toString(nbBottles));
		cellarTotal.setText((int)price + " " + Program.getCaveConfigString("DEVISE",""));
        
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
		private LinkedList<Rangement> names = null;
		private LinkedList<String> values = null;
		public PanelStatsModel(){
			names = new LinkedList<Rangement>();
			values = new LinkedList<String>();
		}
		
		public void addRow(Rangement name, int value){
			names.add(name);
			values.add(Integer.toString(value));
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			if(values == null)
				return 0;
			return values.size();
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
			if(column == 2)
			{
				Rangement r = names.get(row);
				r.putTabStock();
				LinkedList<Rangement> rangements = new LinkedList<Rangement>();
				rangements.add(r);
				MyXmlDom.writeRangements("", rangements, false);
				Program.open( new File(Program.getPreviewXMLFileName()) );
			}
		}

		public void clearRows(){
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
	private JTable table;
	private TableHistoryValues model;

	public PanelHistory()
	{
		setLayout(new MigLayout("","[grow]","[]"));
		model = new TableHistoryValues(false);
		table = new JTable(model);
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
			if(Program.getCave().size() > 0)
				model.setHistory(Program.getStorage().getHistory());
		});
	}
	
	public void setEnable(boolean b){
		setEnabled(b);
	}
}
