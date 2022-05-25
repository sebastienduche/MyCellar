package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Program;
import mycellar.Start;
import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarObject;
import mycellar.core.text.LabelProperty;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static mycellar.MyCellarUtils.convertStringFromHTMLString;
import static mycellar.MyCellarUtils.parseIntOrError;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 5.8
 * @since 25/05/22
 */

class TableShowValues extends AbstractTableModel {

  public static final int ETAT = 0;
  public static final int YEAR = 2;
  static final int TYPE = 3;
  static final int PLACE = 4;
  static final int PRICE = 8;
  private static final long serialVersionUID = 1183158496820687240L;
  private static final int NAME = 1;
  private static final int NUM_PLACE = 5;
  private static final int LINE = 6;
  private static final int COLUMN = 7;
  private static final int COMMENT = 9;
  private static final int MATURITY = 10;
  private static final int PARKER = 11;
  private final String[] columnNames = {"", getLabel("Main.Item", LabelProperty.SINGLE.withCapital()), getLabel("Main.Year"), getLabel("Main.CapacityOrSupport"), getLabel("Main.Storage"),
      getLabel("MyCellarFields.NumPlace"), getLabel("MyCellarFields.Line"), getLabel("MyCellarFields.Column"), getLabel("Main.Price"), getLabel("Main.Comment"),
      getLabel("Main.Maturity"), getLabel("Main.Rating")};

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
        return convertStringFromHTMLString(nom);
      case YEAR:
        return b.getAnnee();
      case TYPE:
        return b.getKind();
      case PLACE:
        return convertStringFromHTMLString(b.getEmplacement());
      case NUM_PLACE:
        return Integer.toString(b.getNumLieu());
      case LINE:
        return Integer.toString(b.getLigne());
      case COLUMN:
        return Integer.toString(b.getColonne());
      case PRICE:
        return b.hasPrice() ? b.getPriceDouble() : "";
      case COMMENT:
        return convertStringFromHTMLString(b.getComment());
      case MATURITY:
        return convertStringFromHTMLString(b.getMaturity());
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
        b.setKind((String) value);
        break;
      case MATURITY:
        b.setMaturity(convertStringFromHTMLString((String) value));
        break;
      case PARKER:
        b.setParker(convertStringFromHTMLString((String) value));
        break;
      case COMMENT:
        b.setComment(convertStringFromHTMLString((String) value));
        break;
      case YEAR:
        if (Program.hasYearControl() && Bouteille.isInvalidYear((String) value)) {
          Erreur.showSimpleErreur(getError("Error.enterValidYear"));
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
          if (RangementUtils.isExistingPlace(empl)) {
            rangement = Program.getPlaceByName(empl);
          }
        } else if (column == NUM_PLACE) {
          Integer i = parseIntOrError(value);
          if (i == null) {
            bError = true;
          } else {
            num_empl = i;
            nValueToCheck = i;
          }
        } else if (column == LINE) {
          Integer i = parseIntOrError(value);
          if (i == null) {
            bError = true;
          } else {
            line = i;
            nValueToCheck = i;
          }
        } else {
          Integer i = parseIntOrError(value);
          if (i == null) {
            bError = true;
          } else {
            column1 = i;
            nValueToCheck = i;
          }
        }

        if (!bError && (column == NUM_PLACE || column == LINE || column == COLUMN)) {
          if (!rangement.isSimplePlace() && nValueToCheck <= 0) {
            Erreur.showSimpleErreur(getError("Error.enterNumericValueAboveZero"));
            bError = true;
          }
        }

        if (!bError && (b.getEmplacement().compareTo(empl) != 0 || b.getNumLieu() != num_empl || b.getLigne() != line || b.getColonne() != column1)) {
          // Controle de l'emplacement de la bouteille
          int tmpNumEmpl = num_empl;
          int tmpLine = line;
          int tmpCol = column1;
          if (!rangement.isSimplePlace()) {
            tmpNumEmpl--;
            tmpCol--;
            tmpLine--;
          } else {
            tmpNumEmpl -= rangement.getStartSimplePlace();
          }
          if (rangement.canAddObjectAt(tmpNumEmpl, tmpLine, tmpCol)) {
            Optional<MyCellarObject> bTemp = Optional.empty();
            if (!rangement.isSimplePlace()) {
              bTemp = rangement.getObject(num_empl - 1, line - 1, column1 - 1);
            }
            if (bTemp.isPresent()) {
              final IMyCellarObject bouteille = bTemp.get();
              Erreur.showSimpleErreur(MessageFormat.format(getError("Error.alreadyInStorage"), convertStringFromHTMLString(bouteille.getNom()), bouteille.getAnnee()));
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
              if (column == PLACE && rangement.isSimplePlace()) {
                int nNumEmpl = b.getNumLieu();
                if (nNumEmpl > rangement.getLastPartNumber()) {
                  b.setNumLieu(rangement.getFreeNumPlaceInSimplePlace());
                }
                b.setLigne(0);
                b.setColonne(0);
              }
              RangementUtils.putTabStock();
            }
          } else {
            if (rangement.isSimplePlace()) {
              Erreur.showSimpleErreur(getError("Error.NotEnoughSpaceStorage"));
            } else {
              if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), getError("Error.cantModifyStorage", LabelProperty.THE_SINGLE), getError("Error.error"), JOptionPane.YES_NO_OPTION)) {
                LinkedList<MyCellarObject> list = new LinkedList<>();
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

  public void setMyCellarObjects(List<? extends MyCellarObject> b) {
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

  public MyCellarObject getMyCellarObject(int i) {
    return monVector.get(i);
  }

}
