package mycellar.placesmanagement.places;

import mycellar.core.IMyCellarObject;
import mycellar.core.exceptions.MyCellarException;

import java.util.Map;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2022
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.0
 * @since 21/03/25
 */
public interface IAbstractPlace {

  public boolean isSimplePlace();

  public boolean isComplexPlace();

  public boolean isIncorrectNumPlace(int numPlace);

  public String getName();

  public int getPartCount();

  public void clearStorage(IMyCellarObject myCellarObject);

  public void clearStorage(IMyCellarObject myCellarObject, PlacePosition place);

  public int getCountCellUsed(int part);

  public boolean addObject(IMyCellarObject myCellarObject);

  public void removeObject(IMyCellarObject myCellarObject) throws MyCellarException;

  public void updateToStock(IMyCellarObject myCellarObject);

  public boolean canAddObjectAt(PlacePosition place);

  public String toXml();

  public void resetStockage();

  public Map<Integer, Integer> getNumberOfObjectsPerPlace();

  public int getFirstPartNumber();

  public int getLastPartNumber();

  public int getTotalCountCellUsed();
}
