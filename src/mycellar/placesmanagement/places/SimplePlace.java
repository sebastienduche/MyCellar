package mycellar.placesmanagement.places;

import mycellar.Program;
import mycellar.core.IMyCellarObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2022
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.9
 * @since 21/03/25
 */
public final class SimplePlace extends AbstractPlace {

  private int partNumberIncrement;
  private boolean limited;
  private int maxItemCount;
  private Map<Integer, ArrayList<IMyCellarObject>> storage;

  public SimplePlace(String name, int partCount) {
    super(name);
    setPartCount(partCount);
    partNumberIncrement = 0;
    limited = false;
    maxItemCount = -1;
    resetStockage();
  }

  public int getPartNumberIncrement() {
    return partNumberIncrement;
  }

  public void setPartNumberIncrement(int partNumberIncrement) {
    this.partNumberIncrement = partNumberIncrement;
  }

  public boolean isLimited() {
    return limited;
  }

  public void setLimited(boolean limited, int maxItemCount) {
    this.limited = limited;
    if (limited) {
      this.maxItemCount = maxItemCount;
    } else {
      this.maxItemCount = -1;
    }
  }

  public int getMaxItemCount() {
    return maxItemCount;
  }

  public void setMaxItemCount(int maxItemCount) {
    setLimited(limited, maxItemCount);
  }

  @Override
  public boolean isSimplePlace() {
    return true;
  }

  @Override
  public boolean isComplexPlace() {
    return false;
  }

  @Override
  public int getCountCellUsed(int part) {
    return storage.get(part).size();
  }

  @Override
  public boolean addObject(IMyCellarObject myCellarObject) {
    if (myCellarObject.hasNoStatus()) {
      myCellarObject.setCreated();
    }
    myCellarObject.setLigne(0);
    myCellarObject.setColonne(0);

    Debug("addObject: " + myCellarObject.getNom() + " " + myCellarObject.getEmplacement() + " " + myCellarObject.getNumLieu());

    int numLieu = myCellarObject.getNumLieu();
    int count = getCountCellUsed(numLieu - partNumberIncrement);
    if (limited && count == maxItemCount) {
      return false;
    }
    updateToStock(myCellarObject);
    Program.getStorage().addWine(myCellarObject);
    return true;
  }

  /**
   * Retourne l'objet se trouvant &agrave; un emplacement pr&eacute;cis
   *
   * @param num_empl int: num&eacute;ro d'emplacement (0...n)
   * @param index    int: index de l'objet (0...n)
   * @return IMyCellarObject
   */
  public IMyCellarObject getObjectAt(int num_empl, int index) {
    return storage.get(num_empl).get(index);
  }

  public boolean hasFreeSpace(PlacePosition place) {
    return hasFreeSpace(place.getPlaceNumIndex());
  }

  private boolean hasFreeSpace(int part) {
    return !isLimited() || getCountCellUsed(part) < maxItemCount;
  }

  @Override
  public boolean isIncorrectNumPlace(int numPlace) {
    return numPlace < partNumberIncrement || numPlace >= partCount + partNumberIncrement;
  }

  @Override
  public Map<Integer, Integer> getNumberOfObjectsPerPlace() {
    Map<Integer, Integer> numberOfObjectsPerPlace = new HashMap<>(partCount);
    for (int i = 0; i < partCount; i++) {
      numberOfObjectsPerPlace.put(i, getCountCellUsed(i));
    }
    return numberOfObjectsPerPlace;
  }

  @Override
  public void updateToStock(IMyCellarObject myCellarObject) {
    storage.get(myCellarObject.getNumLieu() - partNumberIncrement).add(myCellarObject);
  }

  private static void Debug(String sText) {
    Program.Debug("SimplePlace: " + sText);
  }

  @Override
  public void clearStorage(IMyCellarObject myCellarObject, PlacePosition place) {
    storage.get(place.getPart() - partNumberIncrement).remove(myCellarObject);
  }

  @Override
  public boolean canAddObjectAt(PlacePosition place) {
    final int placeNumIndex = place.getPlaceNumIndex();
    if (placeNumIndex < 0 || placeNumIndex >= getPartCount()) {
      return false;
    }
    if (!isLimited()) {
      return true;
    }
    if (getCountCellUsed(placeNumIndex) < maxItemCount) {
      return true;
    }
    return false;
  }

  @Override
  public String toXml() {
    StringBuilder sText = new StringBuilder();
    sText.append("<place name=\"\" IsCaisse=\"true\" NbPlace=\"")
        .append(partCount)
        .append("\" NumStart=\"")
        .append(partNumberIncrement)
        .append("\"");
    if (isLimited()) {
      sText.append(" NbLimit=\"").append(maxItemCount).append("\"");
    } else {
      sText.append(" NbLimit=\"0\"");
    }
    if (isDefaultPlace()) {
      sText.append(" default=\"true\">");
    } else {
      sText.append(" default=\"false\">");
    }
    sText.append("<name><![CDATA[").append(getName()).append("]]></name></place>");
    return sText.toString();
  }

  @Override
  public void resetStockage() {
    storage = new HashMap<>(partCount);
    for (int i = 0; i < partCount; i++) {
      storage.put(i, new ArrayList<>());
    }
  }

  public int getFreeNumPlace() {
    for (int i = 0; i < partCount; i++) {
      if (hasFreeSpace(i)) {
        return i + partNumberIncrement;
      }
    }
    return -1;
  }

  public boolean isSame(SimplePlace r) {
    if (!getName().equals(r.getName())) {
      return false;
    }
    if (partCount != r.getPartCount()) {
      return false;
    }
    if (isSimplePlace() != r.isSimplePlace()) {
      return false;
    }
    return true;
  }

  @Override
  public int getLastPartNumber() {
    return partNumberIncrement + partCount;
  }

  @Override
  public int getFirstPartNumber() {
    return partNumberIncrement;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SimplePlace that = (SimplePlace) o;
    return partNumberIncrement == that.partNumberIncrement && limited == that.limited && maxItemCount == that.maxItemCount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), partNumberIncrement, limited, maxItemCount);
  }

  @Override
  public int getTotalCountCellUsed() {
    int resul = 0;
    for (int i = 0; i < partCount; i++) {
      resul += getCountCellUsed(i);
    }
    return resul;
  }
}
