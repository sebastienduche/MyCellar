package mycellar;

import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.9
 * @since 12/07/17
 */
public class TableauValues extends AbstractTableModel {
	public final static int ETAT = 0;
	static final long serialVersionUID = 220605;
	private String[] columnNames = {"", Program.getLabel("Infos105"), Program.getLabel("Infos027"), Program.getLabel("Infos136")};

	private LinkedList<Rangement> list = new LinkedList<Rangement>();
	private LinkedList<Boolean> listBoolean = new LinkedList<Boolean>();

	/**
	 * getRowCount
	 *
	 * @return int
	 */
	public int getRowCount() {
		return list.size();
	}

	/**
	 * getColumnCount
	 *
	 * @return int
	 */
	public int getColumnCount() {
		return 4;
	}

	/**
	 * getValueAt
	 *
	 * @param row int
	 * @param column int
	 * @return Object
	 */
	public Object getValueAt(int row, int column) {
		Rangement r = list.get(row);
		switch(column) {
		case 0:
			return listBoolean.get(row);
		case 1:
			return r.getNom();
		case 2:
			if (r.isCaisse())
				return Program.getLabel("Infos058");
			int nombre_ligne = 0;
			for (int k = 0; k < r.getNbEmplacements(); k++) {
				nombre_ligne += r.getNbLignes(k);
			}
			if (nombre_ligne <= 1) 
				return new String(Integer.toString(nombre_ligne)) + " " + Program.getLabel("Infos060");
			return new String(Integer.toString(nombre_ligne)) + " " + Program.getLabel("Infos061");
		case 3:
			int nombre_vin = 0;
			if (r.isCaisse()) {
				nombre_vin = r.getNbCaseUseAll();
			}
			else {
				for (int k = 0; k < r.getNbEmplacements(); k++) {
					nombre_vin += r.getNbCaseUse(k);
				}
			}
				
			if (nombre_vin <= 1)
				return new String(Integer.toString(nombre_vin)) + " " + Program.getLabel("Infos063");
			return new String(Integer.toString(nombre_vin)) + " " + Program.getLabel("Infos064");
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
		return (column == ETAT);
	}

	/**
	 * setValueAt
	 *
	 * @param value Object
	 * @param row int
	 * @param column int
	 */
	public void setValueAt(Object value, int row, int column) {
		try {
			listBoolean.set(row, (Boolean)value);
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * addRangement: Fonction pour l'ajout d'un rangement.
	 *
	 * @param r Rangement
	 */
	public void addRangement(Rangement r) {
		list.add(r);
		listBoolean.add(Boolean.FALSE);
	}

	/**
	 * removeAll: Supprime toute les bouteilles.
	 */
	public void removeAll() {
		list.clear();
		listBoolean.clear();
	}
}
