package mycellar.capacity;

import mycellar.MyCellarUtils;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.frame.MainFrame;
import mycellar.general.ProgramPanels;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.MessageFormat;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 25/05/22
 */
class CapacityTableModel extends DefaultTableModel {
  public static final int STATE = 1;
  private final String[] columnNames = {getLabel("Main.Values"), ""};

  private final List<String> list;

  private boolean modify;

  CapacityTableModel() {
    list = MyCellarBottleContenance.getList();
  }

  @Override
  public int getRowCount() {
    if (list != null) {
      return list.size();
    }
    return 0;
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (column == STATE) {
      return Boolean.FALSE;
    }
    return list.get(row);
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    final String oldValue = list.get(row);
    if (column == STATE) {
      if (MyCellarBottleContenance.isContenanceUsed(oldValue)) {
        JOptionPane.showMessageDialog(MainFrame.getInstance(), getLabel("CapacityPanel.UnableDeleteCapacity"), getError("Error.error"), JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(MainFrame.getInstance(), MessageFormat.format(getLabel("CapacityPanel.DelCapacityQuestion"), oldValue), getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION)) {
        return;
      }
      list.remove(oldValue);
      fireTableRowsDeleted(row, row);
      setModify(true);
      ProgramPanels.updateAllPanelsForUpdatingCapacity();
      ProgramPanels.createCapacityPanel().updateView();
    } else {
      String newValue = MyCellarUtils.toCleanString(value);
      if (!newValue.isBlank()) {
        MyCellarBottleContenance.rename(oldValue, newValue);
        fireTableDataChanged();
        setModify(true);
        ProgramPanels.updateAllPanelsForUpdatingCapacity();
        ProgramPanels.createCapacityPanel().updateView();
      }
    }
  }

  void addValue(String value) {
    if (list != null) {
      list.add(value);
    }
    fireTableDataChanged();
  }

  public boolean isModify() {
    return modify;
  }

  public void setModify(boolean modify) {
    this.modify = modify;
  }
}
