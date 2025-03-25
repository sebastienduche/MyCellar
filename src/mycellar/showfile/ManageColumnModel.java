package mycellar.showfile;

import mycellar.Erreur;
import mycellar.core.common.MyCellarFields;

import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_NEEDMINIMUM1COLUMN;
import static mycellar.general.ResourceKey.MAIN_COLUMN;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 25/03/25
 */

public class ManageColumnModel extends DefaultTableModel {

  private final List<MyCellarFields> list;
  private final List<Integer> selectedColumns = new LinkedList<>();
  private final Boolean[] values;

  public ManageColumnModel(List<MyCellarFields> list, List<?> cols) {
    this.list = list;
    values = new Boolean[list.size()];
    selectedColumns.clear();
    Arrays.fill(values, Boolean.FALSE);
    for (Object c : cols) {
      if (c instanceof ShowFileColumn) {
        final int index = list.indexOf(((ShowFileColumn<?>) c).getField());
        if (index != -1) {
          values[index] = Boolean.TRUE;
          selectedColumns.add(list.get(index).getIndex());
        }
      } else if (c instanceof MyCellarFields fields) {
        final int index = list.indexOf(c);
        if (index != -1) {
          values[index] = Boolean.TRUE;
          selectedColumns.add(fields.getIndex());
        }
      }
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column == 0;
  }

  @Override
  public String getColumnName(int column) {
    if (column == 0) {
      return "";
    }
    return getLabel(MAIN_COLUMN);
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public int getRowCount() {
    if (list == null) {
      return 0;
    }
    return list.size();
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (column == 0) {
      return values[row];
    }
    return list.get(row);
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    values[row] = (Boolean) value;
    selectedColumns.clear();
    for (int i = 0; i < values.length; i++) {
      if (values[i]) {
        selectedColumns.add(list.get(i).getIndex());
      }
    }
    if (selectedColumns.isEmpty()) {
      Erreur.showSimpleErreur(getError(ERROR_NEEDMINIMUM1COLUMN));
      values[row] = Boolean.TRUE;
      selectedColumns.add(list.get(row).getIndex());
    }
  }

  public List<Integer> getSelectedColumns() {
    return selectedColumns;
  }
}
