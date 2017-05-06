package mycellar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Collections;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.1
 * @since 16/01/17
 */
public class TableValues extends AbstractTableModel {

	private static final long serialVersionUID = -3899189654755476591L;
	public final static int ETAT = 0;
	public final static int SHOW = 7;

	public final static int NBCOL = 8;
	private String[] columnNames = {"", Program.getLabel("Infos106"), Program.getLabel("Infos189"), Program.getLabel("Infos217"),
			Program.getLabel("Infos082"), Program.getLabel("Infos028"), Program.getLabel("Infos083"), ""
	};

	private LinkedList<Boolean> listBoolean = new LinkedList<Boolean>();

	protected int sortCol = 0;

	protected boolean isSortAsc = true;
	private LinkedList<Bouteille> monVector = new LinkedList<Bouteille>();

	/**
	 * getRowCount
	 *
	 * @return int
	 */
	public int getRowCount() {
		return monVector.size();
	}

	/**
	 * getColumnCount
	 *
	 * @return int
	 */
	public int getColumnCount() {
		return NBCOL;
	}

	/**
	 * getValueAt
	 *
	 * @param row int
	 * @param column int
	 * @return Object
	 */
	public Object getValueAt(int row, int column) {
		if(row >= monVector.size()) {
			Program.Debug("TableValues: Error index " + row + " > " + monVector.size());
			return "";
		}
		if(row >= listBoolean.size()) {
			Program.Debug("TableValues: Error listBoolean index " + row + " > " + monVector.size());
			return "";
		}
		Bouteille b = monVector.get(row);
		switch(column)
		{
		case 0:
			return listBoolean.get(row);
		case 1:
			String nom = b.getNom();
			return Program.convertStringFromHTMLString(nom);
		case 2:
			return b.getAnnee();
		case 3:
			return b.getEmplacement();
		case 4:
			return Integer.toString(b.getNumLieu());
		case 5:
			return Integer.toString(b.getLigne());
		case 6:
			return Integer.toString(b.getColonne());
		case 7:
			return Boolean.FALSE;
		}
		return "";
	}

	/**
	 * getColumnName
	 *
	 * @param column int
	 * @return String
	 */
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/**
	 * isCellEditable
	 *
	 * @param row int
	 * @param column int
	 * @return boolean
	 */
	public boolean isCellEditable(int row, int column) {
		if (column == ETAT || column == SHOW) {
			return true;
		}
		return false;
	}

	/**
	 * setValueAt
	 *
	 * @param value Object
	 * @param row int
	 * @param column int
	 */
	public void setValueAt(Object value, int row, int column) {
		switch (column) {
		case SHOW:
			Bouteille bottle = monVector.get(row);
			Start.showBottle(bottle);
			break;
		case ETAT:
			listBoolean.set(row, (Boolean)value);
			break;
		}
	}

	/**
	 * addBouteille: Ajout d'une bouteille.
	 *
	 * @param b Bouteille
	 */
	public void addBouteille(Bouteille b) {
		if( b != null) {
			monVector.add(b);
			listBoolean.add(Boolean.FALSE);
			fireTableDataChanged();
		}
	}

	/**
	 * removeAll: Vidage de la liste.
	 */
	public void removeAll() {
		monVector.clear();
		listBoolean.clear();
		fireTableDataChanged();
	}

	/**
	 * removeBouteille: Suppression d'une bouteille.
	 *
	 * @param num Bouteille
	 */
	public void removeBouteille(Bouteille num) {
		int index = monVector.indexOf(num);
		monVector.remove(num);
		listBoolean.remove(index);
		fireTableDataChanged();
	}

	/**
	 * removeBouteille: Suppression d'une bouteille.
	 *
	 * @param num int
	 */
	public void removeBouteille(int num) {
		monVector.remove(num);
		listBoolean.remove(num);
		fireTableDataChanged();
	}

	class ColumnListener extends MouseAdapter {
		protected JTable table;

		public ColumnListener(JTable t) {
			table = t;
		}

		public void mouseClicked(MouseEvent e) {
			TableColumnModel colModel = table.getColumnModel();
			int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
			int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();
			if (modelIndex < 0) {
				return;
			}
			if (sortCol == modelIndex) {
				isSortAsc = !isSortAsc;
			}
			else {
				sortCol = modelIndex;
			}
			Collections.sort(monVector, (bottle1, bottle2) -> { int resul = bottle1.getNom().compareTo(bottle2.getNom());
				if(isSortAsc)
					return resul;
				return -resul;});
			table.tableChanged(new TableModelEvent(TableValues.this));
			table.repaint();
		}
	}

	public LinkedList<Bouteille> getDatas(){
		return monVector;
	}
	
	public boolean hasBottle(Bouteille b){
		return monVector.contains(b);
	}


	public Bouteille getBouteille(int i){
		return monVector.get(i);
	}

	public void deleteBottle(Bouteille bottleToDelete) {
		if(bottleToDelete == null)
			return;
		if(monVector.contains(bottleToDelete))
			monVector.remove(bottleToDelete);
	}

}
