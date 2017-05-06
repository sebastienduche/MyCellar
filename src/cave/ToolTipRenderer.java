package Cave;

import java.awt.*;
import javax.swing.*;

/**
 * <p>Titre : Cave � vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci�t� : Seb Informatique</p>
 * @author S�bastien Duch�
 * @version 0.1
 * @since 08/09/04
 */
public class ToolTipRenderer extends javax.swing.table.DefaultTableCellRenderer {
	
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
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (result instanceof JLabel) {
       ( (JLabel) result).setToolTipText( (String) table.getValueAt(row, column));
    }
    return result;
  }
}
