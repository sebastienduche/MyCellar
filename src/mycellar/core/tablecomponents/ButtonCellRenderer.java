package mycellar.core.tablecomponents;

import mycellar.Program;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.7
 * @since 27/05/21
 */
public class ButtonCellRenderer extends JButton implements TableCellRenderer {

  private final String label;
  private ImageIcon image;

  /**
   * StateButtonRenderer
   */
  public ButtonCellRenderer() {
    super();
    label = Program.getLabel("Infos360");
  }

  public ButtonCellRenderer(String label) {
    super();
    this.label = label;
  }

  public ButtonCellRenderer(String label, ImageIcon image) {
    super();
    this.label = label;
    this.image = image;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    }
    else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }
    boolean isSelect = (Boolean) value;
    setSelected(isSelect);
    setFont(Program.FONT_PANEL);
    setText(label);
    if(image != null) {
      setIcon(image);
    }

    return this;
}
}
