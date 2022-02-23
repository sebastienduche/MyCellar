package mycellar.showfile;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 15/05/21
 */
public class SimpleButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

  private static final long serialVersionUID = 3260591898396032750L;
  JButton button;

  public SimpleButtonEditor() {
    button = new JButton();
    button.setBackground(Color.WHITE);
    button.addActionListener(this);
    button.setBorderPainted(false);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    fireEditingStopped();
  }

  @Override
  public Object getCellEditorValue() {
    return "";
  }

  @Override
  public Component getTableCellEditorComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               int row,
                                               int column) {
    return button;
  }
}
