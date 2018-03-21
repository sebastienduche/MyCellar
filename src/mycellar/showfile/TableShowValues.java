package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.RangementUtils;
import mycellar.actions.OpenAddVinAction;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Society : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 3.5
 * @since 21/03/18
 */

public class TableShowValues extends AbstractTableModel {
  public static final int ETAT = 0;
  public static final int NAME = 1;
  public static final int YEAR = 2;
  public static final int TYPE = 3;
  public static final int PLACE = 4;
  private static final int NUM_PLACE = 5;
  private static final int LINE = 6;
  private static final int COLUMN = 7;
  protected static final int PRICE = 8;
  private static final int COMMENT = 9;
  private static final int MATURITY = 10;
  private static final int PARKER = 11;
  private static final int APPELLATION = 12;
  private static final int NBCOL = 13;
  private final String[] columnNames = {"", Program.getLabel("Infos106"), Program.getLabel("Infos189"), Program.getLabel("Infos134"), Program.getLabel("Infos217"),
      Program.getLabel("Infos082"), Program.getLabel("Infos028"), Program.getLabel("Infos083"), Program.getLabel("Infos135"), Program.getLabel("Infos137")
      , Program.getLabel("Infos391"), Program.getLabel("Infos392"), Program.getLabel("Infos393")};

  protected Boolean[] values = null;
  static final long serialVersionUID = 020406;

  List<Bouteille> monVector = new LinkedList<>();

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
   * @param row    int
   * @param column int
   * @return Object
   */
  @Override
  public Object getValueAt(int row, int column) {
    Bouteille b = monVector.get(row);
    switch (column) {
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
      case PRICE:
        return b.hasPrice() ? b.getPriceDouble() : "";
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
  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  /**
   * isCellEditable
   *
   * @param row    int
   * @param column int
   * @return boolean
   */
  @Override
  public boolean isCellEditable(int row, int column) {
    return ETAT == column;
  }

  /**
   * setValueAt
   *
   * @param value  Object
   * @param row    int
   * @param column int
   */
  @Override
  public void setValueAt(Object value, int row, int column) {

    Bouteille b = monVector.get(row);
    switch (column) {
      case ETAT:
        values[row] = (Boolean) value;
        break;
      case NAME:
        b.setNom((String) value);
        break;
      case PRICE:
        b.setPrix((String) value);
        break;
      case TYPE:
        b.setType((String) value);
        break;
      case MATURITY:
        b.setMaturity(Program.convertStringFromHTMLString((String) value));
        break;
      case PARKER:
        b.setParker(Program.convertStringFromHTMLString((String) value));
        break;
      case APPELLATION:
        b.setAppellation(Program.convertStringFromHTMLString((String) value));
        break;
      case COMMENT:
        b.setComment(Program.convertStringFromHTMLString((String) value));
        break;
      case YEAR:
        if (Program.hasYearControl() && !Bouteille.isValidYear((String) value)) {
          Erreur.showSimpleErreur(Program.getError("Error053"));
        } else {
          b.setAnnee((String) value);
        }
        break;
      case PLACE:
      case NUM_PLACE:
      case LINE:
      case COLUMN: {
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

        if (column == PLACE) {
          empl = (String) value;
          rangement = Program.getCave(empl);
        } else if (column == NUM_PLACE) {
          try {
            num_empl = Integer.parseInt((String) value);
            nValueToCheck = num_empl;
          } catch (Exception e) {
            Erreur.showSimpleErreur(Program.getError("Error196"));
            bError = true;
          }
        } else if (column == LINE) {
          try {
            line = Integer.parseInt((String) value);
            nValueToCheck = line;
          } catch (Exception e) {
            Erreur.showSimpleErreur(Program.getError("Error196"));
            bError = true;
          }
        } else {
          try {
            column1 = Integer.parseInt((String) value);
            nValueToCheck = column1;
          } catch (Exception e) {
            Erreur.showSimpleErreur(Program.getError("Error196"));
            bError = true;
          }
        }

        if (!bError && (column == NUM_PLACE || column == LINE || column == COLUMN)) {
          if (rangement != null && !rangement.isCaisse() && nValueToCheck <= 0) {
            Erreur.showSimpleErreur(Program.getError("Error197"));
            bError = true;
          }
        }

        if (!bError && (empl_old.compareTo(empl) != 0 || num_empl_old != num_empl || line_old != line || column_old != column1)) {
          // Controle de l'emplacement de la bouteille
          int tmpNumEmpl = num_empl;
          int tmpLine = line;
          int tmpCol = column1;
          if (rangement != null && !rangement.isCaisse()) {
            tmpNumEmpl--;
            tmpCol--;
            tmpLine--;
          }
          if (rangement != null && rangement.canAddBottle(tmpNumEmpl, tmpLine, tmpCol)) {
            Bouteille bTemp = null;
            if (!rangement.isCaisse()) {
              bTemp = rangement.getBouteille(num_empl - 1, line - 1, column1 - 1);
            }
            if (bTemp != null) {
              Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error059"), Program.convertStringFromHTMLString(bTemp.getNom()), bTemp.getAnnee()));
            } else {
              if (column == PLACE) {
                b.setEmplacement((String) value);
              } else if (column == NUM_PLACE) {
                b.setNumLieu(Integer.parseInt((String) value));
              } else if (column == LINE) {
                b.setLigne(Integer.parseInt((String) value));
              } else if (column == COLUMN) {
                b.setColonne(Integer.parseInt((String) value));
              }
              //values[row][column] = value;
              if (column == PLACE && rangement.isCaisse()) {
                int nNumEmpl = b.getNumLieu();//Integer.parseInt((String) values[row][NUM_PLACE]);
                if (nNumEmpl > rangement.getLastNumEmplacement()) {
                  b.setNumLieu(rangement.getFreeNumPlaceInCaisse());
                }
                b.setLigne(0);
                b.setColonne(0);
              }
              RangementUtils.putTabStock();
            }
          } else {
            if (rangement != null && rangement.isCaisse()) {
              Erreur.showSimpleErreur(Program.getError("Error154"));
            } else {
              if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, Program.getError("Error198"), Program.getError("Error015"), javax.swing.JOptionPane.YES_NO_OPTION)) {
                LinkedList<Bouteille> list = new LinkedList<>();
                list.add(b);
                new OpenAddVinAction(list).actionPerformed(null);
              }
            }
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

    if (b == null) {
      return;
    }
    values = new Boolean[b.size()];
    monVector = b;
    for (int i = 0; i < b.size(); i++) {
      values[i] = false;
    }
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
