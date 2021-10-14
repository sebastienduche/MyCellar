package mycellar.showfile;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

import static mycellar.ProgramConstants.FONT_LABEL_BOLD;

class FontBoldTableCellRenderer extends DefaultTableCellRenderer {

  private static final long serialVersionUID = -7366533325659261460L;

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    c.setFont(FONT_LABEL_BOLD);
    return c;
  }
}
