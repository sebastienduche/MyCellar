package mycellar.core.tablecomponents;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 26/12/23
 */
public class ToolTipRenderer extends DefaultTableCellRenderer {

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (result instanceof JLabel label) {
      final Object valueAt = table.getValueAt(row, column);
      label.setToolTipText(valueAt == null ? "" : valueAt.toString());
    }
    return result;
  }
}
