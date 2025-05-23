package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.datas.history.History;
import mycellar.general.ProgramPanels;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static mycellar.MyCellarUtils.convertStringFromHTMLString;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.BOUTEILLE_TEMPORARYPLACE;
import static mycellar.general.ResourceKey.HISTORY_ACTION;
import static mycellar.general.ResourceKey.HISTORY_DATE;
import static mycellar.general.ResourceKey.HISTORY_ENTERED;
import static mycellar.general.ResourceKey.HISTORY_EXITED;
import static mycellar.general.ResourceKey.HISTORY_LABEL;
import static mycellar.general.ResourceKey.HISTORY_LABELFROM;
import static mycellar.general.ResourceKey.HISTORY_LABELIN;
import static mycellar.general.ResourceKey.HISTORY_MODIFIED;
import static mycellar.general.ResourceKey.HISTORY_TOCHECK;
import static mycellar.general.ResourceKey.HISTORY_VALIDATED;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.7
 * @since 21/03/25
 */

class TableHistoryValues extends AbstractTableModel {
  static final int SELECT = 0;
  static final int DATE = 1;
  static final int TYPE = 2;
  static final int ACTION = 4;
  private static final int LABEL = 3;
  private static final int MAX_ROWS = 10;
  private final List<String> columnList = new LinkedList<>();
  private final boolean withFirstColumnEmpty;
  private List<History> fullList = new ArrayList<>();
  private List<History> displayList = new LinkedList<>();
  private Boolean[] booleanTab = null;

  TableHistoryValues() {
    this(false);
  }

  TableHistoryValues(boolean withFirstColumnEmpty) {
    this.withFirstColumnEmpty = withFirstColumnEmpty;
    if (withFirstColumnEmpty) {
      columnList.add("");
    }
    columnList.add(getLabel(HISTORY_DATE));
    columnList.add(getLabel(HISTORY_ACTION));
    columnList.add(getLabel(HISTORY_LABEL));
    columnList.add("");
  }

  @Override
  public int getRowCount() {
    return displayList.size();
  }

  @Override
  public int getColumnCount() {
    return columnList.size();
  }

  @Override
  public Object getValueAt(int row, int column) {
    History h = displayList.get(row);
    if (!withFirstColumnEmpty) {
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
          Program.throwNotImplemented();
        }
        if (b == null) {
          return "";
        }
        String emplacement;
        if (b.isInTemporaryStock()) {
          emplacement = getLabel(BOUTEILLE_TEMPORARYPLACE);
        } else {
          emplacement = convertStringFromHTMLString(b.getEmplacement());
        }
        String sType = "";
        String sLabel = "";
        switch (h.getState()) {
          case ADD:
            sType = getLabel(HISTORY_ENTERED);
            sLabel = getLabel(HISTORY_LABELIN, convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
            break;
          case VALIDATED:
            sType = getLabel(HISTORY_VALIDATED);
            sLabel = getLabel(HISTORY_LABELIN, convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
            break;
          case TOCHECK:
            sType = getLabel(HISTORY_TOCHECK);
            sLabel = getLabel(HISTORY_LABELIN, convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
            break;
          case MODIFY:
            sType = getLabel(HISTORY_MODIFIED);
            sLabel = getLabel(HISTORY_LABELIN, convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
            break;
          case DEL:
            sType = getLabel(HISTORY_EXITED);
            sLabel = getLabel(HISTORY_LABELFROM, convertStringFromHTMLString(b.getNom()), b.getAnnee(), emplacement);
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
    if (!withFirstColumnEmpty) {
      column++;
    }

    if (column == DATE) {
      return LocalDate.class;
    }

    return super.getColumnClass(column);
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    if (!withFirstColumnEmpty) {
      column++;
    }
    return column == ACTION || column == SELECT;
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    if (!withFirstColumnEmpty) {
      column++;
    }
    switch (column) {
      case ACTION:
        History h = displayList.get(row);
        if (Program.isWineType()) {
          IMyCellarObject bottle = h.getBouteille();
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
          IMyCellarObject music = h.getMusic();
          if (h.isDeleted()) {
            ProgramPanels.showBottle(music, false);
          } else {
            Program.Debug("Music Get ID = " + music.getId());
            Program.getStorage().getListMyCellarObject().getBouteille().stream().filter(b -> b.getId() == music.getId()).findFirst()
                .ifPresentOrElse(
                    m -> ProgramPanels.showBottle(m, true),
                    () -> ProgramPanels.showBottle(music, false));
          }
        } else {
          Program.throwNotImplemented();
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
      if (withFirstColumnEmpty) {
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
   * filter the history
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

  IMyCellarObject getObject(int row) {
    if (Program.isMusicType()) {
      return displayList.get(row).getMusic();
    } else if (Program.isWineType()) {
      return displayList.get(row).getBouteille();
    }
    Program.throwNotImplementedForNewType();
    return null;
  }

  boolean isDeleted(int row) {
    return displayList.get(row).isDeleted();
  }
}
