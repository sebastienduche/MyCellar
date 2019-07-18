package mycellar.showfile;

import mycellar.Erreur;
import mycellar.Program;
import mycellar.core.MyCellarFields;

import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 05/07/19
 */

public class ManageColumnModel extends DefaultTableModel {

	private static final long serialVersionUID = 826266254625465003L;
	private final List<MyCellarFields> list;
	private static final List<Integer> RESULT = new LinkedList<>();
	private final Boolean[] values;
	
	public ManageColumnModel(List<MyCellarFields> list, List<?> cols) {
		this.list = list;
		values = new Boolean[list.size()];
		RESULT.clear();
		for(int i=0; i<values.length; i++) {
			values[i] = Boolean.FALSE;
		}
		for(Object c : cols) {
			if(c instanceof ShowFileColumn) {
				values[list.indexOf(((ShowFileColumn) c).getField())] = Boolean.TRUE;
			}
			else if(c instanceof MyCellarFields) {
				values[list.indexOf(c)] = Boolean.TRUE;
				RESULT.add(((MyCellarFields)c).ordinal());
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
		RESULT.clear();
		for(int i=0; i<values.length; i++) {
			if(values[i])
				RESULT.add(list.get(i).ordinal());
		}
		if(RESULT.isEmpty()) {
			Erreur.showSimpleErreur(Program.getError("ManageColumn.ErrorNb"));
			values[row] = Boolean.TRUE;
			RESULT.add(list.get(row).ordinal());
		}
	}
	
	public List<Integer> getSelectedColumns() {
		return RESULT;
	}
}
