package mycellar.core;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.time.LocalDate;

import static mycellar.Program.DATE_FORMATER;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2020</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 26/10/20
 */
public class DateCellRenderer extends DefaultTableCellRenderer {

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (value instanceof LocalDate) {
      setText(((LocalDate)value).format(DATE_FORMATER));
    }
    return this;
  }
}
