package mycellar;

import java.util.LinkedList;

import javax.swing.table.*;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.9
 * @since 04/05/15
 */
public class ListValues extends AbstractTableModel {
  static final long serialVersionUID = 200505;

  private LinkedList<Bouteille> list = new LinkedList<Bouteille>();

  /**
   * getRowCount
   *
   * @return int
   */
  public int getRowCount() {
    return list.size();
  }

  /**
   * getColumnCount
   *
   * @return int
   */
  public int getColumnCount() {
    return 1;
  }

  /**
   * getValueAt
   *
   * @param row int
   * @param column int
   * @return Object
   */
  public Object getValueAt(int row, int column) {
    return list.get(row).getNom();
  }

  /**
   * getColumnName
   *
   * @param column int
   * @return String
   */
  public String getColumnName(int column) {
    return Program.getLabel("Infos208");
  }

  /**
   * isCellEditable
   *
   * @param row int
   * @param column int
   * @return boolean
   */
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  /**
   * setValueAt
   *
   * @param value Object
   * @param row int
   * @param column int
   */
  public void setValueAt(Object value, int row, int column) {
  }

  /**
   * setBouteille: Mise en place de la liste.
   *
   * @param b LinkedList<Bouteille>
   */
  public void setBouteilles(LinkedList<Bouteille> b) {
	  list = b;
	  this.fireTableDataChanged();
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
	return list.get(x);
}


}
