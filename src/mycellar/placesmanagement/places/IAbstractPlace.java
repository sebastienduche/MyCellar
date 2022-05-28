package mycellar.placesmanagement.places;

import mycellar.core.MyCellarObject;
import mycellar.core.exceptions.MyCellarException;
import mycellar.placesmanagement.Place;

import java.util.Optional;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2022
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 27/05/22
 */
public interface IAbstractPlace {

  @Deprecated
  boolean isSimplePlace();

  @Deprecated
  int getStartSimplePlace();

  String getName();

  int getPartCount();

  void clearStorage(MyCellarObject myCellarObject);

  void clearStorage(MyCellarObject myCellarObject, Place place);

  int getCountCellUsed(int part);

  boolean addObject(MyCellarObject myCellarObject);

  void removeObject(MyCellarObject myCellarObject) throws MyCellarException;

  void updateToStock(MyCellarObject myCellarObject);

  boolean canAddObjectAt(MyCellarObject b);

  boolean canAddObjectAt(Place place);

  boolean canAddObjectAt(int tmpNumEmpl, int tmpLine, int tmpCol);

  Optional<MyCellarObject> getObject(int num_empl, int line, int column);

  Optional<MyCellarObject> getObject(Place place);

  String toXml();

  void resetStockage();
}
