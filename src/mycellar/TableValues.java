package mycellar;

import mycellar.core.MyCellarObject;
import mycellar.core.LabelProperty;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

import static mycellar.Program.getLabel;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.2
 * @since 16/04/21
 */
class TableValues extends AbstractTableModel {

	private static final long serialVersionUID = -3899189654755476591L;
	public static final int ETAT = 0;
	static final int SHOW = 7;

	private final List<String> columnNames = List.of("",
			getLabel("Main.Item", LabelProperty.SINGLE.withCapital()), getLabel("Infos189"), getLabel("Infos217"),
			getLabel("Infos082"), getLabel("Infos028"), getLabel("Infos083"), "");

	private final List<Boolean> listBoolean = new LinkedList<>();
	private final List<MyCellarObject> datas = new LinkedList<>();

	/**
	 * getRowCount
	 *
	 * @return int
	 */
	@Override
	public int getRowCount() {
		if (datas == null) {
			return 0;
		}
		return datas.size();
	}

	/**
	 * getColumnCount
	 *
	 * @return int
	 */
	@Override
	public int getColumnCount() {
		return columnNames.size();
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
		if(row >= datas.size()) {
			Program.Debug("TableValues: Error index " + row + " > " + datas.size());
			return "";
		}
		if(row >= listBoolean.size()) {
			Program.Debug("TableValues: Error listBoolean index " + row + " > " + datas.size());
			return "";
		}
		final MyCellarObject myCellarObject = datas.get(row);
		switch(column)
		{
			case ETAT:
				return listBoolean.get(row);
			case 1:
				String nom = myCellarObject.getNom();
				return Program.convertStringFromHTMLString(nom);
			case 2:
				return myCellarObject.getAnnee();
			case 3:
				if (myCellarObject.isInTemporaryStock()) {
					return getLabel("Bouteille.TemporaryPlace");
				}
				return myCellarObject.getEmplacement();
			case 4:
				return Integer.toString(myCellarObject.getNumLieu());
			case 5:
				return Integer.toString(myCellarObject.getLigne());
			case 6:
				return Integer.toString(myCellarObject.getColonne());
			case SHOW:
				return Boolean.FALSE;
			default:
				return "";
		}
	}

	/**
	 * getColumnName
	 *
	 * @param column int
	 * @return String
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
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
				MyCellarObject bottle = datas.get(row);
				Program.showBottle(bottle, true);
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
	void addBouteille(MyCellarObject b) {
		if(b != null) {
			datas.add(b);
			listBoolean.add(Boolean.FALSE);
			fireTableDataChanged();
		}
	}

	/**
	 * removeAll: Vidage de la liste.
	 */
	public void removeAll() {
		datas.clear();
		listBoolean.clear();
		fireTableDataChanged();
	}

	/**
	 * removeBouteille: Suppression d'une bouteille.
	 *
	 * @param bouteille MyCellarObject
	 */
	void removeBouteille(MyCellarObject bouteille) {
		int index = datas.indexOf(bouteille);
		if(index != -1) {
			datas.remove(bouteille);
			listBoolean.remove(index);
			fireTableDataChanged();
		}
	}

	public List<MyCellarObject> getDatas() {
		return datas;
	}
	
	boolean hasNotBottle(MyCellarObject b) {
		return !datas.contains(b);
	}

	public Bouteille getBouteille(int i) {
		return (Bouteille) datas.get(i);
	}
}
