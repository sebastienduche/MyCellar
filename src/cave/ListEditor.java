package Cave;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 29/12/12
 */
public class ListEditor extends Component implements TableCellEditor {
  protected EventListenerList listenerList = new EventListenerList();
  protected ChangeEvent changeEvent = new ChangeEvent(this);
  protected LinkedList<Bouteille> bottle;
  protected AddVin adv;
  protected LinkedList<Bouteille> listSelected;
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
  public ListEditor(LinkedList<Bouteille> b) {

    super();
    bottle = b;
  }
 
  public void setAddVin(AddVin av){
	  adv = av;
  }

  /**
   * putList: Mise � jour de la liste.
   *
   * @param list LinkedList<Bouteille>: Liste des vins s�lectionn�e.
   */
  public void putList(LinkedList<Bouteille> list) {
    listSelected = list;
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
    return false;
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
    return new Boolean(true);
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
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    if (listSelected != null && adv != null) {
      adv.setBottlesInModification(listSelected);
    }
    return this;
  }

}
