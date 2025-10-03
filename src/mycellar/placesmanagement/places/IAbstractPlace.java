package mycellar.placesmanagement.places;

import mycellar.Bouteille;
import mycellar.core.exceptions.MyCellarException;

import java.util.Map;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2022
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.1
 * @since 03/10/25
 */
public interface IAbstractPlace {

  public boolean isSimplePlace();

  public boolean isComplexPlace();

  public boolean isIncorrectNumPlace(int numPlace);

  public String getName();

  public int getPartCount();

  public void clearStorage(Bouteille bottle);

  public void clearStorage(Bouteille bottle, PlacePosition place);

  public int getCountCellUsed(int part);

  public boolean addObject(Bouteille bottle);

  public void removeObject(Bouteille bottle) throws MyCellarException;

  public void updateToStock(Bouteille bottle);

  public boolean canAddObjectAt(PlacePosition place);

  public String toXml();

  public void resetStockage();

  public Map<Integer, Integer> getNumberOfObjectsPerPlace();

  public int getFirstPartNumber();

  public int getLastPartNumber();

  public int getTotalCountCellUsed();
}
