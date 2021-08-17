package mycellar.showfile;

import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarObject;
import mycellar.core.common.MyCellarFields;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Society : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 1.1
 * @since 10/05/21
 */

public class ShowFileModel extends TableShowValues {

  private static final long serialVersionUID = -3120339216315975530L;

  private List<ShowFileColumn<?>> columns = new ArrayList<>();

  @Override
  public int getColumnCount() {
    return columns.size();
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (row < monVector.size()) {
      final ShowFileColumn<?> showFileColumn = columns.get(column);
      if (showFileColumn.isButton()) {
        return Boolean.TRUE;
      }
      MyCellarObject b = monVector.get(row);
      return showFileColumn.getDisplayValue(b);
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    MyCellarObject b = monVector.get(row);
    if (!columns.get(column).execute(b, row, column)) {
      fireTableRowsUpdated(row, row);
      return;
    }
    columns.get(column).setValue(b, value);
  }

  /**
   * getColumnName
   *
   * @param column int
   * @return String
   */
  @Override
  public String getColumnName(int column) {
    return columns.get(column).getLabel();
  }

  /**
   * isCellEditable
   *
   * @param row    int
   * @param column int
   * @return boolean
   */
  @Override
  public boolean isCellEditable(int row, int column) {
    ShowFileColumn<?> col = columns.get(column);
    if (col.getField() == MyCellarFields.LINE
        || col.getField() == MyCellarFields.COLUMN) {
      IMyCellarObject b = monVector.get(row);
      return !b.getRangement().isCaisse();
    }
    return col.isEditable();
  }

  void removeAllColumns() {
    columns.clear();
    fireTableStructureChanged();
  }

  public List<ShowFileColumn<?>> getColumns() {
    return columns;
  }

  public void setColumns(List<ShowFileColumn<?>> cols) {
    columns = cols;
    fireTableStructureChanged();
  }
}
