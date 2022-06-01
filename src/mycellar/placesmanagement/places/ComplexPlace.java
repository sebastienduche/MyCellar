package mycellar.placesmanagement.places;

import mycellar.Program;
import mycellar.core.MyCellarObject;
import mycellar.core.exceptions.MyCellarException;
import mycellar.placesmanagement.Part;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Row;

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
 * @version 0.3
 * @since 01/06/22
 */
public class ComplexPlace extends AbstractPlace {

  private int line;
  private int column;
  private MyCellarObject[][][] storage;
  private List<Part> partList = null;


  public ComplexPlace(String name, List<Part> listPart) {
    super(name);
    setPlace(listPart);
  }

  private void setPlace(List<Part> listPart) {
    column = 0;
    line = 0;
    partCount = listPart.size();
    partList = new LinkedList<>();
    for (int i = 0; i < partCount; i++) {
      Part part = new Part(listPart.get(i).getNum());
      partList.add(part);
      int rowSize = listPart.get(i).getRowSize();
      part.setRows(rowSize);
      if (rowSize > line) {
        line = rowSize;
      }
      for (int j = 0; j < rowSize; j++) {
        int colSize = listPart.get(i).getRow(j).getCol();
        part.getRow(j).setCol(colSize);
        if (colSize > column) {
          column = colSize;
        }
      }
    }

    storage = new MyCellarObject[partCount][line][column];
  }

  public LinkedList<Part> getPlace() {
    LinkedList<Part> listPart = new LinkedList<>();
    for (Part p : partList) {
      Part part = new Part(p.getNum());
      listPart.add(part);
      for (int j = 0; j < p.getRowSize(); j++) {
        part.setRows(p.getRowSize());
        part.getRow(j).setCol(p.getRow(j).getCol());
      }
    }
    return listPart;
  }

  public int getMaxColumnNumber() {
    return column;
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

  @Override
  public Optional<MyCellarObject> getObject(int num_empl, int line, int column) {
    final MyCellarObject myCellarObject = storage[num_empl][line][column];
    return Optional.ofNullable(myCellarObject);
  }

  @Override
  public void updateToStock(MyCellarObject myCellarObject) {
    int line = myCellarObject.getLigne();
    int num_empl = myCellarObject.getNumLieu();
    int column = myCellarObject.getColonne();
    storage[num_empl - 1][line - 1][column - 1] = myCellarObject;
  }

  private static void Debug(String sText) {
    Program.Debug("SimplePlace: " + sText);
  }

  @Override
  public void clearStorage(MyCellarObject myCellarObject, Place place) {
    storage[place.getPlaceNumIndex()][place.getLineIndex()][place.getColumnIndex()] = null;
  }

  public void clearStorage(Place place) {
    storage[place.getPlaceNumIndex()][place.getLineIndex()][place.getColumnIndex()] = null;
  }

  @Override
  public boolean canAddObjectAt(MyCellarObject b) {
    return canAddObjectAt(b.getNumLieu(), -1, -1);
  }

  @Override
  public boolean canAddObjectAt(int tmpNumEmpl, int tmpLine, int tmpCol) {
    if (tmpNumEmpl < 0 || tmpNumEmpl >= partCount) {
      return false;
    }
    return tmpLine >= 0 && tmpLine < getLineCountAt(tmpNumEmpl) && !(tmpCol < 0 || tmpCol >= getColumnCountAt(tmpNumEmpl, tmpLine));
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

  public int getColumnCountAt(int emplacement, int ligne) {
    if (emplacement < 0 || ligne < 0) {
      return -1;
    }
    return partList.get(emplacement).getRow(ligne).getCol();
  }

  public int getLineCountAt(int part) {
    return partList.get(part).getRowSize();
  }

  @Override
  public void resetStockage() {
    storage = new MyCellarObject[partCount][line][column];
  }

  public boolean isExistingCell(int part, int line, int column) {
    if (isInexistingNumPlace(part)) {
      return false;
    }
    if (getLineCountAt(part) <= line) {
      return false;
    }
    int nbCol = getColumnCountAt(part, line);
    return (column < nbCol);
  }


  public int getMaxColumCountAt(int part) {
    return partList.get(part).getRows().stream().mapToInt(Row::getCol).max().getAsInt();
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
    int resul = 0;
    int nb_colonne = getColumnCountAt(part, line);
    for (int i = 0; i < nb_colonne; i++) {
      if (storage[part][line][i] != null) {
        resul++;
      }
    }
    return resul;
  }

  public int getCountFreeCellFrom(int part, int line, int column) {
    int resul = 0;
    int nb_colonne = getColumnCountAt(part, line);
    for (int i = column; i < nb_colonne; i++) {
      if (storage[part][line][i] == null) {
        resul++;
      } else {
        return resul;
      }
    }
    return resul;
  }

  public int getTotalCellUsed(int part) {
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
  public int getTotalCountCellUsed() {
    int resul = 0;
    for (int i = 0; i < partCount; i++) {
      resul += getTotalCellUsed(i);
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
      numberOfObjectsPerPlace.put(i, getTotalCellUsed(i));
    }
    return numberOfObjectsPerPlace;
  }

  @Override
  public int getLastPartNumber() {
    return partCount;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ComplexPlace that = (ComplexPlace) o;
    return line == that.line && column == that.column && Objects.equals(partList, that.partList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), line, column, partList);
  }
}
