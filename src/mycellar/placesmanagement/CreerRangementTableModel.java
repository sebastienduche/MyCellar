package mycellar.placesmanagement;

import mycellar.MyCellarUtils;
import mycellar.placesmanagement.places.Part;
import mycellar.placesmanagement.places.Row;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static mycellar.ProgramConstants.SPACE;
import static mycellar.ProgramConstants.ZERO;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2012
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 29/04/22
 */

class CreerRangementTableModel extends AbstractTableModel {

  @Serial
  private static final long serialVersionUID = -933488036527447807L;
  private static final int NAME = 0;
  private static final int ROW = 1;
  private static final int COLUMN = 2;
  private final List<Column> columns = new LinkedList<>();
  private final HashMap<Integer, Integer> mapLine = new HashMap<>();
  private List<Part> rows = new LinkedList<>();
  private HashMap<Integer, Integer> mapPart = new HashMap<>();
  private boolean sameColumnNumber = false;

  CreerRangementTableModel() {
    columns.add(new Column(NAME, getLabel("Storage.Shelve")));
    columns.add(new Column(ROW, getLabel("Storage.NumberLines")));
    columns.add(new Column(COLUMN, getLabel("Storage.NumberColumns")));
  }

  @Override
  public int getColumnCount() {
    return columns.size();
  }

  @Override
  public int getRowCount() {
    if (sameColumnNumber) {
      return rows.size();
    } else {
      int count = 0;
      for (Part p : rows) {
        if (p.getRowSize() == 0) {
          count++;
        } else {
          count += p.getRowSize();
        }
      }
      return count;
    }
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (sameColumnNumber) {
      Part p = rows.get(row);
      if (p == null) {
        return "";
      }
      switch (col) {
        case NAME:
          return getLabel("Storage.Shelve") + SPACE + p.getNumber();
        case ROW:
          return p.getRows().size();
        case COLUMN:
          if (p.getRowSize() > 0) {
            return p.getRow(0).getColumnCount();
          }
          return ZERO;
        default:
          return "";
      }
    } else {
      int part = mapPart.get(row);
      int line = mapLine.get(row);
      Part p = rows.get(part);
      if (p == null) {
        return "";
      }
      switch (col) {
        case NAME:
          return getLabel("Storage.Shelve") + SPACE + p.getNumber() + SPACE + getLabel("Storage.NumberLines");
        case ROW:
          return line;
        case COLUMN:
          if (p.getRow(line - 1) != null) {
            return p.getRow(line - 1).getColumnCount();
          }
          return ZERO;
        default:
          return "";
      }
    }
  }

  @Override
  public String getColumnName(int col) {
    if (col >= columns.size()) {
      return "";
    }
    return columns.get(col).getLabel();
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    return col == ROW || col == COLUMN;
  }

  @Override
  public void setValueAt(Object arg0, int row, int col) {
    Part p;
    if (sameColumnNumber) {
      p = rows.get(row);
    } else {
      // Get part then the line
      int part = mapPart.get(row);
      p = rows.get(part);
    }
    if (p == null) {
      return;
    }
    switch (col) {
      case ROW:
        int nRow = MyCellarUtils.safeParseInt((String) arg0, -1);
        if (nRow == -1) {
          return;
        }
        p.setRows(nRow);
        if (!sameColumnNumber) {
          updateValues();
          fireTableDataChanged();
          fireTableStructureChanged();
        } else if (p.getRowSize() > 0) {
          final int nCol = p.getRow(0).getColumnCount();
          for (Row r : p.getRows()) {
            r.setColumnCount(nCol);
          }
        }
        return;
      case COLUMN:
        int nCol = MyCellarUtils.safeParseInt((String) arg0, -1);
        if (nCol == -1) {
          return;
        }
        if (sameColumnNumber) {
          for (Row r : p.getRows()) {
            r.setColumnCount(nCol);
          }
        } else {
          int line = mapLine.get(row);
          p.getRow(line - 1).setColumnCount(nCol);
        }
    }
  }

  public void setValues(List<Part> parts) {
    rows = parts;
    updateValues();
    fireTableDataChanged();
  }

  void setSameColumnNumber(boolean sameColumnNumber) {
    this.sameColumnNumber = sameColumnNumber;
    updateValues();
    fireTableDataChanged();
  }

  private void updateValues() {
    mapPart = new HashMap<>();
    int index = 0;
    int numPart = 0;
    for (Part part : rows) {
      if (sameColumnNumber && part.getRowSize() > 0) {
        // Set the number of columns of the first line to all others lines
        final int col = part.getRow(0).getColumnCount();
        for (Row r : part.getRows()) {
          r.setColumnCount(col);
        }
      }
      int line = 1;
      for (@SuppressWarnings("unused") Row r : part.getRows()) {
        mapPart.put(index, numPart);
        mapLine.put(index, line);
        line++;
        index++;
      }
      if (part.getRowSize() == 0) {
        mapPart.put(index, numPart);
        mapLine.put(index, line);
        part.setRows(1);
        index++;
      }
      numPart++;
    }
  }

}

class Column {
  private final int id;
  private final String label;

  Column(int id, String label) {
    this.id = id;
    this.label = label;
  }

  public int getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }
}
