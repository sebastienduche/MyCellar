package mycellar.placesmanagement;

import mycellar.Program;

import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2018</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 0.3
 * @since 17/12/20
 */
public class RangementToCreateTableModel extends AbstractTableModel {

  private final Map<String, LinkedList<Part>> map;
  private final LinkedList<String> list;

  RangementToCreateTableModel(Map<String, LinkedList<Part>> map) {
    this.map = map;
    list = new LinkedList<>();
    list.addAll(map.keySet());
  }

  @Override
  public int getRowCount() {
    return map.size();
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public String getColumnName(int column) {
    if (column == 0) {
      return Program.getLabel("Infos208");
    }
    return "";
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    final String name = list.get(rowIndex);
    if (columnIndex == 0) {
      return name;
    }
    final LinkedList<Part> parts = map.get(name);
    int row = 0;
    for (Part part : parts) {
      row += part.getRowSize();
    }
    return MessageFormat.format(Program.getLabel("RangementToCreateTableModel.message"), parts.size(), row);
  }

  public void clear() {
    map.clear();
    fireTableDataChanged();
  }
}
