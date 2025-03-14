package mycellar.search;

import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.core.MyCellarObject;
import mycellar.core.text.LabelProperty;
import mycellar.general.ProgramPanels;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;
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
 * @version 3.8
 * @since 14/03/25
 */
class SearchTableModel extends AbstractTableModel {

  @Serial
  private static final long serialVersionUID = -3899189654755476591L;
  static final int ETAT = 0;
  static final int SHOW = 7;
  private final List<String> columnNames = List.of("",
      getLabelWithProperty(MAIN_ITEM, LabelProperty.SINGLE.withCapital()),
      getLabel(MAIN_YEAR),
      getLabel(MAIN_STORAGE),
      getLabel(MYCELLARFIELDS_NUMPLACE),
      getLabel(MYCELLARFIELDS_LINE), getLabel(MYCELLARFIELDS_COLUMN), "");

  private final List<Boolean> listBoolean = new ArrayList<>();
  private final List<MyCellarObject> datas = new ArrayList<>();

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
    final MyCellarObject myCellarObject = datas.get(row);
    return switch (column) {
      case ETAT -> listBoolean.get(row);
      case 1 -> MyCellarUtils.convertStringFromHTMLString(myCellarObject.getNom());
      case 2 -> myCellarObject.getAnnee();
      case 3 -> {
        if (myCellarObject.isInTemporaryStock()) {
          yield getLabel(BOUTEILLE_TEMPORARYPLACE);
        }
        yield myCellarObject.getEmplacement();
      }
      case 4 -> Integer.toString(myCellarObject.getNumLieu());
      case 5 -> Integer.toString(myCellarObject.getLigne());
      case 6 -> Integer.toString(myCellarObject.getColonne());
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

  void addObjects(List<MyCellarObject> myCellarObjects) {
    if (myCellarObjects != null) {
      myCellarObjects.forEach(myCellarObject -> {
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

  void removeObject(MyCellarObject myCellarObject) {
    int index = datas.indexOf(myCellarObject);
    if (index != -1) {
      datas.remove(myCellarObject);
      listBoolean.remove(index);
      fireTableDataChanged();
    }
  }

  List<MyCellarObject> getDatas() {
    return datas;
  }

  boolean doesNotContain(MyCellarObject b) {
    return !datas.contains(b);
  }

  List<MyCellarObject> getSelectedObjects() {
    List<MyCellarObject> selectedObjects = new ArrayList<>();
    for (int i = 0; i < listBoolean.size(); i++) {
      if (listBoolean.get(i)) {
        selectedObjects.add(datas.get(i));
      }
    }
    return selectedObjects;
  }
}
