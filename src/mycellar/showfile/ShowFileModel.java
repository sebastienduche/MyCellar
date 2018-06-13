package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.Start;
import mycellar.core.MyCellarFields;

import java.util.ArrayList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Society : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 15/03/18
 */

public class ShowFileModel extends TableShowValues {

	private static final long serialVersionUID = -3120339216315975530L;

	private ArrayList<ShowFileColumn> list = new ArrayList<>();

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
		return list.get(column-1).getDisplayValue(b);
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		if(column == 0) {
			values[row] = (Boolean)value;
			return;
		}
		Bouteille b = monVector.get(row);
		if(column == getColumnCount() - 1) {
			Start.getInstance().showBottle(b, true);
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
	@Override
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
	@Override
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
	
	void removeAllColumns() {
		list = new ArrayList<>();
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
