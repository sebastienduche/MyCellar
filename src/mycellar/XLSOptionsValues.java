package mycellar;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2006</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 02/03/18
 */
public class XLSOptionsValues extends AbstractTableModel {
  public static final int ETAT = 0;
  private final String[] columnNames = {"",""};
  private final List<String> oVector = new LinkedList<>();
  private final List<Boolean> oBoolVector = new LinkedList<>();
  private Object[][] values = { { false, "" } };
  static final long serialVersionUID = 260706;

  /**
   * getRowCount
   *
   * @return int
   */
  @Override
  public int getRowCount() {
    return values.length;
  }

  /**
   * getColumnCount
   *
   * @return int
   */
  @Override
  public int getColumnCount() {
    return values[0].length;
  }

  /**
   * getValueAt
   *
   * @param row int
   * @param column int
   * @return Object
   */
  @Override
  public Object getValueAt(int row, int column) {
    return values[row][column];
  }

  /**
   * getColumnName
   *
   * @param column int
   * @return String
   */
  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  /**
   * getColumnClass
   *
   * @param column int
   * @return Class
   */
  @Override
  public Class<?> getColumnClass(int column) {
    Class<?> dataType = super.getColumnClass(column);

    return dataType;
  }

  /**
   * isCellEditable
   *
   * @param row int
   * @param column int
   * @return boolean
   */
  @Override
  public boolean isCellEditable(int row, int column) {
    return (column == ETAT);
  }

  /**
   * setValueAt
   *
   * @param value Object
   * @param row int
   * @param column int
   */
  @Override
  public void setValueAt(Object value, int row, int column) {
    try {
      values[row][column] = value;
    }
    catch (Exception e) {
      Program.showException(e);
    }
  }

  /**
   * addString
   *
   * @param _sText String
   * @param _bState boolean
   */
  public void addString(String _sText, boolean _bState) {

    oVector.add(_sText);
    oBoolVector.add(_bState);
    values = new Object[oVector.size()][2];
    for ( int i = 0; i < oVector.size(); i++ ){
      values[i][1] = oVector.get(i);
      values[i][0] = oBoolVector.get(i);
    }
  }

  /**
   * removeAll: Supprime toute les lignes
   */
  public void removeAll() {

    values = new Object[1][2];
    values[0][0] = false;
    values[0][1] = "";
  }

}
