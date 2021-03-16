package mycellar.placesmanagement;

import mycellar.Program;

import java.util.Optional;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 16/03/21
 */

public class Place {

  private final Rangement rangement;
  private final int placeNum;
  private int line;
  private int column;

  private Place(Rangement rangement, int placeNum) {
    this.rangement = rangement;
    this.placeNum = placeNum;
  }

  private Place(Rangement rangement, int placeNum, int line, int column) {
    this.rangement = rangement;
    this.placeNum = placeNum;
    this.line = line;
    this.column = column;
  }

  public Rangement getRangement() {
    return rangement;
  }

  public int getPlaceNum() {
    return placeNum;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public int getPlaceNumIndexForCombo() {
    if (isSimplePlace()) {
      return placeNum - rangement.getStartCaisse() + 1;
    }
    return placeNum;
  }

  /** Zero based */
  public int getPlaceNumIndex() {
    if (isSimplePlace()) {
      return placeNum - rangement.getStartCaisse();
    }
    return placeNum - 1;
  }

  /** Zero based */
  public int getLineIndex() {
    return line - 1;
  }

  /** Zero based */
  public int getColumnIndex() {
    return column - 1;
  }

  public boolean isSimplePlace() {
    return rangement.isCaisse();
  }

  public static class PlaceBuilder {

    protected final Rangement rangement;
    public int numPlace;
    private int line;
    private int column;

    public PlaceBuilder(Rangement rangement) {
      this.rangement = rangement;
    }

    public PlaceBuilder withNumPlace(int numPlace) {
      this.numPlace = numPlace;
      return this;
    }

    public PlaceBuilder withLine(int line) {
      this.line = line;
      return this;
    }

    public PlaceBuilder withColumn(int column) {
      this.column = column;
      return this;
    }

    public Optional<Place> build() {
      if (!validate()) {
        return Optional.empty();
      }
      if (rangement.isCaisse()) {
        return Optional.of(new Place(rangement, numPlace));
      }
      return Optional.of(new Place(rangement, numPlace, line, column));
    }

    public boolean validate() {
      if (rangement.isCaisse()) {
        final boolean result = !rangement.isInexistingNumPlace(numPlace);
        if (!result) {
          Debug("Place: ERROR: Inexisting num place '" + numPlace + "' in " + rangement.getNom());
        }
        return result;
      }
      final boolean existingCell = rangement.isExistingCell(numPlace - 1, line - 1, column - 1);
      if (!existingCell) {
        Debug("ERROR: Inexisting cell: numplace: " + (numPlace - 1) + ", line: " + (line - 1) + ", column:" + (column - 1) + " in " + rangement.getNom());
      }
      return existingCell;
    }
  }

  private static void Debug(String sText) {
    Program.Debug("Rangement: " + sText);
  }
}
