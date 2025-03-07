package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarObject;

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
 * @version 1.5
 * @since 25/12/23
 */
class ListValues extends AbstractTableModel {
  private List<? extends IMyCellarObject> list = new LinkedList<>();

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

  protected void setObjects(List<? extends IMyCellarObject> b) {
    list = b;
    fireTableDataChanged();
  }

  public void removeObject(MyCellarObject num) {
    list.remove(num);
    fireTableDataChanged();
  }

  public MyCellarObject getObject(int x) {
    return (MyCellarObject) list.get(x);
  }

}
