package mycellar;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 02/03/18
 */
public class ToolTipRenderer extends DefaultTableCellRenderer {
	
  static final long serialVersionUID = 80904;
  /**
   * getTableCellRendererComponent
   *
   * @param table JTable
   * @param value Object
   * @param isSelected boolean
   * @param hasFocus boolean
   * @param row int
   * @param column int
   * @return Component
   */
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (result instanceof JLabel) {
       ( (JLabel) result).setToolTipText( (String) table.getValueAt(row, column));
    }
    return result;
  }
}
