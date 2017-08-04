package mycellar.showfile;

import java.text.MessageFormat;
import java.util.LinkedList;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.core.MyCellarError;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Society : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 04/08/17
 */

public class ErrorShowValues extends TableShowValues {

	private static final long serialVersionUID = 2477822182069165515L;
	public final static int ETAT = 0;
	public final static int ERROR = 1;
	public final static int NAME = 2;
	public final static int YEAR = 3;
	public final static int TYPE = 4;
	public final static int PLACE = 5;
	public final static int NUM_PLACE = 6;
	public final static int LINE = 7;
	public final static int COLUMN = 8;
	public final static int STATUS = 9;
	public final static int BUTTON = 10;
	private final static int NBCOL = 11;
	private String[] columnNames = {"", Program.getLabel("ErrorShowValues.error"), Program.getLabel("Infos106"), Program.getLabel("Infos189"), Program.getLabel("Infos134"), Program.getLabel("Infos217"),
			Program.getLabel("Infos082"), Program.getLabel("Infos028"), Program.getLabel("Infos083"), Program.getLabel("ShowFile.Status"), "" };

	protected Boolean[] values = null;
	protected Boolean[] status = null;
	protected Boolean[] editable = null;

	protected int sortCol = 0;

	protected boolean isSortAsc = true;
	protected LinkedList<MyCellarError> monVector = new LinkedList<MyCellarError>();

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
		if(monVector.size() <= row)
			return null;
		MyCellarError error = monVector.get(row);
		Bouteille b = error.getBottle();
		switch(column)
		{
		case ETAT:
			return values[row];
		case NAME:
			String nom = b.getNom();
			return Program.convertStringFromHTMLString(nom);
		case YEAR:
			return b.getAnnee();
		case TYPE:
			return b.getType();
		case PLACE:
			return Program.convertStringFromHTMLString(b.getEmplacement());
		case NUM_PLACE:
			return Integer.toString(b.getNumLieu());
		case LINE:
			return Integer.toString(b.getLigne());
		case COLUMN:
			return Integer.toString(b.getColonne());
		case STATUS:
			if(error.isStatus())
				return Program.getLabel("ShowFile.Added");
			return status[row] ? Program.getLabel("Main.OK") : Program.getLabel("Main.KO");
		case BUTTON:
			return true;
		case ERROR:
			return Program.convertStringFromHTMLString(error.getErrorMessage());
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
		if(editable[row] == Boolean.FALSE)
			return false;
		if (column == ETAT 
				|| column == NAME 
				|| column == TYPE 
				|| column == YEAR 
				|| column == PRICE 
				|| column == PLACE
				|| column == NUM_PLACE
				|| column == LINE
				|| column == COLUMN
				|| column == BUTTON) {
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

		MyCellarError error = monVector.get(row);
		Bouteille b = error.getBottle();
		Rangement rangement = null;
		switch (column) {
		case ETAT:
			values[row] = (Boolean)value;
			break;
		case BUTTON:
			rangement = b.getRangement();
			if(rangement != null && rangement.canAddBottle(b)) {
				Program.getStorage().addWine(b);
				editable[row] = Boolean.FALSE;
				error.setStatus(true);
				fireTableRowsUpdated(row, row);
			} else {
				status[row] = Boolean.FALSE;
				new Erreur(Program.getError("ShowFile.errorAddingBottle"));
			}
			break;
		case NAME:
			b.setNom((String)value);
			break;
		case TYPE:
			b.setType((String)value);
			break;
		case YEAR:
			if( Program.hasYearControl() && !Bouteille.isValidYear( (String) value) )
				new Erreur(Program.getError("Error053"));
			else{
				Program.getStorage().removeAnnee(b.getAnneeInt());
				b.setAnnee((String)value);	
				Program.getStorage().addAnnee(b.getAnneeInt());
			}
			break;
		case PLACE:
		case NUM_PLACE:
		case LINE:
		case COLUMN:
		{
			String empl_old = b.getEmplacement();
			int num_empl_old = b.getNumLieu();
			int line_old = b.getLigne();
			int column_old = b.getColonne();
			rangement = Program.getCave(empl_old);
			boolean bError = false;
			int nValueToCheck = -1;
			String empl = empl_old;
			int num_empl = num_empl_old;
			int line = line_old;
			int column1 = column_old;

			if ( column == PLACE ) {
				empl = (String)value; 
				rangement = Program.getCave(empl);
			}
			else if ( column == NUM_PLACE ) {
				try{
					num_empl = Integer.parseInt((String)value);
					nValueToCheck = num_empl;
				}
				catch (Exception e) {
					new Erreur(Program.getError("Error196"));
					bError = true;
				}
			}
			else if ( column == LINE ) {
				try{
					line = Integer.parseInt((String)value);
					nValueToCheck = line;
				}
				catch (Exception e) {
					new Erreur(Program.getError("Error196"));
					bError = true;
				}
			}
			else if ( column == COLUMN ) {
				try{
					column1 = Integer.parseInt((String)value);
					nValueToCheck = column1;
				}
				catch (Exception e) {
					new Erreur(Program.getError("Error196"));
					bError = true;
				}
			}

			if ( !bError && (column == NUM_PLACE || column == LINE || column == COLUMN) ) {
				if (rangement != null && !rangement.isCaisse() && nValueToCheck <= 0)
				{
					new Erreur(Program.getError("Error197"));
					bError = true;
				}
			}

			if ( !bError && (empl_old.compareTo(empl) != 0 || num_empl_old != num_empl || line_old != line || column_old != column1)) {
				// Controle de l'emplacement de la bouteille
				int tmpNumEmpl = num_empl;
				int tmpLine = line;
				int tmpCol = column1;
				if(!rangement.isCaisse()) {
					tmpNumEmpl--;
					tmpCol--;
					tmpLine--;
				}
				if(rangement.canAddBottle(tmpNumEmpl, tmpLine, tmpCol))
				{
					Bouteille bTemp = null;
					if(!rangement.isCaisse())
						bTemp = rangement.getBouteille(num_empl-1, line-1, column1-1);
					if( bTemp != null) {
						status[row] = Boolean.FALSE;
						new Erreur(MessageFormat.format(Program.getError("Error059"), Program.convertStringFromHTMLString(bTemp.getNom()), b.getAnnee()));
					}
					else {
						if(column == PLACE)
							b.setEmplacement((String)value);
						else if(column == NUM_PLACE)
							b.setNumLieu(Integer.parseInt((String)value));
						else if(column == LINE)
							b.setLigne(Integer.parseInt((String)value));
						else if(column == COLUMN)
							b.setColonne(Integer.parseInt((String)value));
						//values[row][column] = value;
						if ( column == PLACE && rangement.isCaisse()) {
							int nNumEmpl = b.getNumLieu();//Integer.parseInt((String) values[row][NUM_PLACE]);
							if( nNumEmpl > rangement.getLastNumEmplacement())
								b.setNumLieu(rangement.getFreeNumPlaceInCaisse());
							b.setLigne(0);
							b.setColonne(0);
						}
						status[row] = Boolean.TRUE;
					}
				}
				else
					status[row] = Boolean.FALSE;
			}
			else
				status[row] = Boolean.FALSE;
			fireTableRowsUpdated(row, row);
		}
		break;
		}
	}

	/**
	 * setErrors: Ajout des erreurs.
	 *
	 * @param b LinkedList<MyCellarError>
	 */
	public void setErrors(LinkedList<MyCellarError> b) {

		if(b == null)
			return;
		values = new Boolean[b.size()];
		status = new Boolean[b.size()];
		editable = new Boolean[b.size()];
		monVector = b;
		for (int i = 0; i < b.size(); i++) {
			values[i] = Boolean.FALSE;
			status[i] = Boolean.FALSE;
			editable[i] = Boolean.TRUE;
		}
		fireTableDataChanged();
	}
	
	@Override
	public Bouteille getBottle(int i) {
		return monVector.get(i).getBottle();
	}

	/**
	 * getNbData
	 *
	 * @return int
	 */
	public int getNbData() {
		return monVector.size();
	}

}
