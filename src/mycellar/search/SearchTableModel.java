package mycellar.search;

import mycellar.Bouteille;
import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.general.ProgramPanels;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.BOUTEILLE_TEMPORARYPLACE;
import static mycellar.general.ResourceKey.MAIN_ITEM;
import static mycellar.general.ResourceKey.MAIN_STORAGE;
import static mycellar.general.ResourceKey.MAIN_YEAR;
import static mycellar.general.ResourceKey.MYCELLARFIELDS_COLUMN;
import static mycellar.general.ResourceKey.MYCELLARFIELDS_LINE;
import static mycellar.general.ResourceKey.MYCELLARFIELDS_NUMPLACE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 4.1
 * @since 03/10/25
 */
class SearchTableModel extends AbstractTableModel {

  @Serial
  private static final long serialVersionUID = -3899189654755476591L;
  static final int ETAT = 0;
  static final int SHOW = 7;
  private final List<String> columnNames = List.of("",
      getLabel(MAIN_ITEM),
      getLabel(MAIN_YEAR),
      getLabel(MAIN_STORAGE),
      getLabel(MYCELLARFIELDS_NUMPLACE),
      getLabel(MYCELLARFIELDS_LINE), getLabel(MYCELLARFIELDS_COLUMN), "");

  private final List<Boolean> listBoolean = new ArrayList<>();
  private final List<Bouteille> datas = new ArrayList<>();

  @Override
  public int getRowCount() {
    if (datas == null) {
      return 0;
    }
    return datas.size();
  }

  @Override
  public int getColumnCount() {
    return columnNames.size();
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (row >= datas.size()) {
      Program.Debug("SearchTableModel: Error index " + row + " > " + datas.size());
      return "";
    }
    if (row >= listBoolean.size()) {
      Program.Debug("SearchTableModel: Error listBoolean index " + row + " > " + datas.size());
      return "";
    }
    var bottle = datas.get(row);
    return switch (column) {
      case ETAT -> listBoolean.get(row);
      case 1 -> MyCellarUtils.convertStringFromHTMLString(bottle.getNom());
      case 2 -> bottle.getAnnee();
      case 3 -> {
        if (bottle.isInTemporaryStock()) {
          yield getLabel(BOUTEILLE_TEMPORARYPLACE);
        }
        yield bottle.getEmplacement();
      }
      case 4 -> Integer.toString(bottle.getNumLieu());
      case 5 -> Integer.toString(bottle.getLigne());
      case 6 -> Integer.toString(bottle.getColonne());
      case SHOW -> Boolean.FALSE;
      default -> "";
    };
  }

  @Override
  public String getColumnName(int column) {
    return columnNames.get(column);
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return (column == ETAT || column == SHOW);
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    switch (column) {
      case SHOW:
        ProgramPanels.showBottle(datas.get(row), true);
        break;
      case ETAT:
        listBoolean.set(row, (Boolean) value);
        break;
    }
  }

  void addObjects(List<Bouteille> bottles) {
    if (bottles != null) {
      bottles.forEach(myCellarObject -> {
        datas.add(myCellarObject);
        listBoolean.add(Boolean.FALSE);
      });
      fireTableDataChanged();
    }
  }

  void removeAll() {
    datas.clear();
    listBoolean.clear();
    fireTableDataChanged();
  }

  void removeObject(Bouteille bottle) {
    int index = datas.indexOf(bottle);
    if (index != -1) {
      datas.remove(bottle);
      listBoolean.remove(index);
      fireTableDataChanged();
    }
  }

  List<Bouteille> getDatas() {
    return datas;
  }

  boolean doesNotContain(Bouteille b) {
    return !datas.contains(b);
  }

  List<Bouteille> getSelectedObjects() {
    List<Bouteille> selectedObjects = new ArrayList<>();
    for (int i = 0; i < listBoolean.size(); i++) {
      if (listBoolean.get(i)) {
        selectedObjects.add(datas.get(i));
      }
    }
    return selectedObjects;
  }
}
