package mycellar;

import javax.swing.table.*;
import java.util.Vector;

/**
 * <p>Titre : Cave � vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2006</p>
 * <p>Soci�t� : Seb Informatique</p>
 * @author S�bastien Duch�
 * @version 0.1
 * @since 26/07/06
 */
public class XLSOptionsValues extends AbstractTableModel {
  public final static int ETAT = 0;
  private String[] columnNames = {"",""};
  private Vector<String> oVector = new Vector<String>();
  private Vector<Boolean> oBoolVector = new Vector<Boolean>();
  private Object[][] values = { { new Boolean(false), ""
  }
  };
  static final long serialVersionUID = 260706;

  /**
   * getRowCount
   *
   * @return int
   */
  public int getRowCount() {
    return values.length;
  }

  /**
   * getColumnCount
   *
   * @return int
   */
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
  public Object getValueAt(int row, int column) {
    return values[row][column];
  }

  /**
   * getColumnName
   *
   * @param column int
   * @return String
   */
  public String getColumnName(int column) {
    return columnNames[column];
  }

  /**
   * getColumnClass
   *
   * @param column int
   * @return Class
   */
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
  public boolean isCellEditable(int row, int column) {
    if (column == ETAT) {
      return true;
    }
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
    oBoolVector.add(new Boolean(_bState));
    values = new Object[oVector.size()][2];
    for ( int i = 0; i < oVector.size(); i++ ){
      values[i][1] = oVector.get(i).toString();
      values[i][0] = (Boolean) oBoolVector.get(i);
    }
  }

  /**
   * removeAll: Supprime toute les lignes
   */
  public void removeAll() {

    values = new Object[1][2];
    values[0][0] = new Boolean(false);
    values[0][1] = "";
  }

}
