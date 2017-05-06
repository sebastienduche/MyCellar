package mycellar;

import java.util.LinkedList;

import javax.swing.table.*;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 20/04/16
 */
public class ManageListTableValue extends AbstractTableModel {
  public final static int ETAT = 0;
  static final long serialVersionUID = 220605;
  private String[] columnNames = {"", Program.getLabel("Infos401")
  };

  private LinkedList<Boolean> values = new LinkedList<Boolean>();
  private LinkedList<String> list = new LinkedList<String>();

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
    return columnNames.length;
  }

  /**
   * getValueAt
   *
   * @param row int
   * @param column int
   * @return Object
   */
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
    	if(column == ETAT)
    		values.set(row, (Boolean)value);
    }
    catch (Exception e) {
      Program.showException(e);
    }
  }

  public void setValues(LinkedList<String> list){
	  if(list != null)
		  this.list = list;
	  values.clear();
	  for(int i=0;i<list.size();i++)
		  values.add(new Boolean(false));
	  this.fireTableDataChanged();
  }
  
  public void addValue(String value){
	  if(list != null)
	  {
		  list.add(value);
		  values.add(new Boolean(false));
	  }
	  this.fireTableDataChanged();
  }
  
  public void removeValueAt(LinkedList<Integer> index){
	  if(list != null)
	  {
		  for(int i = index.size()-1;i>=0;i--){
			  Integer val = index.get(i);
		  list.remove(val.intValue());
		  values.remove(val.intValue());
		  }
	  }
	  this.fireTableDataChanged();
  }
  
  public LinkedList<Integer> getSelectedRows(){
	  LinkedList<Integer> indexes = new LinkedList<Integer>();
	  for(int i=0;i<list.size();i++){
		  if(((Boolean)values.get(i)).equals(Boolean.TRUE))
			  indexes.add(new Integer(i));
	  }
	  return indexes;
  }
  
  public LinkedList<String> getSelectedValues(){
	  LinkedList<String> indexes = new LinkedList<String>();
	  for(int i=0;i<list.size();i++){
		  if(((Boolean)values.get(i)).equals(Boolean.TRUE))
			  indexes.add(list.get(i));
	  }
	  return indexes;
  }

}
