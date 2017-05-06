package mycellar;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * <p>Titre : Cave � vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci�t� : Seb Informatique</p>
 * @author S�bastien Duch�
 * @version 0.2
 * @since 30/10/05
 */
public class StateRenderer extends JCheckBox implements TableCellRenderer {

	static final long serialVersionUID = 301005;
  /**
   * StateRenderer
   */
  public StateRenderer() {
    super();
  }

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

    try {
      if (isSelected) {
        setForeground(table.getSelectionForeground());
        super.setBackground(table.getSelectionBackground());
      }
      else {
        setForeground(table.getForeground());
        setBackground(table.getBackground());
      }
      boolean isSelect = ( (Boolean) value).booleanValue();
      setSelected(isSelect);
    }
    catch (NullPointerException npe) {

    }
    ;
    return this;
  }
}
