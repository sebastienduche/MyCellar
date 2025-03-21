package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Program;
import mycellar.core.IMyCellarObject;
import mycellar.frame.MainFrame;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.placesmanagement.places.SimplePlace;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

import static java.util.List.of;
import static mycellar.MyCellarUtils.convertStringFromHTMLString;
import static mycellar.MyCellarUtils.parseIntOrError;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_ALREADYINSTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_CANTMODIFYSTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_ENTERNUMERICVALUEABOVEZERO;
import static mycellar.general.ResourceErrorKey.ERROR_ENTERVALIDYEAR;
import static mycellar.general.ResourceErrorKey.ERROR_ERROR;
import static mycellar.general.ResourceErrorKey.ERROR_NOTENOUGHSPACESTORAGE;
import static mycellar.general.ResourceKey.MAIN_CAPACITYORSUPPORT;
import static mycellar.general.ResourceKey.MAIN_COMMENT;
import static mycellar.general.ResourceKey.MAIN_ITEM;
import static mycellar.general.ResourceKey.MAIN_MATURITY;
import static mycellar.general.ResourceKey.MAIN_PRICE;
import static mycellar.general.ResourceKey.MAIN_RATING;
import static mycellar.general.ResourceKey.MAIN_STORAGE;
import static mycellar.general.ResourceKey.MAIN_YEAR;
import static mycellar.general.ResourceKey.MYCELLARFIELDS_COLUMN;
import static mycellar.general.ResourceKey.MYCELLARFIELDS_LINE;
import static mycellar.general.ResourceKey.MYCELLARFIELDS_NUMPLACE;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 6.7
 * @since 21/03/25
 */

class TableShowValues extends AbstractTableModel {

  public static final int ETAT = 0;
  public static final int YEAR = 2;
  static final int TYPE = 3;
  static final int PLACE = 4;
  static final int PRICE = 8;
  private static final int NAME = 1;
  private static final int NUM_PLACE = 5;
  private static final int LINE = 6;
  private static final int COLUMN = 7;
  private static final int COMMENT = 9;
  private static final int MATURITY = 10;
  private static final int PARKER = 11;
  private final String[] columnNames = {"",
      getLabel(MAIN_ITEM),
      getLabel(MAIN_YEAR), getLabel(MAIN_CAPACITYORSUPPORT),
      getLabel(MAIN_STORAGE),
      getLabel(MYCELLARFIELDS_NUMPLACE),
      getLabel(MYCELLARFIELDS_LINE),
      getLabel(MYCELLARFIELDS_COLUMN),
      getLabel(MAIN_PRICE),
      getLabel(MAIN_COMMENT),

      getLabel(MAIN_MATURITY),
      getLabel(MAIN_RATING)};

  protected Boolean[] values = null;

  List<? extends IMyCellarObject> myCellarObjects = new LinkedList<>();

