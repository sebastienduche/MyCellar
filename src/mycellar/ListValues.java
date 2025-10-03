package mycellar;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.MAIN_NAME;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2004
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.7
 * @since 03/10/25
 */
class ListValues extends AbstractTableModel {
  private List<Bouteille> list = new LinkedList<>();

  @Override
  public int getRowCount() {
    return list.size();
  }

  @Override
  public int getColumnCount() {
    return 1;
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (list == null || list.isEmpty())
      return null;
    return list.get(row).getNom();
  }

  @Override
  public String getColumnName(int column) {
    return getLabel(MAIN_NAME);
  }

  void setObjects(List<Bouteille> b) {
    list = b;
    fireTableDataChanged();
  }

  public void removeObject(Bouteille bottle) {
    list.remove(bottle);
    fireTableDataChanged();
  }

  public Bouteille getObject(int x) {
    return list.get(x);
  }

}
