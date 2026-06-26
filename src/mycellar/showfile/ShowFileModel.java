package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.core.common.MyCellarFields;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Society : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.5
 * @since 03/10/25
 */

class ShowFileModel extends TableShowValues {

  private List<ShowFileColumn<?>> columns = new ArrayList<>();

  @Override
  public int getColumnCount() {
    return columns.size();
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (row < bottles.size()) {
      final ShowFileColumn<?> showFileColumn = columns.get(column);
      if (showFileColumn.isButton()) {
        return Boolean.TRUE;
      }
      Bouteille b = bottles.get(row);
      return showFileColumn.getDisplayValue(b);
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    Bouteille b = bottles.get(row);
    if (!columns.get(column).execute(b, row, column)) {
      fireTableRowsUpdated(row, row);
      return;
    }
    columns.get(column).setModelValue(b, value);
  }

  @Override
  public String getColumnName(int column) {
    return columns.get(column).getColumnName();
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    ShowFileColumn<?> col = columns.get(column);
    if (col.getField() == MyCellarFields.LINE
        || col.getField() == MyCellarFields.COLUMN) {
      Bouteille b = bottles.get(row);
      return !b.getAbstractPlace().isSimplePlace();
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

  public void setColumns(List<ShowFileColumn<?>> showFileColumns) {
    columns = showFileColumns;
    fireTableStructureChanged();
  }
}
