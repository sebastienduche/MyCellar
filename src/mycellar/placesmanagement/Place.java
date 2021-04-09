package mycellar.placesmanagement;

import mycellar.Program;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.5
 * @since 23/03/21
 */

public class Place {

  private final Rangement rangement;
  private final int placeNum;
  private final int line;
  private final int column;

  private Place(Rangement rangement, int placeNum) {
    this.rangement = rangement;
    this.placeNum = placeNum;
    line = -1;
    column = -1;
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

  public boolean hasPlace() {
    return !Program.EMPTY_PLACE.equals(rangement);
  }

  public static class PlaceBuilder {

    protected final Rangement rangement;
    private int numPlace;
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

    public Place build() {
      if (rangement.isCaisse()) {
        return new Place(rangement, numPlace);
      }
      return new Place(rangement, numPlace, line, column);
    }
  }
}
