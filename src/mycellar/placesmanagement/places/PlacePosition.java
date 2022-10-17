package mycellar.placesmanagement.places;

import mycellar.Program;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2021
 * Societe : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.4
 * @since 17/10/22
 */

public class PlacePosition {

  private final AbstractPlace abstractPlace;
  private final int part;
  private final int line;
  private final int column;
  private final boolean oneBased;

  private PlacePosition(AbstractPlace abstractPlace, int part, int line, int column, boolean oneBased) {
    this.abstractPlace = abstractPlace;
    this.part = part;
    this.line = line;
    this.column = column;
    this.oneBased = oneBased;
  }

  public AbstractPlace getAbstractPlace() {
    return abstractPlace;
  }

  public int getPart() {
    return oneBased ? part : part + 1;
  }

  public int getLine() {
    return oneBased ? line : line + 1;
  }

  public int getColumn() {
    return oneBased ? column : column + 1;
  }

  /**
   * Zero based
   */
  public int getPlaceNumIndex() {
    if (part == -1) {
      return part;
    }
    if (isSimplePlace()) {
      return part - ((SimplePlace) abstractPlace).getPartNumberIncrement() + (oneBased ? 0 : 1);
    }
    return oneBased ? part - 1 : part;
  }

  /**
   * Zero based
   */
  public int getLineIndex() {
    return oneBased ? line - 1 : line;
  }

  /**
   * Zero based
   */
  public int getColumnIndex() {
    return oneBased ? column - 1 : column;
  }

  public boolean isSimplePlace() {
    return abstractPlace.isSimplePlace();
  }

  public boolean isComplexPlace() {
    return abstractPlace.isComplexPlace();
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

    public PlacePositionBuilder withLine(int line) {
      this.line = line;
      return this;
    }

    public PlacePositionBuilder withColumn(int column) {
      this.column = column;
      return this;
    }

    public PlacePosition build() {
      if (rangement.isSimplePlace()) {
        return new PlacePosition(rangement, numPlace, -1, -1, true);
      }
      return new PlacePosition(rangement, numPlace, line, column, true);
    }
  }

  public static class PlacePositionBuilderZeroBased {

    protected final AbstractPlace rangement;
    private int numPlace;
    private int line;
    private int column;

    public PlacePositionBuilderZeroBased(AbstractPlace rangement) {
      this.rangement = rangement;
    }

    public PlacePositionBuilderZeroBased withNumPlace(int numPlace) {
      this.numPlace = numPlace;
      return this;
    }

    public PlacePositionBuilderZeroBased withLine(int line) {
      this.line = line;
      return this;
    }

    public PlacePositionBuilderZeroBased withColumn(int column) {
      this.column = column;
      return this;
    }

    public PlacePosition build() {
      if (rangement.isSimplePlace()) {
        return new PlacePosition(rangement, numPlace, -1, -1, false);
      }
      return new PlacePosition(rangement, numPlace, line, column, false);
    }
  }
}
