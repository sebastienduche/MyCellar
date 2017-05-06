package mycellar;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.2
 * @since 04/02/16
 */

public class TableHistoryValues extends AbstractTableModel {
  public final static int SELECT = 0;
  public final static int DATE = 1;
  public final static int TYPE = 2;
  public final static int LABEL = 3;
  public final static int ACTION = 4;

  public final static int NONE = -1;
  public final static int ADD = 0;
  public final static int MODIFY = 1;
  public final static int DEL = 2;

  static final long serialVersionUID = 0601072;

  private LinkedList<History> m_oList = new LinkedList<History>();
  private LinkedList<History> displayList = new LinkedList<History>();
  private LinkedList<String> columnList = new LinkedList<String>();
  private Boolean[] booleanTab = null;
  private boolean firstcolumn = false;
  
  public TableHistoryValues(boolean firstcolumn){
	  this.firstcolumn = firstcolumn;
	  if(firstcolumn)
		  columnList.add("");
	  columnList.add(Program.getLabel("Infos342"));
	  columnList.add(Program.getLabel("Infos343"));
	  columnList.add(Program.getLabel("Infos344"));
	  columnList.add("");
	  
  }

  /**
   * getRowCount
   *
   * @return int
   */
  public int getRowCount() {
    return displayList.size();
  }

  /**
   * getColumnCount
   *
   * @return int
   */
  public int getColumnCount() {
    return columnList.size();
  }

  /**
   * getValueAt
   *
   * @param row int
   * @param column int
   * @return Object
   */
  public Object getValueAt(int row, int column) {
	  History h = displayList.get(row);
	  if(!firstcolumn)
		  column++;
	  switch(column)
	  {
	  case SELECT:
		  return booleanTab[row];
	  case ACTION:
		  return Boolean.FALSE;
	  case DATE:
		  return h.GetDate();
	  case LABEL:
	  case TYPE:
	  {
		  Bouteille b = h.GetBottle();
		  String sType = "";
		  String sLabel = "";
	        switch (h.GetType()) {
	          case ADD:
	            sType = Program.getLabel("Infos345");
	            sLabel = Program.convertStringFromHTMLString(b.getNom()) + " " + b.getAnnee() + " " + Program.getLabel("Infos348") + " " + Program.convertStringFromHTMLString(b.getEmplacement());
	            break;
	          case MODIFY:
	            sType = Program.getLabel("Infos346");
	            sLabel = Program.convertStringFromHTMLString(b.getNom()) + " " + b.getAnnee() + " " + Program.getLabel("Infos348") + " " + Program.convertStringFromHTMLString(b.getEmplacement());
	            break;
	          case DEL:
	            sType = Program.getLabel("Infos347");
	            sLabel = Program.convertStringFromHTMLString(b.getNom()) + " " + b.getAnnee() + " " + Program.getLabel("Infos349") + " " + Program.convertStringFromHTMLString(b.getEmplacement());
	            break;
	        }
	        if(column == TYPE)
	        	return sType;
	        return sLabel;
	  }
	  default:
		  return "";
		  
	  }
  }

  /**
   * getColumnName
   *
   * @param column int
   * @return String
   */
  public String getColumnName(int column) {
    return columnList.get(column);
  }

  /**
   * getColumnClass
   *
   * @param column int
   * @return Class
   */
  public Class<?> getColumnClass(int column) {
	  if(!firstcolumn)
		  column++;
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
	  if(!firstcolumn)
		  column++;
    if (column == ACTION || column == SELECT) {
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

	  if(!firstcolumn)
		  column++;
    switch (column) {
      case ACTION:
        History h = (History) displayList.get(row);
        Bouteille bottle = h.GetBottle();
        Program.Morehistory = new ShowMoreHistory( bottle );
      break;
      case SELECT:
    	  booleanTab[row] = (Boolean)value;
    }
  }

  /**
   * removeAll: Vidage de la liste.
   */
  public void removeAll() {
    displayList.clear();
    this.fireTableDataChanged();
  }

  /**
   * getData
   *
   * @return Vector
   */
  public LinkedList<History> getData() {

    return m_oList;
  }

  /**
   * getNbData
   *
   * @return int
   */
  public int getNbData() {
    return m_oList.size();
  }

  /**
   * addHistory: Ajout de l'historique.
   *
   * @param v LinkedList
   * @param _nSort int
   */
  public void setHistory(LinkedList<History> v) {
    try {
      m_oList = v;
      displayList = new LinkedList<History>();
      booleanTab = new Boolean[v.size()];
      if(firstcolumn)
      {
	      for(History h:v)
	    	  displayList.addFirst(h);
      }
      else
      {
    	  Iterator<History> it = v.descendingIterator();
    	  int n = 0;
    	  while(it.hasNext())
    	  {
    		  if( n == 10)
    			  break;
    		  displayList.add(it.next());
    		  n++;
    	  }
      }
      for(int i=0; i<booleanTab.length; i++)
    	  booleanTab[i] = Boolean.FALSE;
      this.fireTableDataChanged();
    }
    catch (Exception e) {
      Program.showException(e);
    }
  }

  /**
   * SetFilter: Filtre l'historique
   *
   * @param _nFilter int
   */
  public void SetFilter(int _nFilter) {

    try {
      displayList.clear();
      for (int i = 0; i < m_oList.size(); i++) {
        History h = (History) m_oList.get(i);
        if ( _nFilter == NONE || h.GetType() == _nFilter) {
        	displayList.addLast(h);
        }
      }
      booleanTab = new Boolean[displayList.size()];
      for(int i=0; i<booleanTab.length; i++)
    	  booleanTab[i] = Boolean.FALSE;
      this.fireTableDataChanged();
    }
    catch (Exception e) {
      Program.showException(e);
    }
  }
  
  public Bouteille getBottle(int row) {
	  History h = (History) displayList.get(row);
      return h.GetBottle();
  }
  
  public boolean isBottleDeleted(int row) {
	  History h = displayList.get(row);
	  return h.GetType() == History.DEL;
  }
}
