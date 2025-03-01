package mycellar.placesmanagement.places;

import mycellar.Program;
import mycellar.core.MyCellarObject;
import mycellar.core.exceptions.MyCellarException;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2022
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.1
 * @since 01/03/25
 */
public class ComplexPlace extends AbstractPlace {

  private int lineCount;
  private int columnCount;
  private MyCellarObject[][][] storage;
  private List<Part> partList;


  public ComplexPlace(String name, List<Part> listPart) {
    super(name);
    setPlace(listPart);
  }

  private void setPlace(List<Part> listPart) {
    columnCount = 0;
    lineCount = 0;
    partCount = listPart.size();
    partList = new LinkedList<>();
    for (int i = 0; i < partCount; i++) {
      final Part oldPart = listPart.get(i);
      int rowSize = oldPart.rows().size();
      LinkedList<Row> rows = new LinkedList<>();
      for (int j = 0; j < rowSize; j++) {
        int colSize = oldPart.getRowAt(j).getColumnCount();
        rows.add(new Row(j + 1, colSize));
        if (colSize > columnCount) {
          columnCount = colSize;
        }
      }
      Part part = new Part(oldPart.number(), rows);
      partList.add(part);
      if (rowSize > lineCount) {
        lineCount = rowSize;
      }
//      for (int j = 0; j < rowSize; j++) {
//        int colSize = oldPart.getRowAt(j).getColumnCount();
//        part.getRowAt(j).setColumnCount(colSize);
//        if (colSize > columnCount) {
//          columnCount = colSize;
//        }
//      }
    }

    storage = new MyCellarObject[partCount][lineCount][columnCount];
  }

  public List<Part> getParts() {
    return Collections.unmodifiableList(partList);
  }

  public int getColumnCount() {
    return columnCount;
  }

  @Override
  public boolean isComplexPlace() {
    return true;
  }

  @Override
  public boolean isSimplePlace() {
    return false;
  }

  @Override
  public boolean isIncorrectNumPlace(int numPlace) {
    return numPlace < 0 || numPlace >= partCount;
  }

  @Override
  public int getCountCellUsed(int part) {
    int resul = 0;
    int nb_ligne = getLineCountAt(part);
    for (int j = 0; j < nb_ligne; j++) {
      int nb_colonne = getColumnCountAt(part, j);
      for (int i = 0; i < nb_colonne; i++) {
        if (storage[part][j][i] != null) {
          resul++;
        }
      }
    }
    return resul;
  }

  @Override
  public boolean addObject(MyCellarObject myCellarObject) {
    if (myCellarObject.hasNoStatus()) {
      myCellarObject.setCreated();
    }
    Debug("addObjectComplexPlace: " + myCellarObject.getNom() + " " + myCellarObject.getEmplacement() + " " + myCellarObject.getNumLieu() + " " + myCellarObject.getLigne() + " " + myCellarObject.getColonne());
    updateToStock(myCellarObject);
    Program.getStorage().addWine(myCellarObject);
    return true;
  }

  public Optional<MyCellarObject> getObject(PlacePosition place) {
    final MyCellarObject myCellarObject = storage[place.getPlaceNumIndex()][place.getLineIndex()][place.getColumnIndex()];
    return Optional.ofNullable(myCellarObject);
  }

  @Override
  public void updateToStock(MyCellarObject myCellarObject) {
    final PlacePosition place = myCellarObject.getPlacePosition();
    storage[place.getPlaceNumIndex()][place.getLineIndex()][place.getColumnIndex()] = myCellarObject;
  }

  private static void Debug(String sText) {
    Program.Debug("ComplexPlace: " + sText);
  }

  @Override
  public void clearStorage(MyCellarObject myCellarObject, PlacePosition place) {
    storage[place.getPlaceNumIndex()][place.getLineIndex()][place.getColumnIndex()] = null;
  }

  public void updatePlace(List<Part> listPart) {
    Debug("Updating the list of places: ");
    listPart.forEach(part -> Debug(part.toString()));
    setPlace(listPart);
    Program.setListCaveModified();
    Program.setModified();
  }

  @Override
  public boolean canAddObjectAt(PlacePosition place) {
//    return canAddObjectAt(place.getPlaceNumIndex(), place.getLineIndex(), place.getColumnIndex());
    final int placeNumIndex = place.getPlaceNumIndex();
    final int lineIndex = place.getLineIndex();
    final int columnIndex = place.getColumnIndex();
    if (placeNumIndex < 0 || placeNumIndex >= partCount) {
      return false;
    }
    return lineIndex >= 0 && lineIndex < getLineCountAt(placeNumIndex) && !(columnIndex < 0 || columnIndex >= getColumnCountAt(placeNumIndex, lineIndex));
  }