  @Override
  public int getRowCount() {
    return myCellarObjects.size();
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public Object getValueAt(int row, int column) {
    Program.throwNotImplementedIfNotFor(myCellarObjects.get(row), Bouteille.class);
    Bouteille b = (Bouteille) myCellarObjects.get(row);
    return switch (column) {
      case ETAT -> values[row];
      case NAME -> convertStringFromHTMLString(b.getNom());
      case YEAR -> b.getAnnee();
      case TYPE -> b.getKind();
      case PLACE -> convertStringFromHTMLString(b.getEmplacement());
      case NUM_PLACE -> Integer.toString(b.getNumLieu());
      case LINE -> Integer.toString(b.getLigne());
      case COLUMN -> Integer.toString(b.getColonne());
      case PRICE -> b.hasPrice() ? b.getPriceDouble() : "";
      case COMMENT -> convertStringFromHTMLString(b.getComment());
      case MATURITY -> convertStringFromHTMLString(b.getMaturity());
      case PARKER -> b.getParker();
      default -> "";
    };
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
    Program.throwNotImplementedIfNotFor(myCellarObjects.get(row), Bouteille.class);
    Bouteille b = (Bouteille) myCellarObjects.get(row);
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
          Erreur.showSimpleErreur(getError(ERROR_ENTERVALIDYEAR));
        } else {
          b.setAnnee((String) value);
        }
        break;
      case PLACE:
      case NUM_PLACE:
      case LINE:
      case COLUMN: {
        AbstractPlace rangement = b.getAbstractPlace();
        boolean bError = false;
        int nValueToCheck = -1;
        int num_empl = b.getNumLieu();
        int line = b.getLigne();
        int column1 = b.getColonne();
        String empl = b.getEmplacement();
        if (column == PLACE) {
          empl = (String) value;
          if (PlaceUtils.isExistingPlace(empl)) {
            rangement = PlaceUtils.getPlaceByName(empl);
          }
        } else if (column == NUM_PLACE) {
          Integer i = parseIntOrError(String.valueOf(value));
          if (i == null) {
            bError = true;
          } else {
            num_empl = i;
            nValueToCheck = i;
          }
        } else if (column == LINE) {
          Integer i = parseIntOrError(String.valueOf(value));
          if (i == null) {
            bError = true;
          } else {
            line = i;
            nValueToCheck = i;
          }
        } else {
          Integer i = parseIntOrError(String.valueOf(value));
          if (i == null) {
            bError = true;
          } else {
            column1 = i;
            nValueToCheck = i;
          }
        }

        if (!bError && (column == NUM_PLACE || column == LINE || column == COLUMN)) {
          if (!rangement.isSimplePlace() && nValueToCheck <= 0) {
            Erreur.showSimpleErreur(getError(ERROR_ENTERNUMERICVALUEABOVEZERO));
            bError = true;
          }
        }

        if (!bError && (b.getEmplacement().compareTo(empl) != 0 || b.getNumLieu() != num_empl || b.getLigne() != line || b.getColonne() != column1)) {
          // Controle de l'emplacement de la bouteille
          if (rangement.canAddObjectAt(new PlacePosition.PlacePositionBuilderZeroBased(rangement)
              .withNumPlace(num_empl)
              .withLine(line)
              .withColumn(column1).build())) {
            boolean isPresent = false;
            if (rangement.isComplexPlace()) {
              final IMyCellarObject bouteille = ((ComplexPlace) rangement).getObject(new PlacePosition.PlacePositionBuilderZeroBased(rangement)
                  .withNumPlace(num_empl)
                  .withLine(line)
                  .withColumn(column1)
                  .build()).orElse(null);
              if (bouteille != null) {
                isPresent = true;
                Erreur.showSimpleErreur(getError(ERROR_ALREADYINSTORAGE, convertStringFromHTMLString(bouteille.getNom()), bouteille.getAnnee()));
              }
            }
            if (!isPresent) {
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
                  b.setNumLieu(((SimplePlace) rangement).getFreeNumPlace());
                }
                b.setLigne(0);
                b.setColonne(0);
              }
              PlaceUtils.putTabStock();
            }
          } else {
            if (rangement.isSimplePlace()) {
              Erreur.showSimpleErreur(getError(ERROR_NOTENOUGHSPACESTORAGE));
            } else {
              if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), getError(ERROR_CANTMODIFYSTORAGE), getError(ERROR_ERROR), JOptionPane.YES_NO_OPTION)) {
                Program.modifyBottles(of(b));
              }
            }
          }
          break;
        }
      }
      break;
    }
  }

  public void setMyCellarObjects(List<? extends IMyCellarObject> list) {
    if (list == null) {
      return;
    }
    values = new Boolean[list.size()];
    myCellarObjects = list;
    for (int i = 0; i < list.size(); i++) {
      values[i] = false;
    }
    fireTableDataChanged();
  }

  public IMyCellarObject getMyCellarObject(int i) {
    return myCellarObjects.get(i);
  }

}
