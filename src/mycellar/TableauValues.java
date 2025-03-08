package mycellar;

import mycellar.core.text.LabelProperty;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;

import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;
import static mycellar.general.ResourceKey.CREATESTORAGE_SIMPLESTORAGE;
import static mycellar.general.ResourceKey.MAIN_SEVERALITEMS;
import static mycellar.general.ResourceKey.MAIN_STORAGE;
import static mycellar.general.ResourceKey.STORAGE_NBLINE;
import static mycellar.general.ResourceKey.STORAGE_NBLINES;
import static mycellar.general.ResourceKey.STORAGE_NUMBERLINES;
import static mycellar.general.ResourceKey.STORAGE_NUMBEROF;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.3
 * @since 08/03/25
 */
class TableauValues extends AbstractTableModel {
  public static final int STATE = 0;
  private final String[] columnNames = new String[]{"",
      getLabel(MAIN_STORAGE),
      getLabel(STORAGE_NUMBERLINES),
      getLabelWithProperty(STORAGE_NUMBEROF, LabelProperty.PLURAL)};

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
      case STATE:
        return listBoolean.get(row);
      case 1:
        return rangement.getName();
      case 2:
        if (rangement.isSimplePlace()) {
          return getLabel(CREATESTORAGE_SIMPLESTORAGE);
        }
        int nombre_ligne = 0;
        for (int k = 0; k < rangement.getPartCount(); k++) {
          nombre_ligne += ((ComplexPlace) rangement).getLineCountAt(k);
        }
        if (nombre_ligne <= 1) {
          return MessageFormat.format(getLabel(STORAGE_NBLINE), nombre_ligne);
        }
        return MessageFormat.format(getLabel(STORAGE_NBLINES), nombre_ligne);
      case 3:
        int nombre_vin = 0;
        if (rangement.isSimplePlace()) {
          nombre_vin = rangement.getTotalCountCellUsed();
        } else {
          for (int k = 0; k < rangement.getPartCount(); k++) {
            nombre_vin += rangement.getCountCellUsed(k);
          }
        }
        return MessageFormat.format(getLabelWithProperty(MAIN_SEVERALITEMS, new LabelProperty(nombre_vin > 1)), nombre_vin);
    }
    return "";
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return (column == STATE);
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
