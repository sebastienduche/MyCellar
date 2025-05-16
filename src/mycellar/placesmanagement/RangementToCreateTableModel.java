package mycellar.placesmanagement;

import mycellar.placesmanagement.places.Part;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.util.LinkedList;
import java.util.Map;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.MAIN_NAME;
import static mycellar.general.ResourceKey.RANGEMENTTOCREATETABLEMODEL_MESSAGE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2018
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.9
 * @since 14/03/25
 */
public class RangementToCreateTableModel extends AbstractTableModel {

  @Serial
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
      return getLabel(MAIN_NAME);
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
      row += part.rows().size();
    }
    return getLabel(RANGEMENTTOCREATETABLEMODEL_MESSAGE, parts.size(), row);
  }

  public void clear() {
    map.clear();
    fireTableDataChanged();
  }
}