  @Override
  public String toXml() {
    StringBuilder sText = new StringBuilder();
    sText.append("<place name=\"\" IsCaisse=\"false\" NbPlace=\"")
        .append(partCount)
        .append("\">\n");
    for (int i = 0; i < partCount; i++) {
      sText.append("<internal-place NbLine=\"").append(getLineCountAt(i)).append("\">\n");
      for (int j = 0; j < getLineCountAt(i); j++) {
        sText.append("<line NbColumn=\"").append(getColumnCountAt(i, j)).append("\"/>\n");
      }
      sText.append("</internal-place>\n");
    }
    sText.append("<name><![CDATA[").append(getName()).append("]]></name></place>");
    return sText.toString();
  }

  public int getColumnCountAt(int part, int line) {
    if (part < 0 || line < 0) {
      return -1;
    }
    return partList.get(part).getRowAt(line).getColumnCount();
  }

  public int getLineCountAt(int part) {
    return partList.get(part).rows().size();
  }

  @Override
  public void resetStockage() {
    storage = new MyCellarObject[partCount][lineCount][columnCount];
  }

  public boolean isExistingCell(int part, int line, int column) {
    if (isIncorrectNumPlace(part)) {
      return false;
    }
    if (getLineCountAt(part) <= line) {
      return false;
    }
    int nbCol = getColumnCountAt(part, line);
    return (column < nbCol);
  }


  public int getMaxColumCountAt(int part) {
    return partList.get(part).rows().stream().mapToInt(Row::getColumnCount).max()
        .orElse(0);
  }

  public int getMaxColumCount() {
    int max = 0;
    for (int i = 0; i < partCount; i++) {
      int val = getMaxColumCountAt(i);
      if (val > max) {
        max = val;
      }
    }
    return max;
  }

  public int getNbCaseUseInLine(int part, int line) {
    int count = 0;
    int nb_colonne = getColumnCountAt(part, line);
    for (int i = 0; i < nb_colonne; i++) {
      if (storage[part][line][i] != null) {
        count++;
      }
    }
    return count;
  }

  public int getCountFreeCellFrom(int part, int line, int column) {
    int count = 0;
    int nb_colonne = getColumnCountAt(part, line);
    for (int i = column; i < nb_colonne; i++) {
      if (storage[part][line][i] == null) {
        count++;
      } else {
        return count;
      }
    }
    return count;
  }

  @Override
  public int getTotalCountCellUsed() {
    int resul = 0;
    for (int i = 0; i < partCount; i++) {
      resul += getCountCellUsed(i);
    }
    return resul;
  }

  public void moveToLine(MyCellarObject myCellarObject, int newLine) throws MyCellarException {
    if (!isExistingCell(myCellarObject.getNumLieu() - 1, newLine - 1, myCellarObject.getColonne() - 1)) {
      throw new MyCellarException("Unable to move this object to a new line: " + myCellarObject);
    }
    clearStorage(myCellarObject);
    myCellarObject.setLigne(newLine);
    updateToStock(myCellarObject);
  }

  public boolean isSameColumnNumber() {
    for (int i = 0; i < partCount; i++) {
      int nbCol = 0;
      for (int j = 0; j < getLineCountAt(i); j++) {
        if (nbCol == 0) {
          nbCol = getColumnCountAt(i, j);
          continue;
        }
        if (nbCol != getColumnCountAt(i, j)) {
          return false;
        }
      }
    }
    return true;
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
  public int getLastPartNumber() {
    return partCount;
  }

  @Override
  public int getFirstPartNumber() {
    return 1;
  }

  @Deprecated
  public boolean isSame(ComplexPlace r) {
    if (!getName().equals(r.getName())) {
      return false;
    }
    if (partCount != r.getPartCount()) {
      return false;
    }
    if (isSimplePlace() != r.isSimplePlace()) {
      return false;
    }
    for (int i = 0; i < partCount; i++) {
      int lignes = getLineCountAt(i);
      if (lignes != r.getLineCountAt(i)) {
        return false;
      }
      for (int j = 0; j < lignes; j++) {
        if (getColumnCountAt(i, j) != r.getColumnCountAt(i, j)) {
          return false;
        }
      }
    }
    return true;
  }

  public static LinkedList<Part> copyParts(List<Part> partList) {
    LinkedList<Part> list = new LinkedList<>();
    for (Part part : partList) {
      LinkedList<Row> rows = new LinkedList<>();
      for (int j = 0; j < part.rows().size(); j++) {
        rows.add(new Row(j + 1, part.getRowAt(j).getColumnCount()));
      }
      list.add(new Part(part.number(), rows));
    }
    return list;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ComplexPlace that = (ComplexPlace) o;
    return lineCount == that.lineCount && columnCount == that.columnCount && Objects.equals(partList, that.partList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lineCount, columnCount, partList);
  }
}
