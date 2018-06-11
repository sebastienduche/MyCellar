package mycellar;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 02/03/18
 */
class ListEditor extends Component implements TableCellEditor {
  private final EventListenerList listenerList = new EventListenerList();
  private final ChangeEvent changeEvent = new ChangeEvent(this);
  protected List<Bouteille> bottle;
  private AddVin adv;
  private LinkedList<Bouteille> listSelected;
  static final long serialVersionUID = 301004;

  /**
   * ListEditor: Constructeur.
   */
  @SuppressWarnings("unused")
private ListEditor() {
    super();
  }

  /**
   * ListEditor: Constructeur
   *
   * @param b LinkedList<Bouteille>: Liste de bouteilles.
   */
  ListEditor(List<Bouteille> b) {
    super();
    bottle = b;
  }
 
  void setAddVin(AddVin av){
	  adv = av;
  }

  /**
   * putList: Mise � jour de la liste.
   *
   * @param list LinkedList<Bouteille>: Liste des vins s�lectionn�e.
   */
  void putList(LinkedList<Bouteille> list) {
    listSelected = list;
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
    return false;
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
    return true;
  }

  /**
   * getTableCellEditorComponent: Mise � jour de la liste des vins et de l'objet Ajout de vins.
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
    if (listSelected != null && adv != null) {
      adv.setBottlesInModification(listSelected);
    }
    return this;
  }

}
