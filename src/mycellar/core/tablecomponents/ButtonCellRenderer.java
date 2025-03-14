package mycellar.core.tablecomponents;

import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.general.ResourceKey.SHOWFILE_MORE;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2004
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.0
 * @since 14/03/25
 */
public class ButtonCellRenderer extends JButton implements TableCellRenderer {

  private final String label;
  private ImageIcon image;


  public ButtonCellRenderer() {
    super();
    label = MyCellarLabelManagement.getLabel(SHOWFILE_MORE);
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
    } else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }
    if (value == null) {
      return this;
    }
    boolean isSelect = (Boolean) value;
    setSelected(isSelect);
    setFont(FONT_PANEL);
    setText(label);
    if (image != null) {
      setIcon(image);
    }

    return this;
  }
}
