package mycellar.xls;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin
 * <p>Description : Votre description
 * <p>Copyright : Copyright (c) 1998
 * <p>Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 14/06/20
 */
class XLSOptionsValues extends AbstractTableModel {
  static final int ETAT = 0;
  @Serial
  private static final long serialVersionUID = -3584372585227509153L;
  private final String[] columnNames = {"", ""};
  private final List<String> oVector = new LinkedList<>();
  private final List<Boolean> oBoolVector = new LinkedList<>();
  private Object[][] values = {{false, ""}};

  @Override
  public int getRowCount() {
    return values.length;
  }

  @Override
  public int getColumnCount() {
    return values[0].length;
  }

  @Override
  public Object getValueAt(int row, int column) {
    return values[row][column];
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return (column == ETAT);
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    values[row][column] = value;
  }


  void addString(String _sText, boolean _bState) {
    oVector.add(_sText);
    oBoolVector.add(_bState);
    values = new Object[oVector.size()][2];
    for (int i = 0; i < oVector.size(); i++) {
      values[i][0] = oBoolVector.get(i);
      values[i][1] = oVector.get(i);
    }
  }

  public void removeAll() {
    values = new Object[1][2];
    values[0][0] = false;
    values[0][1] = "";
  }

}
