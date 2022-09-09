package mycellar.placesmanagement.places;

import mycellar.Program;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2021
 * Societe : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.1
 * @since 09/09/22
 */

public class PlacePosition {

  private final AbstractPlace abstractPlace;
  private final int part;
  private final int line;
  private final int column;

  private PlacePosition(AbstractPlace abstractPlace, int part) {
    this.abstractPlace = abstractPlace;
    this.part = part;
    line = -1;
    column = -1;
  }

  private PlacePosition(AbstractPlace abstractPlace, int part, int line, int column) {
    this.abstractPlace = abstractPlace;
    this.part = part;
    this.line = line;
    this.column = column;
  }

  public AbstractPlace getAbstractPlace() {
    return abstractPlace;
  }

  public int getPart() {
    return part;
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
    if (part == -1) {
      return part;
    }
    if (isSimplePlace()) {
      return part - ((SimplePlace) abstractPlace).getPartNumberIncrement();
    }
    return part - 1;
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
    return abstractPlace.isSimplePlace();
  }

  public boolean hasPlace() {
    return !Program.EMPTY_PLACE.equals(abstractPlace);
  }

  @Override
  public String toString() {
    return "PlacePosition{" +
        "abstractPlace=" + abstractPlace +
        ", part=" + part +
        ", line=" + line +
        ", column=" + column +
        '}';
  }

  public static class PlacePositionBuilder {

    protected final AbstractPlace rangement;
    private int numPlace;
    private int line;
    private int column;

    public PlacePositionBuilder(AbstractPlace rangement) {
      this.rangement = rangement;
    }

    public PlacePositionBuilder withNumPlace(int numPlace) {
      this.numPlace = numPlace;
      return this;
    }

    public PlacePositionBuilder withNumPlace1Based(int numPlace) {
      this.numPlace = numPlace + 1;
      return this;
    }

    public PlacePositionBuilder withLine(int line) {
      this.line = line;
      return this;
    }

    public PlacePositionBuilder withLine1Based(int line) {
      this.line = line + 1;
      return this;
    }

    public PlacePositionBuilder withColumn(int column) {
      this.column = column;
      return this;
    }

    public PlacePositionBuilder withColumn1Based(int column) {
      this.column = column + 1;
      return this;
    }

    public PlacePosition build() {
      if (rangement.isSimplePlace()) {
        return new PlacePosition(rangement, numPlace);
      }
      return new PlacePosition(rangement, numPlace, line, column);
    }
  }
}
