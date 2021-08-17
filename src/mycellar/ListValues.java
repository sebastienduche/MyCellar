package mycellar;

import mycellar.core.IMyCellarObject;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 1.2
 * @since 09/04/21
 */
class ListValues extends AbstractTableModel {
  static final long serialVersionUID = 200505;

  private List<? extends IMyCellarObject> list = new LinkedList<>();

  /**
   * getRowCount
   *
   * @return int
   */
  @Override
  public int getRowCount() {
    return list.size();
  }

  /**
   * getColumnCount
   *
   * @return int
   */
  @Override
  public int getColumnCount() {
    return 1;
  }

  /**
   * getValueAt
   *
   * @param row    int
   * @param column int
   * @return Object
   */
  @Override
  public Object getValueAt(int row, int column) {
    if (list == null || list.isEmpty())
      return null;
    return list.get(row).getNom();
  }

  /**
   * getColumnName
   *
   * @param column int
   * @return String
   */
  @Override
  public String getColumnName(int column) {
    return Program.getLabel("Infos208");
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
    return false;
  }

  /**
   * setValueAt
   *
   * @param value  Object
   * @param row    int
   * @param column int
   */
  @Override
  public void setValueAt(Object value, int row, int column) {
  }

  /**
   * setBouteille: Mise en place de la liste.
   *
   * @param b LinkedList<Bouteille>
   */
  public void setBouteilles(List<? extends IMyCellarObject> b) {
    list = b;
    fireTableDataChanged();
  }

  /**
   * removeBouteille: Suppression d'une bouteille de la liste.
   *
   * @param num Bouteille: bouteille.
   */
  public void removeBouteille(Bouteille num) {
    list.remove(num);
    fireTableDataChanged();
  }

  public Bouteille getBouteille(int x) {
    return (Bouteille) list.get(x);
  }


}
