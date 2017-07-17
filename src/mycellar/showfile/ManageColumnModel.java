package mycellar.showfile;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.table.DefaultTableModel;

import mycellar.Erreur;
import mycellar.Program;
import mycellar.core.MyCellarFields;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 17/07/17
 */

public class ManageColumnModel extends DefaultTableModel {

	private static final long serialVersionUID = 826266254625465003L;
	private ArrayList<MyCellarFields> list;
	private static LinkedList<Integer> result = new LinkedList<Integer>();
	private Boolean[] values = null;
	
	public ManageColumnModel(ArrayList<MyCellarFields> list, ArrayList<?> cols) {
		this.list = list;
		values = new Boolean[list.size()];
		result.clear();
		for(int i=0; i<values.length; i++) {
			values[i] = Boolean.FALSE;
		}
		for(Object c : cols) {
			if(c instanceof ShowFileColumn)
				values[list.indexOf(((ShowFileColumn) c).getField())] = Boolean.TRUE;
			else if(c instanceof MyCellarFields) {
				values[list.indexOf(c)] = Boolean.TRUE;
				result.add(((MyCellarFields)c).ordinal());
			}
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
