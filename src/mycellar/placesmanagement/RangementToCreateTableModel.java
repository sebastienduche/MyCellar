package mycellar.placesmanagement;

import mycellar.core.text.LabelType;

import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Map;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * <p>Titre : Cave &agrave; vin
 * <p>Description : Votre description
 * <p>Copyright : Copyright (c) 2018
 * <p>Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.4
 * @since 24/02/22
 */
public class RangementToCreateTableModel extends AbstractTableModel {

  private static final long serialVersionUID = -3771006395292367300L;
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
      return getLabel(LabelType.INFO, "208");
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
    return MessageFormat.format(getLabel(LabelType.INFO_OTHER, "RangementToCreateTableModel.message"), parts.size(), row);
  }

  public void clear() {
    map.clear();
    fireTableDataChanged();
  }
}
