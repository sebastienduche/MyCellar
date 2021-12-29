package mycellar.capacity;

import mycellar.Program;
import mycellar.Start;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.general.ProgramPanels;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.MessageFormat;
import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 29/12/21
 */
class CapacityTableModel extends DefaultTableModel {
  public static final int ETAT = 1;
  static final long serialVersionUID = 220605;
  private final String[] columnNames = {Program.getLabel("Infos401"), ""};

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
    if (column == ETAT) {
      return Boolean.FALSE;
    }
    return list.get(row);
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return true;
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    final String oldValue = list.get(row);
    if (column == ETAT) {
      if (MyCellarBottleContenance.isContenanceUsed(oldValue)) {
        JOptionPane.showMessageDialog(Start.getInstance(), Program.getLabel("CapacityPanel.unableDeleteCapacity"), Program.getLabel("Infos032"), JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(Start.getInstance(), MessageFormat.format(Program.getLabel("CapacityPanel.delCapacityQuestion"), oldValue), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
        return;
      }
      list.remove(oldValue);
      fireTableRowsDeleted(row, row);
      setModify(true);
      ProgramPanels.updateAllPanels();
      ProgramPanels.createCapacityPanel().updateView();
    } else {
      String newValue = Program.toCleanString(value);
      if (!newValue.isBlank()) {
        MyCellarBottleContenance.rename(oldValue, newValue);
        fireTableDataChanged();
        setModify(true);
        ProgramPanels.updateAllPanels();
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
