package Cave.showfile;

import java.util.LinkedList;

import Cave.Bouteille;
import Cave.Start;
import Cave.core.MyCellarFields;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Society : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 24/02/16
 */

public class ShowFileModel extends TableShowValues {

	private static final long serialVersionUID = -3120339216315975530L;

	private LinkedList<ShowFileColumn> list = new LinkedList<ShowFileColumn>();

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
			Start.showBottle(b);
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
		list = new LinkedList<ShowFileColumn>();
		fireTableStructureChanged();
	}
	
	public void setColumns(LinkedList<ShowFileColumn> cols) {
		list = cols;
		fireTableStructureChanged();
	}

	public LinkedList<ShowFileColumn> getColumns() {
		return list;
	}
}
