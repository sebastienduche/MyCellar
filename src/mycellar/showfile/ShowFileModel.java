package mycellar.showfile;

import java.util.ArrayList;

import mycellar.Bouteille;
import mycellar.Start;
import mycellar.core.MyCellarFields;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Society : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 25/10/17
 */

public class ShowFileModel extends TableShowValues {

	private static final long serialVersionUID = -3120339216315975530L;

	private ArrayList<ShowFileColumn> list = new ArrayList<ShowFileColumn>();

	@Override
	public int getColumnCount() {
		return list.size() + 2;
	}

	@Override
	public Object getValueAt(int row, int column) {
		if(column == 0)
			return values[row];
		if(column == getColumnCount() - 1)
			return Boolean.TRUE;
		Bouteille b = monVector.get(row);
		return list.get(column-1).getValue(b);
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		if(column == 0) {
			values[row] = (Boolean)value;
			return;
		}
		Bouteille b = monVector.get(row);
		if(column == getColumnCount() - 1) {
			Start.showBottle(b, true);
			return;
			
		}
		list.get(column-1).setValue(b, value);
	}

	/**
	 * getColumnName
	 *
	 * @param column int
	 * @return String
	 */
	public String getColumnName(int column) {
		if(column == 0 || column == getColumnCount() - 1)
			return "";
		return list.get(column-1).getLabel();
	}

	/**
	 * isCellEditable
	 *
	 * @param row int
	 * @param column int
	 * @return boolean
	 */
	public boolean isCellEditable(int row, int column) {
		if(column == 0 || column == getColumnCount() - 1)
			return true;
		ShowFileColumn col = list.get(column-1);
		if(col.getField() == MyCellarFields.LINE
			|| col.getField() == MyCellarFields.COLUMN) {
			Bouteille b = monVector.get(row);
			return !b.getRangement().isCaisse();
		}
		return true;
	}
	
	public void removeAllColumns() {
		list = new ArrayList<ShowFileColumn>();
		fireTableStructureChanged();
	}
	
	public void setColumns(ArrayList<ShowFileColumn> cols) {
		list = cols;
		fireTableStructureChanged();
	}

	public ArrayList<ShowFileColumn> getColumns() {
		return list;
	}
}
