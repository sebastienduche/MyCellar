package mycellar;

import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.3
 * @since 09/11/19
 */

class TableHistoryValues extends AbstractTableModel {

	private static final long serialVersionUID = 2991755646049419440L;
  static final int SELECT = 0;
  private static final int DATE = 1;
  static final int TYPE = 2;
  private static final int LABEL = 3;
  static final int ACTION = 4;

  private List<History> m_oList = new ArrayList<>();
  private List<History> displayList = new LinkedList<>();
  private final List<String> columnList = new LinkedList<>();
  private Boolean[] booleanTab = null;
  private final boolean firstcolumn;
  
  TableHistoryValues(boolean firstcolumn){
	  this.firstcolumn = firstcolumn;
	  if(firstcolumn) {
      columnList.add("");
    }
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
  @Override
  public int getRowCount() {
    return displayList.size();
  }

  /**
   * getColumnCount
   *
   * @return int
   */
  @Override
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
  @Override
  public Object getValueAt(int row, int column) {
	  History h = displayList.get(row);
	  if(!firstcolumn) {
      column++;
    }
	  switch(column)
	  {
	  case SELECT:
		  return booleanTab[row];
	  case ACTION:
		  return Boolean.FALSE;
	  case DATE:
		  return h.getDate();
	  case LABEL:
	  case TYPE:
	  {
		  Bouteille b = h.getBouteille();
      String emplacement;
		  if (b.isInTemporaryStock()) {
		    emplacement = Program.getLabel("Bouteille.TemporaryPlace");
      } else {
		    emplacement = Program.convertStringFromHTMLString(b.getEmplacement());
      }
      String sType = "";
      String sLabel = "";
      switch (h.getType()) {
        case History.ADD:
          sType = Program.getLabel("Infos345");
          sLabel = MessageFormat.format(Program.getLabel("Infos348"), Program.convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
          break;
        case History.VALIDATED:
          sType = Program.getLabel("History.Validated");
          sLabel = MessageFormat.format(Program.getLabel("Infos348"), Program.convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
          break;
        case History.TOCHECK:
          sType = Program.getLabel("History.ToCheck");
          sLabel = MessageFormat.format(Program.getLabel("Infos348"), Program.convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
          break;
        case History.MODIFY:
          sType = Program.getLabel("Infos346");
          sLabel = MessageFormat.format(Program.getLabel("Infos348"), Program.convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
          break;
        case History.DEL:
          sType = Program.getLabel("Infos347");
          sLabel = MessageFormat.format(Program.getLabel("Infos349"), Program.convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
          break;
      }
      if(column == TYPE) {
        return sType;
      }
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
  @Override
  public String getColumnName(int column) {
    return columnList.get(column);
  }

  /**
   * getColumnClass
   *
   * @param column int
   * @return Class
   */
  @Override
  public Class<?> getColumnClass(int column) {
	  if(!firstcolumn) {
      column++;
    }
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
	  if(!firstcolumn) {
      column++;
    }
    return column == ACTION || column == SELECT;
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
	  if(!firstcolumn) {
      column++;
    }
    switch (column) {
      case ACTION:
        History h = displayList.get(row);
        Bouteille bottle = h.getBouteille();
        if(h.isDeleted()) {
          Program.showBottle(bottle, false);
        } else {
        	Optional<Bouteille> optional = Program.getStorage().getListBouteilles().getBouteille().stream().filter(b -> b.getId() == bottle.getId()).findFirst();
        	Program.Debug("Bottle Get ID = "+bottle.getId());
        	if(optional.isPresent()) {
            Program.showBottle(optional.get(), true);
          } else {
            Program.showBottle(bottle, false);
          }
        }
      break;
      case SELECT:
    	  booleanTab[row] = (Boolean)value;
    	  break;
    }
  }

  /**
   * removeAll: Vidage de la liste.
   */
  public void removeAll() {
    displayList.clear();
    fireTableDataChanged();
  }

  /**
   * getData
   *
   * @return List
   */
  List<History> getData() {
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
   * @param list LinkedList
   */
  public void setHistory(List<History> list) {
    try {
      m_oList = list;
      displayList = new LinkedList<>();
      booleanTab = new Boolean[list.size()];
      if(firstcolumn) {
        displayList.addAll(list);
      } else {
    	  Iterator<History> it = list.stream().sorted((o1, o2) -> {
				if(o1.getTime() != null && o2.getTime() != null) {
          return -o1.getTime().compareTo(o2.getTime());
        }
				return -1;
		}).iterator();
    	  int n = 0;
    	  while(it.hasNext()) {
    		  if(n == 10) {
            break;
          }
    		  displayList.add(it.next());
    		  n++;
    	  }
      }
      Arrays.fill(booleanTab, Boolean.FALSE);
      fireTableDataChanged();
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
  void SetFilter(int _nFilter) {
    try {
      displayList.clear();
      for (History h : m_oList) {
        if (_nFilter == -1 || h.getType() == _nFilter) {
        	displayList.add(h);
        }
      }
      booleanTab = new Boolean[displayList.size()];
      Arrays.fill(booleanTab, Boolean.FALSE);
      fireTableDataChanged();
    }
    catch (Exception e) {
      Program.showException(e);
    }
  }
  
  Bouteille getBottle(int row) {
	  return displayList.get(row).getBouteille();
  }
  
  boolean isBottleDeleted(int row) {
	  return displayList.get(row).isDeleted();
  }
}
