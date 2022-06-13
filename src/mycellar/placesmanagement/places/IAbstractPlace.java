package mycellar.placesmanagement.places;

import mycellar.core.MyCellarObject;
import mycellar.core.exceptions.MyCellarException;

import java.util.Map;
import java.util.Optional;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2022
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.5
 * @since 13/06/22
 */
public interface IAbstractPlace {

  public boolean isSimplePlace();

  public String getName();

  public int getPartCount();

  public void clearStorage(MyCellarObject myCellarObject);

  public void clearStorage(MyCellarObject myCellarObject, PlacePosition place);

  public int getCountCellUsed(int part);

  public boolean addObject(MyCellarObject myCellarObject);

  public void removeObject(MyCellarObject myCellarObject) throws MyCellarException;

  public void updateToStock(MyCellarObject myCellarObject);

  @Deprecated
  public boolean canAddObjectAt(MyCellarObject b);

  public boolean canAddObjectAt(PlacePosition place);

  @Deprecated
  public boolean canAddObjectAt(int tmpNumEmpl, int tmpLine, int tmpCol);

  @Deprecated
  public Optional<MyCellarObject> getObject(int num_empl, int line, int column);

  public Optional<MyCellarObject> getObject(PlacePosition place);

  public String toXml();

  public void resetStockage();

  public Map<Integer, Integer> getNumberOfObjectsPerPlace();

  public int getFirstPartNumber();

  public int getLastPartNumber();

  public int getTotalCountCellUsed();
}
