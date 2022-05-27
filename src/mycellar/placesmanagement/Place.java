package mycellar.placesmanagement;

import mycellar.Program;
import mycellar.placesmanagement.places.IBasicPlace;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2021
 * Societe : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.7
 * @since 27/05/22
 */

public class Place {

  private final IBasicPlace rangement;
  private final int placeNum;
  private final int line;
  private final int column;

  private Place(IBasicPlace rangement, int placeNum) {
    this.rangement = rangement;
    this.placeNum = placeNum;
    line = -1;
    column = -1;
  }

  private Place(IBasicPlace rangement, int placeNum, int line, int column) {
    this.rangement = rangement;
    this.placeNum = placeNum;
    this.line = line;
    this.column = column;
  }

  public IBasicPlace getRangement() {
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

  /**
   * Zero based
   */
  public int getPlaceNumIndex() {
    if (isSimplePlace()) {
      return placeNum - rangement.getStartSimplePlace();
    }
    return placeNum - 1;
  }

  /**
   * Zero based
   */
  public int getLineIndex() {
    return line - 1;
  }

  /**
   * Zero based
   */
  public int getColumnIndex() {
    return column - 1;
  }

  public boolean isSimplePlace() {
    return rangement.isSimplePlace();
  }

  public boolean hasPlace() {
    return !Program.EMPTY_PLACE.equals(rangement);
  }

  @Override
  public String toString() {
    return "Place{" +
        "rangement=" + rangement +
        ", placeNum=" + placeNum +
        ", line=" + line +
        ", column=" + column +
        '}';
  }

  public static class PlaceBuilder {

    protected final IBasicPlace rangement;
    private int numPlace;
    private int line;
    private int column;

    public PlaceBuilder(IBasicPlace rangement) {
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
      if (rangement.isSimplePlace()) {
        return new Place(rangement, numPlace);
      }
      return new Place(rangement, numPlace, line, column);
    }
  }
}
