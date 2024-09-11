package mycellar.placesmanagement.places;

import mycellar.core.MyCellarObject;
import mycellar.core.exceptions.MyCellarException;

import java.util.Map;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2022
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.9
 * @since 11/09/24
 */
public interface IAbstractPlace {

  public boolean isSimplePlace();

  public boolean isComplexPlace();

  public boolean isIncorrectNumPlace(int numPlace);

  public String getName();

  public int getPartCount();

  public void clearStorage(MyCellarObject myCellarObject);

  public void clearStorage(MyCellarObject myCellarObject, PlacePosition place);

  public int getCountCellUsed(int part);

  public boolean addObject(MyCellarObject myCellarObject);

  public void removeObject(MyCellarObject myCellarObject) throws MyCellarException;

  public void updateToStock(MyCellarObject myCellarObject);

  public boolean canAddObjectAt(PlacePosition place);

  public String toXml();

  public void resetStockage();

  public Map<Integer, Integer> getNumberOfObjectsPerPlace();

  public int getFirstPartNumber();

  public int getLastPartNumber();

  public int getTotalCountCellUsed();
}
