package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Program;
import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarObject;
import mycellar.core.text.LabelProperty;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static mycellar.MyCellarUtils.convertStringFromHTMLString;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.1
 * @since 30/03/22
 */

public class ErrorShowValues extends TableShowValues {

  public static final int NAME = 2;
  public static final int YEAR = 3;
  public static final int TYPE = 4;
  public static final int PLACE = 5;
  static final int STATUS = 9;
  static final int BUTTON = 10;
  private static final long serialVersionUID = 2477822182069165515L;
  private static final int ETAT = 0;
  private static final int ERROR = 1;
  private static final int NUM_PLACE = 6;
  private static final int LINE = 7;
  private static final int COLUMN = 8;
  private static final int NBCOL = 11;
  private final String[] columnNames = {"", getLabel("ErrorShowValues.error"), getLabel("Main.Item", LabelProperty.SINGLE.withCapital()), getLabel("Infos189"), getLabel("Infos134"), getLabel("Infos217"),
      getLabel("MyCellarFields.numPlace"), getLabel("MyCellarFields.line"), getLabel("MyCellarFields.column"), getLabel("ShowFile.Status"), ""};

  private Boolean[] status = null;
  private Boolean[] editable = null;

  private List<MyCellarError> errors = new LinkedList<>();

  @Override
  public int getRowCount() {
    return errors.size();
  }

  @Override
  public int getColumnCount() {
    return NBCOL;
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (errors.size() <= row) {
      return null;
    }
    MyCellarError error = errors.get(row);
    IMyCellarObject b = error.getMyCellarObject();
    switch (column) {
      case ETAT:
        return values[row];
      case NAME:
        return convertStringFromHTMLString(b.getNom());
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
      case STATUS:
        if (error.isStatus()) {
          return getLabel("ShowFile.Added");
        }
        return status[row] ? getLabel("Main.OK") : getLabel("Main.KO");
      case BUTTON:
        return true;
      case ERROR:
        return convertStringFromHTMLString(error.getErrorMessage());
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
    return !Boolean.FALSE.equals(editable[row]) && (column == ETAT
        || column == NAME
        || column == TYPE
        || column == YEAR
        || column == PLACE
        || column == NUM_PLACE
        || column == LINE
        || column == COLUMN
        || column == BUTTON);
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    MyCellarError error = errors.get(row);
    MyCellarObject b = error.getMyCellarObject();
    Rangement rangement;
    switch (column) {
      case ETAT:
        values[row] = (Boolean) value;
        break;
      case BUTTON:
        rangement = b.getRangement();
        if (rangement != null && rangement.canAddObjectAt(b)) {
          error.setSolved(true);
          Program.getStorage().addWine(b);
          editable[row] = Boolean.FALSE;
          error.setStatus(true);
          fireTableRowsUpdated(row, row);
        } else {
          status[row] = Boolean.FALSE;
          Erreur.showSimpleErreur(getError("ShowFile.errorAddingBottle", LabelProperty.THE_SINGLE));
        }
        break;
      case NAME:
        b.setNom((String) value);
        break;
      case TYPE:
        b.setKind((String) value);
        break;
      case YEAR:
        if (Program.hasYearControl() && Bouteille.isInvalidYear((String) value)) {
          Erreur.showSimpleErreur(getError("Error053"));
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
        rangement = b.getRangement();
        boolean bError = false;
        int nValueToCheck = -1;
        String empl = empl_old;
        int num_empl = num_empl_old;
        int line = line_old;
        int column1 = column_old;

        if (column == PLACE) {
          empl = (String) value;
          if (RangementUtils.isExistingPlace(empl)) {
            rangement = Program.getPlaceByName(empl);
          }
        } else if (column == NUM_PLACE) {
          try {
            num_empl = Integer.parseInt((String) value);
            nValueToCheck = num_empl;
          } catch (NumberFormatException e) {
            Erreur.showSimpleErreur(getError("Error196"));
            bError = true;
          }
        } else if (column == LINE) {
          try {
            line = Integer.parseInt((String) value);
            nValueToCheck = line;
          } catch (NumberFormatException e) {
            Erreur.showSimpleErreur(getError("Error196"));
            bError = true;
          }
        } else {
          try {
            column1 = Integer.parseInt((String) value);
            nValueToCheck = column1;
          } catch (NumberFormatException e) {
            Erreur.showSimpleErreur(getError("Error196"));
            bError = true;
          }
        }

        if (!bError && (column == NUM_PLACE || column == LINE || column == COLUMN)) {
          if (!rangement.isSimplePlace() && nValueToCheck <= 0) {
            Erreur.showSimpleErreur(getError("Error197"));
            bError = true;
          }
        }

        if (!bError && (empl_old.compareTo(empl) != 0 || num_empl_old != num_empl || line_old != line || column_old != column1)) {
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
              status[row] = Boolean.FALSE;
              Erreur.showSimpleErreur(MessageFormat.format(getError("Error059"), convertStringFromHTMLString(bTemp.get().getNom()), b.getAnnee()));
            } else {
              if (column == PLACE) {
                b.setEmplacement((String) value);
                if (rangement.isSimplePlace()) {
                  int nNumEmpl = b.getNumLieu();
                  if (nNumEmpl > rangement.getLastPartNumber()) {
                    b.setNumLieu(rangement.getFreeNumPlaceInSimplePlace());
                  }
                  b.setLigne(0);
                  b.setColonne(0);
                }
              } else if (column == NUM_PLACE) {
                b.setNumLieu(Integer.parseInt((String) value));
              } else if (column == LINE) {
                b.setLigne(Integer.parseInt((String) value));
              } else {
                b.setColonne(Integer.parseInt((String) value));
              }
              status[row] = Boolean.TRUE;
            }
          } else {
            status[row] = Boolean.FALSE;
          }
        }
        fireTableRowsUpdated(row, row);
      }
      break;
    }
  }

  public void setErrors(List<MyCellarError> myCellarErrors) {
    values = new Boolean[myCellarErrors.size()];
    status = new Boolean[myCellarErrors.size()];
    editable = new Boolean[myCellarErrors.size()];
    errors = myCellarErrors;
    for (int i = 0; i < myCellarErrors.size(); i++) {
      values[i] = Boolean.FALSE;
      status[i] = Boolean.FALSE;
      editable[i] = Boolean.TRUE;
    }
    fireTableDataChanged();
  }

  @Override
  public MyCellarObject getMyCellarObject(int i) {
    return errors.get(i).getMyCellarObject();
  }

}
