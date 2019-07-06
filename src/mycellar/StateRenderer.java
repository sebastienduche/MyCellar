package mycellar;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 05/07/19
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
   * StateRenderer
   */
  public StateRenderer(String label) {
    super(label);
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
  @Override
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
      boolean isSelect = ( (Boolean) value);
      setSelected(isSelect);
    }
    catch (NullPointerException npe) {
    }
    return this;
  }
}
