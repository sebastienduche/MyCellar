package mycellar.core.datas;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 12/03/21
 */

public class Place {

  private final String name;
  private final int placeNum;
  private int line;
  private int column;
  private final boolean simplePlace;

  public Place(String name, int placeNum) {
    this.name = name;
    this.placeNum = placeNum;
    simplePlace = true;
  }

  public Place(String name, int placeNum, int line, int column) {
    this.name = name;
    this.placeNum = placeNum;
    this.line = line;
    this.column = column;
    simplePlace = false;
  }

  public String getName() {
    return name;
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

  public boolean isSimplePlace() {
    return simplePlace;
  }
}
