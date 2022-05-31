package mycellar.placesmanagement.places;

import mycellar.Program;
import mycellar.core.MyCellarObject;
import mycellar.core.exceptions.MyCellarException;
import mycellar.placesmanagement.Place;

import java.util.Objects;
import java.util.Optional;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2022
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.2
 * @since 31/05/22
 */
public abstract class AbstractPlace implements Comparable<AbstractPlace>, IAbstractPlace {

  private String name;
  protected int partCount;
  private boolean defaultPlace = false;

  public AbstractPlace(String name) {
    this.name = name.strip();
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name.strip();
  }

  @Override
  public int getPartCount() {
    return partCount;
  }

  protected void setPartCount(int partCount) {
    this.partCount = partCount;
  }

  public boolean isDefaultPlace() {
    return defaultPlace;
  }

  public void setDefaultPlace(boolean defaultPlace) {
    this.defaultPlace = defaultPlace;
  }

  @Override
  public void removeObject(MyCellarObject myCellarObject) throws MyCellarException {
    clearStorage(myCellarObject);
    Program.getStorage().deleteWine(myCellarObject);
  }

  @Override
  public void clearStorage(MyCellarObject myCellarObject) {
    clearStorage(myCellarObject, myCellarObject.getPlace());
  }

  public int getCountCellUsed(Place place) {
    return getCountCellUsed(place.getPlaceNumIndex());
  }

  public int getTotalCellUsed() {
    int resul = 0;
    for (int i = 0; i < partCount; i++) {
      resul += getCountCellUsed(i);
    }
    return resul;
  }

  @Override
  public Optional<MyCellarObject> getObject(Place place) {
    return getObject(place.getPlaceNumIndex(), place.getLineIndex(), place.getColumnIndex());
  }

  @Override
  public boolean canAddObjectAt(Place place) {
    return canAddObjectAt(place.getPlaceNumIndex(), place.getLineIndex(), place.getColumnIndex());
  }

  @Override
  public boolean isSimplePlace() {
    return false;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(defaultPlace, name, partCount);
  }

  @Override
  public int compareTo(AbstractPlace o) {
    return getName().compareTo(o.getName());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractPlace other = (AbstractPlace) obj;
    return defaultPlace == other.defaultPlace && Objects.equals(name, other.name) && partCount == other.partCount;
  }

}