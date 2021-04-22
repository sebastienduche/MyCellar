package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Program;
import mycellar.Start;
import mycellar.core.IMyCellarObject;
import mycellar.core.LabelProperty;
import mycellar.core.MyCellarObject;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Society : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 5.0
 * @since 22/04/21
 */

class TableShowValues extends AbstractTableModel {

  private static final long serialVersionUID = 1183158496820687240L;
  public static final int ETAT = 0;
  private static final int NAME = 1;
  public static final int YEAR = 2;
  static final int TYPE = 3;
  static final int PLACE = 4;
  private static final int NUM_PLACE = 5;
  private static final int LINE = 6;
  private static final int COLUMN = 7;
  static final int PRICE = 8;
  private static final int COMMENT = 9;
  private static final int MATURITY = 10;
  private static final int PARKER = 11;
  private final String[] columnNames = {"", Program.getLabel("Main.Item", LabelProperty.SINGLE.withCapital()), Program.getLabel("Infos189"), Program.getLabel("Infos134"), Program.getLabel("Infos217"),
      Program.getLabel("Infos082"), Program.getLabel("Infos028"), Program.getLabel("Infos083"), Program.getLabel("Infos135"), Program.getLabel("Infos137"),
      Program.getLabel("Infos391"), Program.getLabel("Infos392")};

  protected Boolean[] values = null;

  List<? extends MyCellarObject> monVector = new LinkedList<>();

  @Override
  public int getRowCount() {
    return monVector.size();
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public Object getValueAt(int row, int column) {
    Program.throwNotImplementedIfNotFor(monVector.get(row), Bouteille.class);
    Bouteille b = (Bouteille) monVector.get(row);
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
      default:
        return "";
    }
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return ETAT == column;
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    Program.throwNotImplementedIfNotFor(monVector.get(row), Bouteille.class);
    Bouteille b = (Bouteille) monVector.get(row);
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
      case COMMENT:
        b.setComment(Program.convertStringFromHTMLString((String) value));
        break;
      case YEAR:
        if (Program.hasYearControl() && Bouteille.isInvalidYear((String) value)) {
          Erreur.showSimpleErreur(Program.getError("Error053"));
        } else {
          b.setAnnee((String) value);
        }
        break;
      case PLACE:
      case NUM_PLACE:
      case LINE:
      case COLUMN: {
        Rangement rangement = b.getRangement();
        boolean bError = false;
        int nValueToCheck = -1;
        int num_empl = b.getNumLieu();
        int line = b.getLigne();
        int column1 = b.getColonne();
        String empl = b.getEmplacement();
        if (column == PLACE) {
          empl = (String) value;
          if (Program.isExistingPlace(empl)) {
            rangement = Program.getCave(empl);
          }
        } else if (column == NUM_PLACE) {
          try {
            num_empl = Integer.parseInt((String) value);
            nValueToCheck = num_empl;
          } catch (NumberFormatException e) {
            Erreur.showSimpleErreur(Program.getError("Error196"));
            bError = true;
          }
        } else if (column == LINE) {
          try {
            line = Integer.parseInt((String) value);
            nValueToCheck = line;
          } catch (NumberFormatException e) {
            Erreur.showSimpleErreur(Program.getError("Error196"));
            bError = true;
          }
        } else {
          try {
            column1 = Integer.parseInt((String) value);
            nValueToCheck = column1;
          } catch (NumberFormatException e) {
            Erreur.showSimpleErreur(Program.getError("Error196"));
            bError = true;
          }
        }

        if (!bError && (column == NUM_PLACE || column == LINE || column == COLUMN)) {
          if (!rangement.isCaisse() && nValueToCheck <= 0) {
            Erreur.showSimpleErreur(Program.getError("Error197"));
            bError = true;
          }
        }

        if (!bError && (b.getEmplacement().compareTo(empl) != 0 || b.getNumLieu() != num_empl || b.getLigne() != line || b.getColonne() != column1)) {
          // Controle de l'emplacement de la bouteille
          int tmpNumEmpl = num_empl;
          int tmpLine = line;
          int tmpCol = column1;
          if (!rangement.isCaisse()) {
            tmpNumEmpl--;
            tmpCol--;
            tmpLine--;
          } else {
            tmpNumEmpl -= rangement.getStartCaisse();
          }
          if (rangement.canAddBottle(tmpNumEmpl, tmpLine, tmpCol)) {
            Optional<MyCellarObject> bTemp = Optional.empty();
            if (!rangement.isCaisse()) {
              bTemp = rangement.getBouteille(num_empl - 1, line - 1, column1 - 1);
            }
            if (bTemp.isPresent()) {
              final IMyCellarObject bouteille = bTemp.get();
              Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error059"), Program.convertStringFromHTMLString(bouteille.getNom()), bouteille.getAnnee()));
            } else {
              if (column == PLACE) {
                b.setEmplacement((String) value);
              } else if (column == NUM_PLACE) {
                b.setNumLieu(Integer.parseInt((String) value));
              } else if (column == LINE) {
                b.setLigne(Integer.parseInt((String) value));
              } else {
                b.setColonne(Integer.parseInt((String) value));
              }
              if (column == PLACE && rangement.isCaisse()) {
                int nNumEmpl = b.getNumLieu();
                if (nNumEmpl > rangement.getLastNumEmplacement()) {
                  b.setNumLieu(rangement.getFreeNumPlaceInCaisse());
                }
                b.setLigne(0);
                b.setColonne(0);
              }
              RangementUtils.putTabStock();
            }
          } else {
            if (rangement.isCaisse()) {
              Erreur.showSimpleErreur(Program.getError("Error154"));
            } else {
              if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), Program.getError("Error198", LabelProperty.THE_SINGLE), Program.getError("Error015"), JOptionPane.YES_NO_OPTION)) {
                LinkedList<Bouteille> list = new LinkedList<>();
                list.add(b);
                Program.modifyBottles(list);
              }
            }
          }
          break;
        }
      }
      break;
    }
  }

  public void setBottles(List<? extends MyCellarObject> b) {
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
  public MyCellarObject getMyCellarObject(int i) {
    return monVector.get(i);
  }

}
