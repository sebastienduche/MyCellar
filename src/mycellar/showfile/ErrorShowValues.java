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
import mycellar.placesmanagement.places.AbstractPlace;

import java.text.MessageFormat;
import java.util.Arrays;
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
 * @version 2.7
 * @since 01/06/22
 */

public class ErrorShowValues extends TableShowValues {

  enum Column {
    ETAT(0),
    ERROR(1),
    NAME(2),
    YEAR(3),
    TYPE(4),
    PLACE(5),
    NUM_PLACE(6),
    LINE(7),
    COLUMN(8),
    STATUS(9),
    BUTTON(10);

    private final int index;

    Column(int i) {
      index = i;
    }

    int getIndex() {
      return index;
    }

    static Column fromIndex(int i) {
      return Arrays.stream(values()).filter(column -> column.getIndex() == i).findFirst().orElse(null);
    }
  }

  private static final long serialVersionUID = 2477822182069165515L;
  private static final int NBCOL = 11;
  private final String[] columnNames = {"", getLabel("ErrorShowValues.Error"), getLabel("Main.Item", LabelProperty.SINGLE.withCapital()), getLabel("Main.Year"), getLabel("Main.CapacityOrSupport"), getLabel("Main.Storage"),
      getLabel("MyCellarFields.NumPlace"), getLabel("MyCellarFields.Line"), getLabel("MyCellarFields.Column"), getLabel("ShowFile.Status"), ""};

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
    final Column column1 = Column.fromIndex(column);
    if (column1 == null) {
      return "";
    }
    switch (column1) {
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
  public boolean isCellEditable(int row, int col) {
    if (!Boolean.FALSE.equals(editable[row])) {
      final Column column = Column.fromIndex(col);
      switch (column) {
        case ETAT:
        case NAME:
        case TYPE:
        case YEAR:
        case PLACE:
        case NUM_PLACE:
        case LINE:
        case COLUMN:
        case BUTTON:
          return true;
        default:
          return false;
      }
    }
    return false;
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    MyCellarError error = errors.get(row);
    MyCellarObject b = error.getMyCellarObject();
    AbstractPlace rangement;
    final Column column = Column.fromIndex(col);
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
          Erreur.showSimpleErreur(getError("Error.enterValidYear"));
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

        if (column.equals(Column.PLACE)) {
          empl = (String) value;
          if (RangementUtils.isExistingPlace(empl)) {
            rangement = Program.getPlaceByName(empl);
          }
        } else if (column.equals(Column.NUM_PLACE)) {
          Integer i = parseIntOrError(value);
          if (i == null) {
            bError = true;
          } else {
            num_empl = i;
            nValueToCheck = i;
          }
        } else if (column.equals(Column.LINE)) {
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

        if (!bError && (column.equals(Column.NUM_PLACE) || column.equals(Column.LINE) || column.equals(Column.COLUMN))) {
          if (!rangement.isSimplePlace() && nValueToCheck <= 0) {
            Erreur.showSimpleErreur(getError("Error.enterNumericValueAboveZero"));
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
              Erreur.showSimpleErreur(MessageFormat.format(getError("Error.alreadyInStorage"), convertStringFromHTMLString(bTemp.get().getNom()), b.getAnnee()));
            } else {
              if (column.equals(Column.PLACE)) {
                b.setEmplacement((String) value);
                if (rangement.isSimplePlace()) {
                  int nNumEmpl = b.getNumLieu();
                  if (nNumEmpl > ((Rangement) rangement).getLastPartNumber()) {
                    b.setNumLieu(((Rangement) rangement).getFreeNumPlaceInSimplePlace());
                  }
                  b.setLigne(0);
                  b.setColonne(0);
                }
              } else if (column.equals(Column.NUM_PLACE)) {
                b.setNumLieu(Integer.parseInt((String) value));
              } else if (column.equals(Column.LINE)) {
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
      default:
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
