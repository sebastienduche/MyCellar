package mycellar.showfile;

import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarFields;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Society : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.9
 * @since 09/04/21
 */

public class ShowFileModel extends TableShowValues {

	private static final long serialVersionUID = -3120339216315975530L;

	private List<ShowFileColumn<?>> list = new ArrayList<>();

	@Override
	public int getColumnCount() {
		return list.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if(row < monVector.size()) {
			final ShowFileColumn<?> showFileColumn = list.get(column);
			if (showFileColumn.isButton()) {
				return Boolean.TRUE;
			}
			IMyCellarObject b = monVector.get(row);
			return showFileColumn.getDisplayValue(b);
		}
		return null;
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		IMyCellarObject b = monVector.get(row);
		if (!list.get(column).execute(b, row, column)) {
			fireTableRowsUpdated(row, row);
			return;
		}
		list.get(column).setValue(b, value);
	}

	/**
	 * getColumnName
	 *
	 * @param column int
	 * @return String
	 */
	@Override
	public String getColumnName(int column) {
		return list.get(column).getLabel();
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
		ShowFileColumn<?> col = list.get(column);
		if(col.getField() == MyCellarFields.LINE
			|| col.getField() == MyCellarFields.COLUMN) {
			IMyCellarObject b = monVector.get(row);
			return !b.getRangement().isCaisse();
		}
		return col.isEditable();
	}
	
	void removeAllColumns() {
		list = new ArrayList<>();
		fireTableStructureChanged();
	}
	
	public void setColumns(List<ShowFileColumn<?>> cols) {
		list = cols;
		fireTableStructureChanged();
	}

	public List<ShowFileColumn<?>> getColumns() {
		return list;
	}
}
