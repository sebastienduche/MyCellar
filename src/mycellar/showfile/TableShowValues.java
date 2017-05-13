package mycellar.showfile;

import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.Rangement;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Society : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.7
 * @since 13/05/17
 */

public class TableShowValues extends AbstractTableModel {
  public final static int ETAT = 0;
  public final static int NAME = 1;
  public final static int YEAR = 2;
  public final static int TYPE = 3;
  public final static int PLACE = 4;
  public final static int NUM_PLACE = 5;
  public final static int LINE = 6;
  public final static int COLUMN = 7;
  public final static int PRICE = 8;
  public final static int COMMENT = 9;
  public final static int MATURITY = 10;
  public final static int PARKER = 11;
  public final static int APPELLATION = 12;
  private final static int NBCOL = 13;
  private String[] columnNames = {"", Program.getLabel("Infos106"), Program.getLabel("Infos189"), Program.getLabel("Infos134"), Program.getLabel("Infos217"),
      Program.getLabel("Infos082"), Program.getLabel("Infos028"), Program.getLabel("Infos083"), Program.getLabel("Infos135"), Program.getLabel("Infos137")
      , Program.getLabel("Infos391") , Program.getLabel("Infos392"), Program.getLabel("Infos393") };

  protected Boolean[] values = null;
  static final long serialVersionUID = 020406;

  protected int sortCol = 0;

  protected boolean isSortAsc = true;
  protected LinkedList<Bouteille> monVector = new LinkedList<Bouteille>();

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
	  Bouteille b = monVector.get(row);
	  switch(column)
	  {
	  case 0:
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
	  case PRICE:
		  return Program.convertStringFromHTMLString(b.getPrix());
	  case COMMENT:
		  return Program.convertStringFromHTMLString(b.getComment());
	  case MATURITY:
		  return Program.convertStringFromHTMLString(b.getMaturity());
	  case PARKER:
		  return b.getParker();
	  case APPELLATION:
		  return b.getAppellation();
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
    if (column == ETAT 
     || column == NAME 
     || column == TYPE 
     || column == YEAR 
     || column == PRICE 
     || column == PLACE
     || column == NUM_PLACE
     || column == LINE
     || column == COLUMN
     || column == MATURITY
     || column == PARKER
     || column == APPELLATION
     || column == COMMENT) {
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

	  Bouteille b = monVector.get(row);
    switch (column) {
    case 0:
    	values[row] = (Boolean)value;
    	break;
    case NAME:
    	b.setNom((String)value);
    	break;
    case PRICE:
    	b.setPrix((String)value);
    	break;
    case TYPE:
    	b.setType((String)value);
    	break;
    case MATURITY:
    	b.setMaturity(Program.convertStringFromHTMLString((String)value));
    	break;
    case PARKER:
    	b.setParker(Program.convertStringFromHTMLString((String)value));
    	break;
    case APPELLATION:
    	b.setAppellation(Program.convertStringFromHTMLString((String)value));
    	break;
    case COMMENT:
    	b.setComment(Program.convertStringFromHTMLString((String)value));
    	break;
    case YEAR:
        if( Program.hasYearControl() && !Bouteille.isValidYear( (String) value) )
       	  javax.swing.JOptionPane.showMessageDialog(null, Program.getError("Error053"), Program.getError("Error015"), javax.swing.JOptionPane.ERROR_MESSAGE);
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
    	Rangement rangement = Program.getCave(empl_old);
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
              javax.swing.JOptionPane.showMessageDialog(null, Program.getError("Error196"), Program.getError("Error015"), javax.swing.JOptionPane.ERROR_MESSAGE);
              bError = true;
            }
    	}
    	else if ( column == LINE ) {
    		try{
    			line = Integer.parseInt((String)value);
    			nValueToCheck = line;
    		}
            catch (Exception e) {
              javax.swing.JOptionPane.showMessageDialog(null, Program.getError("Error196"), Program.getError("Error015"), javax.swing.JOptionPane.ERROR_MESSAGE);
              bError = true;
            }
    	}
    	else if ( column == COLUMN ) {
    		try{
    			column1 = Integer.parseInt((String)value);
    			nValueToCheck = column1;
    		}
            catch (Exception e) {
              javax.swing.JOptionPane.showMessageDialog(null, Program.getError("Error196"), Program.getError("Error015"), javax.swing.JOptionPane.ERROR_MESSAGE);
              bError = true;
            }
    	}
    	
    	if ( !bError && (column == NUM_PLACE || column == LINE || column == COLUMN) ) {
    		if (rangement != null && !rangement.isCaisse() && nValueToCheck <= 0)
    		{
    			javax.swing.JOptionPane.showMessageDialog(null, Program.getError("Error197"), Program.getError("Error015"), javax.swing.JOptionPane.ERROR_MESSAGE);
                bError = true;
    		}
    	}
    		
    	if ( !bError && (empl_old.compareTo(empl) != 0 || num_empl_old != num_empl || line_old != line || column_old != column1)) {
    		// Controle de l'emplacement de la bouteille
    		if(rangement.canAddBottle(num_empl, line, column1))
    		{
		    	Bouteille bTemp = null;
		    	if(!rangement.isCaisse())
		    		bTemp = rangement.getBouteille(num_empl-1, line-1, column1-1);
		    	if( bTemp != null) {
		    		String sText = Program.convertStringFromHTMLString(bTemp.getNom()) + " " + Program.getError("Error059");
		    		javax.swing.JOptionPane.showMessageDialog(null, sText, Program.getError("Error015"), javax.swing.JOptionPane.ERROR_MESSAGE);
		    	}
		    	else {
		    		Rangement oldPlace = b.getRangement();
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
		    		if(oldPlace != null)
		    			oldPlace.putTabStock();
		    		b.getRangement().putTabStock();
		    	}
    		}
    		else {
    			String sText = Program.getError("Error198");
    			sText = sText.replaceFirst("A1", Integer.toString(num_empl));
    			sText = sText.replaceFirst("A2", Integer.toString(line));
    			sText = sText.replaceFirst("A3", Integer.toString(column1));
    			sText = sText.replaceFirst("A4", rangement.getNom());
    			if (rangement.isCaisse())
    				sText = Program.getError("Error154");
    			javax.swing.JOptionPane.showMessageDialog(null, sText, Program.getError("Error015"), javax.swing.JOptionPane.ERROR_MESSAGE);
    		}
    		break;
    	}
      }
      break;
    }
  }

  /**
   * setBottles: Ajout des bouteilles.
   *
   * @param b LinkedList<Bouteille>
   */
  public void setBottles(LinkedList<Bouteille> b) {

	  if(b == null)
		  return;
      values = new Boolean[b.size()];
      monVector = b;
      for (int i = 0; i < b.size(); i++)
    	  values[i] = new Boolean(false);
      fireTableDataChanged();
  }

  /**
   * getBouteille: Récupération d'une bouteille.
   */
  public Bouteille getBottle(int i) {
   return monVector.get(i);
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
