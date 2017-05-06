package mycellar;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 01/09/15
 */
public class StateButtonRenderer extends JButton implements TableCellRenderer {

  static final long serialVersionUID = 040107;
  private String label;
  private ImageIcon image;
  
  /**
   * StateButtonRenderer
   */
  public StateButtonRenderer() {
    super();
    label = Program.getLabel("Infos360");
  }
  
  public StateButtonRenderer(String label, ImageIcon image) {
	    super();
	    this.label = label;
	    this.image = image;
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
      setFont(Program.font_panel);
      setText(label);
      if(image != null)
    	  setIcon(image);
    }
    catch (NullPointerException npe) {
    };
    
    return this;
  }
}
