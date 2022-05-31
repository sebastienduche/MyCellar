package mycellar.placesmanagement.places;

import mycellar.placesmanagement.Part;
import mycellar.placesmanagement.Row;

import java.util.LinkedList;
import java.util.List;

public class ComplexPlaceBuilder {
  private final String name;
  private List<Part> partList;
  private int nbParts;

  private boolean sameColumns;
  private int[] columnsByPart;

  private int[] linesByPart;
  private int[][] columnsByLines;

  public ComplexPlaceBuilder(String name) {
    this.name = name;
    partList = new LinkedList<>();
  }

  public ComplexPlaceBuilder nbParts(int[] values) {
    nbParts = values.length;
    linesByPart = values;
    return this;
  }

  public ComplexPlaceBuilder sameColumnsNumber(int[] values) {
    sameColumns = true;
    columnsByPart = values;
    return this;
  }

  public ComplexPlaceBuilder differentColumnsNumber() {
    sameColumns = false;
    columnsByLines = new int[nbParts][1];
    return this;
  }
  
  public ComplexPlaceBuilder withPartList(List<Part> partList) {
	  this.partList = partList;
	  return this;
  }

  public ComplexPlaceBuilder columnsNumberForPart(int part, int[] columns) throws Exception {
    if (sameColumns) {
      throw new Exception("This place has the same column number option set!");
    }
    if (part >= nbParts) {
      throw new Exception("Incorrect part number! :" + part);
    }

    if (columns.length < linesByPart[part]) {
      throw new Exception("Incorrect columns length number! :" + part);
    }
    columnsByLines[part] = columns;
    return this;
  }

  public ComplexPlace build() {
	  if(partList.isEmpty()) {
	    for (int i = 0; i < nbParts; i++) {
	      Part part = new Part(i);
	      partList.add(part);
	      part.setRows(linesByPart[i]);
	      if (sameColumns) {
	        for (Row row : part.getRows()) {
	          row.setCol(columnsByPart[i]);
	        }
	      } else {
	        for (Row row : part.getRows()) {
	          row.setCol(columnsByLines[i][row.getNum() - 1]);
	        }
	      }
	    }
    }
    return new ComplexPlace(name, partList);
  }
}
