package mycellar;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.3
 * @since 02/03/18
 */
public class TableValues extends AbstractTableModel {

	private static final long serialVersionUID = -3899189654755476591L;
	public static final int ETAT = 0;
	public static final int SHOW = 7;

	private static final int NBCOL = 8;
	private final String[] columnNames = {"", Program.getLabel("Infos106"), Program.getLabel("Infos189"), Program.getLabel("Infos217"),
			Program.getLabel("Infos082"), Program.getLabel("Infos028"), Program.getLabel("Infos083"), ""	};

	private final List<Boolean> listBoolean = new LinkedList<>();
	private final List<Bouteille> monVector = new LinkedList<>();

	/**
	 * getRowCount
	 *
	 * @return int
	 */
	@Override
	public int getRowCount() {
		return monVector.size();
	}

	/**
	 * getColumnCount
	 *
	 * @return int
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
	public boolean isCellEditable(int row, int column) {
		return (column == ETAT || column == SHOW);
	}

	/**
	 * setValueAt
	 *
	 * @param value Object
	 * @param row int
	 * @param column int
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {
		switch (column) {
		case SHOW:
			Bouteille bottle = monVector.get(row);
			Start.showBottle(bottle, true);
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

	public List<Bouteille> getDatas(){
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
