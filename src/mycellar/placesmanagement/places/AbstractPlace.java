package mycellar.placesmanagement.places;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.exceptions.MyCellarException;

import java.util.Objects;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2022
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 03/10/25
 */
public abstract class AbstractPlace implements Comparable<AbstractPlace>, IAbstractPlace {

  private String name;
  int partCount;
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

  public void setPartCount(int partCount) {
    this.partCount = partCount;
  }

  boolean isDefaultPlace() {
    return defaultPlace;
  }

  void setDefaultPlace(boolean defaultPlace) {
    this.defaultPlace = defaultPlace;
  }

  @Override
  public void removeObject(Bouteille bottle) throws MyCellarException {
    clearStorage(bottle);
    Program.getStorage().deleteWine(bottle);
  }

  @Override
  public void clearStorage(Bouteille bottle) {
    clearStorage(bottle, bottle.getPlacePosition());
  }

  public int getCountCellUsed(PlacePosition place) {
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
