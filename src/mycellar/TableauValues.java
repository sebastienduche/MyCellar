package mycellar;

import mycellar.core.text.LabelProperty;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;

import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.0
 * @since 31/05/22
 */
class TableauValues extends AbstractTableModel {
  public static final int ETAT = 0;
  static final long serialVersionUID = 220605;
  private final String[] columnNames = {"", getLabel("Main.Storage"), getLabel("Storage.NumberLines"), getLabel("Storage.NumberOf", LabelProperty.PLURAL)};

  private final List<AbstractPlace> list = new LinkedList<>();
  private final List<Boolean> listBoolean = new LinkedList<>();

  @Override
  public int getRowCount() {
    return list.size();
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public Object getValueAt(int row, int column) {
	AbstractPlace rangement = list.get(row);
    switch (column) {
      case 0:
        return listBoolean.get(row);
      case 1:
        return rangement.getName();
      case 2:
        if (rangement.isSimplePlace()) {
          return getLabel("CreateStorage.SimpleStorage");
        }
        int nombre_ligne = 0;
        for (int k = 0; k < rangement.getPartCount(); k++) {
          nombre_ligne += ((ComplexPlace)rangement).getLineCountAt(k);
        }
        if (nombre_ligne <= 1) {
          return MessageFormat.format(getLabel("Storage.NbLine"), nombre_ligne);
        }
        return MessageFormat.format(getLabel("Storage.NbLines"), nombre_ligne);
      case 3:
        int nombre_vin = 0;
        if (rangement.isSimplePlace()) {
          nombre_vin = rangement.getTotalCountCellUsed();
        } else {
          for (int k = 0; k < rangement.getPartCount(); k++) {
            nombre_vin += ((ComplexPlace)rangement).getTotalCellUsed(k);
          }
        }
        return MessageFormat.format(getLabel("Main.SeveralItems", new LabelProperty(nombre_vin > 1)), nombre_vin);
    }
    return "";
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
    try {
      listBoolean.set(row, (Boolean) value);
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  void addRangement(AbstractPlace r) {
    list.add(r);
    listBoolean.add(Boolean.FALSE);
  }

  public void removeAll() {
    list.clear();
    listBoolean.clear();
  }

  AbstractPlace getRangementAt(int index) {
    return list.get(index);
  }
}
