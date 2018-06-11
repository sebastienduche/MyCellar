package mycellar;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.util.EventObject;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 02/03/18
 */
public class StateEditor extends JCheckBox implements TableCellEditor {
  private final EventListenerList listenerList = new EventListenerList();
  private ChangeEvent changeEvent = new ChangeEvent(this);
  static final long serialVersionUID = 301005;

  /**
   * StateEditor: Constructeur.
   */
  public StateEditor() {
    super();
    addActionListener((e) -> fireEditingStopped());
  }

  /**
   * addCellEditorListener
   *
   * @param listener CellEditorListener
   */
  @Override
  public void addCellEditorListener(CellEditorListener listener) {
    listenerList.add(CellEditorListener.class, listener);
  }

  /**
   * removeCellEditorListener
   *
   * @param listener CellEditorListener
   */
  @Override
  public void removeCellEditorListener(CellEditorListener listener) {
    listenerList.remove(CellEditorListener.class, listener);
  }

  /**
   * fireEditingStopped
   */
  private void fireEditingStopped() {
    CellEditorListener listener;
    Object[] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i++) {
      if (listeners[i].equals(CellEditorListener.class)) {
        listener = (CellEditorListener) listeners[i + 1];
        listener.editingStopped(changeEvent);
      }
    }
  }

  /**
   * fireEditingCanceled
   */
  private void fireEditingCanceled() {
    CellEditorListener listener;
    Object[] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i++) {
      if (listeners[i].equals(CellEditorListener.class)) {
        listener = (CellEditorListener) listeners[i + 1];
        listener.editingCanceled(changeEvent);
      }
    }
  }

  /**
   * cancelCellEditing
   */
  @Override
  public void cancelCellEditing() {
    fireEditingCanceled();
  }

  /**
   * stopCellEditing
   *
   * @return boolean
   */
  @Override
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
  @Override
  public boolean isCellEditable(EventObject event) {
    return true;
  }

  /**
   * shouldSelectCell
   *
   * @param event EventObject
   * @return boolean
   */
  @Override
  public boolean shouldSelectCell(EventObject event) {
    return true;
  }

  /**
   * getCellEditorValue
   *
   * @return Object
   */
  @Override
  public Object getCellEditorValue() {
    return isSelected();
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
  @Override
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    boolean isSelect = ( (Boolean) value);
    setSelected(isSelect);
    return this;
  }
}
