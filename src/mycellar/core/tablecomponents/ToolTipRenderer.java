package mycellar.core.tablecomponents;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 0.4
 * @since 27/05/21
 */
public class ToolTipRenderer extends DefaultTableCellRenderer {

  static final long serialVersionUID = 80904;

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (result instanceof JLabel) {
      ((JLabel) result).setToolTipText(table.getValueAt(row, column).toString());
    }
    return result;
  }
}
