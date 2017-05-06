package Cave;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 13/11/16
 */

public class StateButtonEditor extends JButton implements TableCellEditor {
  protected EventListenerList listenerList = new EventListenerList();
  protected ChangeEvent changeEvent = new ChangeEvent(this);
  static final long serialVersionUID = 040105;

  public StateButtonEditor() {
    super();
    addActionListener((e) -> fireEditingStopped());

  }

  /**
   * addCellEditorListener
   *
   * @param listener CellEditorListener
   */
  public void addCellEditorListener(CellEditorListener listener) {
    listenerList.add(CellEditorListener.class, listener);
  }

  /**
   * removeCellEditorListener
   *
   * @param listener CellEditorListener
   */
  public void removeCellEditorListener(CellEditorListener listener) {
    listenerList.remove(CellEditorListener.class, listener);
  }

  /**
   * fireEditingStopped
   */
  protected void fireEditingStopped() {
    CellEditorListener listener;
    Object[] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i++) {
      if (listeners[i] == CellEditorListener.class) {
        listener = (CellEditorListener) listeners[i + 1];
        listener.editingStopped(changeEvent);
      }
    }
  }

  /**
   * fireEditingCanceled
   */
  protected void fireEditingCanceled() {
    CellEditorListener listener;
    Object[] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i++) {
      if (listeners[i] == CellEditorListener.class) {
        listener = (CellEditorListener) listeners[i + 1];
        listener.editingCanceled(changeEvent);
      }
    }
  }

  /**
   * cancelCellEditing
   */
  public void cancelCellEditing() {
    fireEditingCanceled();
  }

  /**
   * stopCellEditing
   *
   * @return boolean
   */
  public boolean stopCellEditing() {
    fireEditingStopped();
    return true;
  }

  /**
   * isCellEditable
   *
   * @param event EventObject
   * @return boolean
   */
  public boolean isCellEditable(EventObject event) {
    return true;
  }

  /**
   * shouldSelectCell
   *
   * @param event EventObject
   * @return boolean
   */
  public boolean shouldSelectCell(EventObject event) {
    return true;
  }

  /**
   * getCellEditorValue
   *
   * @return Object
   */
  public Object getCellEditorValue() {
    return new Boolean(isSelected());
  }

  /**
   * getTableCellEditorComponent
   *
   * @param table JTable
   * @param value Object
   * @param isSelected boolean
   * @param row int
   * @param column int
   * @return Component
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    boolean isSelect = ( (Boolean) value).booleanValue();
    setSelected(isSelect);
    return this;
  }

}
