package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarObject;
import mycellar.core.datas.history.History;
import mycellar.general.ProgramPanels;

import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.1
 * @since 15/05/21
 */

class TableHistoryValues extends AbstractTableModel {
  static final int SELECT = 0;
  static final int DATE = 1;
  static final int TYPE = 2;
  static final int ACTION = 4;
  private static final long serialVersionUID = 2991755646049419440L;
  private static final int LABEL = 3;
  private static final int MAX_ROWS = 10;
  private final List<String> columnList = new LinkedList<>();
  private final boolean firstcolumn;
  private List<History> fullList = new ArrayList<>();
  private List<History> displayList = new LinkedList<>();
  private Boolean[] booleanTab = null;

  TableHistoryValues(boolean firstcolumn) {
    this.firstcolumn = firstcolumn;
    if (firstcolumn) {
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
   * @param row    int
   * @param column int
   * @return Object
   */
  @Override
  public Object getValueAt(int row, int column) {
    History h = displayList.get(row);
    if (!firstcolumn) {
      column++;
    }
    switch (column) {
      case SELECT:
        return booleanTab[row];
      case ACTION:
        return Boolean.FALSE;
      case DATE:
        return h.getLocaleDate();
      case LABEL:
      case TYPE: {
        IMyCellarObject b;
        if (Program.isMusicType()) {
          b = h.getMusic();
        } else if (Program.isWineType()) {
          b = h.getBouteille();
        } else {
          b = null;
          Program.throwNotImplementedIfNotFor(new Music(), Bouteille.class);
        }
        if (b == null) {
          return "";
        }
        String emplacement;
        if (b.isInTemporaryStock()) {
          emplacement = Program.getLabel("Bouteille.TemporaryPlace");
        } else {
          emplacement = Program.convertStringFromHTMLString(b.getEmplacement());
        }
        String sType = "";
        String sLabel = "";
        switch (h.getState()) {
          case ADD:
            sType = Program.getLabel("Infos345");
            sLabel = MessageFormat.format(Program.getLabel("Infos348"), Program.convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
            break;
          case VALIDATED:
            sType = Program.getLabel("History.Validated");
            sLabel = MessageFormat.format(Program.getLabel("Infos348"), Program.convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
            break;
          case TOCHECK:
            sType = Program.getLabel("History.ToCheck");
            sLabel = MessageFormat.format(Program.getLabel("Infos348"), Program.convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
            break;
          case MODIFY:
            sType = Program.getLabel("Infos346");
            sLabel = MessageFormat.format(Program.getLabel("Infos348"), Program.convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
            break;
          case DEL:
            sType = Program.getLabel("Infos347");
            sLabel = MessageFormat.format(Program.getLabel("Infos349"), Program.convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
            break;
          case ALL:
            break;
        }
        if (column == TYPE) {
          return sType;
        }
        return sLabel;
      }
      default:
        return "";
    }
  }

  @Override
  public String getColumnName(int column) {
    return columnList.get(column);
  }

  @Override
  public Class<?> getColumnClass(int column) {
    if (!firstcolumn) {
      column++;
    }

    if (column == DATE) {
      return LocalDate.class;
    }

    Class<?> dataType = super.getColumnClass(column);
    return dataType;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    if (!firstcolumn) {
      column++;
    }
    return column == ACTION || column == SELECT;
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    if (!firstcolumn) {
      column++;
    }
    switch (column) {
      case ACTION:
        if (Program.isWineType()) {
          History h = displayList.get(row);
          MyCellarObject bottle = h.getBouteille();
          if (h.isDeleted()) {
            ProgramPanels.showBottle(bottle, false);
          } else {
            Program.Debug("Bottle Get ID = " + bottle.getId());
            Program.getStorage().getListMyCellarObject().getBouteille().stream().filter(b -> b.getId() == bottle.getId()).findFirst()
                .ifPresentOrElse(
                    bouteille -> ProgramPanels.showBottle(bouteille, true),
                    () -> ProgramPanels.showBottle(bottle, false));
          }
        } else if (Program.isMusicType()) {
          Program.throwNotImplementedIfNotFor(new Music(), Bouteille.class);
        } else {
          Program.throwNotImplementedIfNotFor(new Music(), Bouteille.class);
        }
        break;
      case SELECT:
        booleanTab[row] = (Boolean) value;
        break;
    }
  }

  /**
   * removeAll: Vidage de la liste.
   */
  public void removeAll() {
    displayList.clear();
    fullList = new LinkedList<>();
    fireTableDataChanged();
  }

  History getHistoryAt(int index) {
    return fullList.get(index);
  }

  /**
   * setHistory: Ajout de l'historique.
   *
   * @param list LinkedList
   */
  public void setHistory(List<History> list) {
    try {
      fullList = list;
      displayList = new LinkedList<>();
      booleanTab = new Boolean[list.size()];
      if (firstcolumn) {
        displayList.addAll(list);
      } else {
        Iterator<History> it = list
            .stream()
            .sorted(Comparator.comparing(History::getLocaleDate).reversed())
            .iterator();
        int n = 0;
        while (it.hasNext()) {
          if (n == MAX_ROWS) {
            break;
          }
          displayList.add(it.next());
          n++;
        }
      }
      Arrays.fill(booleanTab, Boolean.FALSE);
      fireTableDataChanged();
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  /**
   * setFilter: Filtre l'historique
   *
   * @param filter int
   */
  void setFilter(int filter) {
    try {
      displayList.clear();
      for (History h : fullList) {
        if (filter == -1 || h.getType() == filter) {
          displayList.add(h);
        }
      }
      booleanTab = new Boolean[displayList.size()];
      Arrays.fill(booleanTab, Boolean.FALSE);
      fireTableDataChanged();
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  MyCellarObject getObject(int row) {
    if (Program.isMusicType()) {
      return displayList.get(row).getMusic();
    } else if (Program.isWineType()) {
      return displayList.get(row).getBouteille();
    }
    Program.throwNotImplementedIfNotFor(new Music(), Bouteille.class);
    return null;
  }

  boolean isDeleted(int row) {
    return displayList.get(row).isDeleted();
  }
}
