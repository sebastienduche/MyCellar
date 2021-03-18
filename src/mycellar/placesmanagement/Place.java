package mycellar.placesmanagement;

import mycellar.Program;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.4
 * @since 17/03/21
 */

public class Place {

  private final Rangement rangement;
  private final int placeNum;
  private final int placeNumValueSimplePlace; // Value as displayed in the combo
  private int line;
  private int column;

  private Place(Rangement rangement, int placeNum, int placeNumValueSimplePlace) {
    this.rangement = rangement;
    this.placeNum = placeNum;
    this.placeNumValueSimplePlace = placeNumValueSimplePlace;
  }

  private Place(Rangement rangement, int placeNum, int placeNumValueSimplePlace, int line, int column) {
    this.rangement = rangement;
    this.placeNum = placeNum;
    this.placeNumValueSimplePlace = placeNumValueSimplePlace;
    this.line = line;
    this.column = column;
  }

  public Rangement getRangement() {
    return rangement;
  }

  public int getPlaceNum() {
    if (isSimplePlace()) {
      return placeNumValueSimplePlace;
    }
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
      return getPlaceNumValueSimplePlace() - 1;
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

  public int getPlaceNumValueSimplePlace() {
    return placeNumValueSimplePlace;
  }

  public boolean hasPlace() {
    return !Program.EMPTY_PLACE.equals(rangement);
  }

  public static class PlaceBuilder {

    protected final Rangement rangement;
    private int numPlace;
    private int numPlaceSimplePlace;
    private int line;
    private int column;

    public PlaceBuilder(Rangement rangement) {
      this.rangement = rangement;
    }

    public PlaceBuilder withNumPlace(int numPlace) {
      this.numPlace = numPlace;
      return this;
    }

    public PlaceBuilder withNumPlaces(int numPlace, int numPlaceSimplePlace) {
      this.numPlace = numPlace;
      this.numPlaceSimplePlace = numPlaceSimplePlace;
      return this;
    }

    public PlaceBuilder withNumPlaceSimplePlace(String numPlaceSimplePlace) {
      this.numPlaceSimplePlace = Program.safeParseInt(numPlaceSimplePlace, -1);
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

    public Place build() {
      if (rangement.isCaisse()) {
        return new Place(rangement, numPlace, numPlaceSimplePlace);
      }
      return new Place(rangement, numPlace, numPlaceSimplePlace, line, column);
    }
  }
}
