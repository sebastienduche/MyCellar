package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarObject;
import mycellar.core.text.LabelProperty;
import mycellar.frame.MainFrame;
import mycellar.placesmanagement.PanelPlacePosition;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.SimplePlace;

import javax.swing.JOptionPane;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
 * @version 3.5
 * @since 11/09/24
 */

public class ErrorShowValues extends TableShowValues {

  enum Column {
    STATE(0),
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
    return switch (column1) {
      case STATE -> values[row];
      case NAME -> convertStringFromHTMLString(b.getNom());
      case YEAR -> b.getAnnee();
      case TYPE -> b.getKind();
      case PLACE -> convertStringFromHTMLString(b.getEmplacement());
      case NUM_PLACE -> Integer.toString(b.getNumLieu());
      case LINE -> Integer.toString(b.getLigne());
      case COLUMN -> Integer.toString(b.getColonne());
      case STATUS -> {
        if (error.isStatus()) {
          yield getLabel("ShowFile.Added");
        }
        yield status[row] ? getLabel("Main.OK") : getLabel("Main.KO");
      }
      case BUTTON -> true;
      case ERROR -> convertStringFromHTMLString(error.getErrorMessage());
    };
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    if (!Boolean.FALSE.equals(editable[row])) {
      final Column column = Column.fromIndex(col);
      return switch (column) {
        case STATE, NAME, TYPE, YEAR, PLACE, NUM_PLACE, LINE, COLUMN, BUTTON -> true;
        default -> false;
      };
    }
    return false;
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    MyCellarError error = errors.get(row);
    MyCellarObject b = error.getMyCellarObject();
    AbstractPlace abstractPlace;
    final Column column = Column.fromIndex(col);
    switch (column) {
      case STATE:
        values[row] = (Boolean) value;
        break;
      case BUTTON:
        AbstractPlace abstractPlace1 = b.getAbstractPlace();
        if (abstractPlace1 != null && abstractPlace1.canAddObjectAt(b.getPlacePosition())) {
          if (abstractPlace1.isSimplePlace() || (abstractPlace1.isComplexPlace() && ((ComplexPlace)abstractPlace1).getObject(b.getPlacePosition()).isEmpty())) {
            error.setSolved(true);
            Program.getStorage().addWine(b);
            editable[row] = Boolean.FALSE;
            error.setStatus(true);
            fireTableRowsUpdated(row, row);
          } else {
            status[row] = Boolean.FALSE;
            Erreur.showSimpleErreur(getError("ShowFile.errorAddingBottle", LabelProperty.THE_SINGLE));
          }
        } else {
          status[row] = Boolean.FALSE;
          Erreur.showSimpleErreur(getError("ShowFile.errorAddingBottle", LabelProperty.THE_SINGLE));
        }
        break;
      case NAME:
        b.setNom(String.valueOf(value));
        break;
      case TYPE:
        b.setKind(String.valueOf(value));
        break;
      case YEAR:
        if (Program.hasYearControl() && Bouteille.isInvalidYear((String) value)) {
          Erreur.showSimpleErreur(getError("Error.enterValidYear"));
        } else {
          b.setAnnee(String.valueOf(value));
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
        abstractPlace = b.getAbstractPlace();
        boolean bError = false;
        int nValueToCheck = -1;
        String empl = empl_old;
        int num_empl = num_empl_old;
        int line = line_old;
        int column1 = column_old;

        if (column.equals(Column.PLACE)) {
          empl = ((AbstractPlace) value).getName();
          abstractPlace = (AbstractPlace) value;
        } else if (column.equals(Column.NUM_PLACE)) {
          Integer i = parseIntOrError(String.valueOf(value));
          if (i == null) {
            bError = true;
          } else {
            num_empl = i;
            nValueToCheck = i;
          }
        } else if (column.equals(Column.LINE)) {
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

        if (!bError && (column.equals(Column.NUM_PLACE) || column.equals(Column.LINE) || column.equals(Column.COLUMN))) {
          if (b.getAbstractPlace() != null && !b.getAbstractPlace().isSimplePlace() && nValueToCheck <= 0) {
            Erreur.showSimpleErreur(getError("Error.enterNumericValueAboveZero"));
            bError = true;
          }
        }

        if (abstractPlace != null && !bError && (empl_old.compareTo(empl) != 0 || num_empl_old != num_empl || line_old != line || column_old != column1)) {
          if (abstractPlace.canAddObjectAt(new PlacePosition.PlacePositionBuilder(abstractPlace)
              .withNumPlace(num_empl)
              .withLine(line)
              .withColumn(column1).build())) {
            MyCellarObject searchObject = null;
            if (abstractPlace.isComplexPlace()) {
              searchObject = ((ComplexPlace)abstractPlace).getObject(new PlacePosition.PlacePositionBuilderZeroBased(abstractPlace)
                  .withNumPlace(num_empl)
                  .withLine(line)
                  .withColumn(column1)
                  .build()).orElse(null);
            }
            if (searchObject != null) {
              status[row] = Boolean.FALSE;
              Erreur.showSimpleErreur(MessageFormat.format(getError("Error.alreadyInStorage"), convertStringFromHTMLString(searchObject.getNom()), b.getAnnee()));
            } else {
              if (column.equals(Column.PLACE)) {
                b.setEmplacement(empl);
                if (abstractPlace.isSimplePlace()) {
                  int nNumEmpl = b.getNumLieu();
                  if (nNumEmpl > abstractPlace.getLastPartNumber()) {
                    b.setNumLieu(((SimplePlace) abstractPlace).getFreeNumPlace());
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
            if (MyCellarUtils.isAnyOf(column, List.of(Column.PLACE, Column.NUM_PLACE, Column.LINE, Column.COLUMN))) {
              final PanelPlacePosition panelPlace = new PanelPlacePosition(abstractPlace, true, false, true, true, false, true, false);
              JOptionPane.showMessageDialog(MainFrame.getInstance(), panelPlace,
                  getLabel("Main.ChooseCell"),
                  JOptionPane.PLAIN_MESSAGE);
              PlacePosition place = panelPlace.getSelectedPlacePosition();
              if (place.hasPlace()) {
                abstractPlace = place.getAbstractPlace();
                b.setEmplacement(abstractPlace.getName());
                b.setNumLieu(place.getPart());
                b.setLigne(place.getLine());
                b.setColonne(place.getColumn());
                status[row] = place.hasValidPlace();
              } else {
                status[row] = Boolean.FALSE;
              }
            }
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
