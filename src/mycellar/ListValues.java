package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarObject;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 23/08/21
 */
class ListValues extends AbstractTableModel {
  static final long serialVersionUID = 200505;

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
    return Program.getLabel("Infos208");
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
  }

  public void setBouteilles(List<? extends IMyCellarObject> b) {
    list = b;
    fireTableDataChanged();
  }

  public void removeBouteille(MyCellarObject num) {
    list.remove(num);
    fireTableDataChanged();
  }

  public Bouteille getBouteille(int x) {
    return (Bouteille) list.get(x);
  }


}
