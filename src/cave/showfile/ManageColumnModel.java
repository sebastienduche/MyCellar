package Cave.showfile;

import java.util.LinkedList;

import javax.swing.table.DefaultTableModel;

import Cave.Erreur;
import Cave.Program;
import Cave.core.MyCellarFields;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 24/02/16
 */

public class ManageColumnModel extends DefaultTableModel {

	private static final long serialVersionUID = 826266254625465003L;
	private LinkedList<MyCellarFields> list;
	private static LinkedList<Integer> result = new LinkedList<Integer>();
	private Boolean[] values = null;
	
	public ManageColumnModel(LinkedList<MyCellarFields> list, LinkedList<?> columns) {
		this.list = list;
		values = new Boolean[list.size()];
		result.clear();
		for(int i=0; i<values.length; i++) {
			values[i] = Boolean.FALSE;
			result.add(this.list.get(i).ordinal());
		}
		for(Object c : columns) {
			if(c instanceof ShowFileColumn)
				values[list.indexOf(((ShowFileColumn) c).getField())] = Boolean.TRUE;
			else if(c instanceof MyCellarFields)
				values[list.indexOf(c)] = Boolean.TRUE;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return column == 0;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column == 0)
			return "";
		return Program.getLabel("Main.Column");
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public int getRowCount() {
		if(list == null)
			return 0;
		return list.size();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		if(column == 0)
			return values[row];
		return list.get(row);
	}
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		values[row] = (Boolean) value;
		result.clear();
		for(int i=0; i<values.length; i++) {
			if(values[i])
				result.add(this.list.get(i).ordinal());
		}
		if(result.size() == 0) {
			new Erreur(Program.getError("ManageColumn.ErrorNb"));
			values[row] = Boolean.TRUE;
			result.add(this.list.get(row).ordinal());
		}
	}
	
	public LinkedList<Integer> getSelectedColumns() {
		return result;
	}
}
