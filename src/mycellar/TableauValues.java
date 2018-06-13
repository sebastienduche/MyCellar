package mycellar;

import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.1
 * @since 08/03/18
 */
class TableauValues extends AbstractTableModel {
	public static final int ETAT = 0;
	static final long serialVersionUID = 220605;
	private final String[] columnNames = {"", Program.getLabel("Infos105"), Program.getLabel("Infos027"), Program.getLabel("Infos136")};

	private final List<Rangement> list = new LinkedList<>();
	private final List<Boolean> listBoolean = new LinkedList<>();

	/**
	 * getRowCount
	 *
	 * @return int
	 */
	@Override
	public int getRowCount() {
		return list.size();
	}

	/**
	 * getColumnCount
	 *
	 * @return int
	 */
	@Override
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
	@Override
	public Object getValueAt(int row, int column) {
		Rangement r = list.get(row);
		switch(column) {
		case 0:
			return listBoolean.get(row);
		case 1:
			return r.getNom();
		case 2:
			if (r.isCaisse())
				return Program.getLabel("Infos024");
			int nombre_ligne = 0;
			for (int k = 0; k < r.getNbEmplacements(); k++) {
				nombre_ligne += r.getNbLignes(k);
			}
			if (nombre_ligne <= 1) {
				return MessageFormat.format(Program.getLabel("Infos060"), nombre_ligne);
			}
			return MessageFormat.format(Program.getLabel("Infos061"), nombre_ligne);
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
				
			if (nombre_vin <= 1) {
				return MessageFormat.format(Program.getLabel("Infos063"), nombre_vin);
			}
			return MessageFormat.format(Program.getLabel("Infos064"), nombre_vin);
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
		return (column == ETAT);
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
