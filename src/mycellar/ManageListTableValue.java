package mycellar;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 02/03/18
 */
public class ManageListTableValue extends AbstractTableModel {
  public static final int ETAT = 0;
  static final long serialVersionUID = 220605;
  private final String[] columnNames = {"", Program.getLabel("Infos401")};

  private final LinkedList<Boolean> values = new LinkedList<>();
  private List<String> list = new LinkedList<>();

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
    return columnNames.length;
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
	  if(column == ETAT)
		  return values.get(row);
	  return list.get(row);
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
    	if(column == ETAT)
    		values.set(row, (Boolean)value);
    }
    catch (Exception e) {
      Program.showException(e);
    }
  }

  public void setValues(List<String> list){
	  if(list != null)
		  this.list = list;
	  values.clear();
	  if (list != null) {
      for (int i = 0; i < list.size(); i++)
        values.add(false);
    }
	  fireTableDataChanged();
  }
  
  public void addValue(String value){
	  if(list != null) {
		  list.add(value);
		  values.add(false);
	  }
	  fireTableDataChanged();
  }
  
  public void removeValueAt(List<Integer> index){
	  if(list != null) {
		  for(int i = index.size()-1;i>=0;i--){
			  Integer val = index.get(i);
		  list.remove(val.intValue());
		  values.remove(val.intValue());
		  }
	  }
	  this.fireTableDataChanged();
  }
  
  public LinkedList<Integer> getSelectedRows(){
	  LinkedList<Integer> indexes = new LinkedList<>();
	  for(int i=0;i<list.size();i++){
		  if(values.get(i).equals(Boolean.TRUE))
			  indexes.add(i);
	  }
	  return indexes;
  }
  
  public LinkedList<String> getSelectedValues(){
	  LinkedList<String> indexes = new LinkedList<>();
	  for(int i=0;i<list.size();i++){
		  if(values.get(i).equals(Boolean.TRUE))
			  indexes.add(list.get(i));
	  }
	  return indexes;
  }

}
