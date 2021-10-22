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
 * @version 0.5
 * @since 21/10/21
 */
public class ToolTipRenderer extends DefaultTableCellRenderer {

  static final long serialVersionUID = 80904;

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (result instanceof JLabel) {
      final Object valueAt = table.getValueAt(row, column);
      ((JLabel) result).setToolTipText(valueAt == null ? "" : valueAt.toString());
    }
    return result;
  }
}
