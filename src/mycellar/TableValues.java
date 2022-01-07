package mycellar;

import mycellar.core.LabelProperty;
import mycellar.core.MyCellarObject;
import mycellar.general.ProgramPanels;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

import static mycellar.Program.getLabel;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.2
 * @since 16/04/21
 */
class TableValues extends AbstractTableModel {

  public static final int ETAT = 0;
  static final int SHOW = 7;
  private static final long serialVersionUID = -3899189654755476591L;
  private final List<String> columnNames = List.of("",
      getLabel("Main.Item", LabelProperty.SINGLE.withCapital()), getLabel("Infos189"), getLabel("Infos217"),
      getLabel("Infos082"), getLabel("Infos028"), getLabel("Infos083"), "");

  private final List<Boolean> listBoolean = new LinkedList<>();
  private final List<MyCellarObject> datas = new LinkedList<>();

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
      Program.Debug("TableValues: Error index " + row + " > " + datas.size());
      return "";
    }
    if (row >= listBoolean.size()) {
      Program.Debug("TableValues: Error listBoolean index " + row + " > " + datas.size());
      return "";
    }
    final MyCellarObject myCellarObject = datas.get(row);
    switch (column) {
      case ETAT:
        return listBoolean.get(row);
      case 1:
        String nom = myCellarObject.getNom();
        return Program.convertStringFromHTMLString(nom);
      case 2:
        return myCellarObject.getAnnee();
      case 3:
        if (myCellarObject.isInTemporaryStock()) {
          return getLabel("Bouteille.TemporaryPlace");
        }
        return myCellarObject.getEmplacement();
      case 4:
        return Integer.toString(myCellarObject.getNumLieu());
      case 5:
        return Integer.toString(myCellarObject.getLigne());
      case 6:
        return Integer.toString(myCellarObject.getColonne());
      case SHOW:
        return Boolean.FALSE;
      default:
        return "";
    }
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
        MyCellarObject bottle = datas.get(row);
        ProgramPanels.showBottle(bottle, true);
        break;
      case ETAT:
        listBoolean.set(row, (Boolean) value);
        break;
    }
  }


  void addObject(MyCellarObject b) {
    if (b != null) {
      datas.add(b);
      listBoolean.add(Boolean.FALSE);
      fireTableDataChanged();
    }
  }

  public void removeAll() {
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

  public List<MyCellarObject> getDatas() {
    return datas;
  }

  boolean hasNotObject(MyCellarObject b) {
    return !datas.contains(b);
  }

  public Bouteille getBouteille(int i) {
    return (Bouteille) datas.get(i);
  }
}
