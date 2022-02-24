package mycellar.core.tablecomponents;

import mycellar.core.text.LabelType;
import mycellar.core.text.MyCellarLabelManagement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

import static mycellar.ProgramConstants.FONT_PANEL;


/**
 * <p>Titre : Cave &agrave; vin
 * <p>Description : Votre description
 * <p>Copyright : Copyright (c) 2004
 * <p>Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 05/01/22
 */
public class ButtonCellRenderer extends JButton implements TableCellRenderer {

  private static final long serialVersionUID = -6826155883692278688L;
  private final String label;
  private ImageIcon image;


  public ButtonCellRenderer() {
    super();
    label = MyCellarLabelManagement.getLabel(LabelType.INFO, "360");
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
