package mycellar.placesmanagement;

import mycellar.MyCellarUtils;
import mycellar.placesmanagement.places.Part;
import mycellar.placesmanagement.places.Row;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.text.MessageFormat;
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
 * @version 1.5
 * @since 07/03/25
 */

class CreerRangementTableModel extends AbstractTableModel {

  @Serial
  private static final long serialVersionUID = -933488036527447807L;
  private static final int NAME = 0;
  private static final int ROW = 1;
  private static final int COLUMN = 2;
  private final List<Column> columns = new LinkedList<>();
  private final HashMap<Integer, Integer> mapLine = new HashMap<>();
  private List<Part> parts = new LinkedList<>();
  private HashMap<Integer, Integer> mapPart = new HashMap<>();
  private boolean sameColumnNumber = false;
  private boolean modified = false;

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
      return parts.size();
    } else {
      int count = 0;
      for (Part p : parts) {
        if (p.rows().isEmpty()) {
          count++;
        } else {
          count += p.rows().size();
        }
      }
      return count;
    }
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (sameColumnNumber) {
      Part p = parts.get(row);
      if (p == null) {
        return "";
      }
      return switch (col) {
        case NAME -> MessageFormat.format(getLabel("Storage.ShelveNumber"), p.getNumberAsDisplay());
        case ROW -> p.rows().size();
        case COLUMN -> {
          if (!p.rows().isEmpty()) {
            yield p.getRowAt(0).getColumnCount();
          }
          yield ZERO;
        }
        default -> "";
      };
    } else {
      int part = mapPart.get(row);
      int line = mapLine.get(row);
      Part p = parts.get(part);
      if (p == null) {
        return "";
      }
      return switch (col) {
        case NAME ->
            MessageFormat.format(getLabel("Storage.ShelveNumber"), p.getNumberAsDisplay()) + SPACE + getLabel("Storage.NumberLines");
        case ROW -> line;
        case COLUMN -> {
          if (p.getRowAt(line - 1) != null) {
            yield p.getRowAt(line - 1).getColumnCount();
          }
          yield ZERO;
        }
        default -> "";
      };
    }
  }

  @Override
  public String getColumnName(int col) {
    if (col >= columns.size()) {
      return "";
    }
    return columns.get(col).label();
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    return col == ROW || col == COLUMN;
  }

  @Override
  public void setValueAt(Object arg0, int row, int col) {
    modified = true;
    Part p;
    if (sameColumnNumber) {
      p = parts.get(row);
    } else {
      // Get part then the line
      int part = mapPart.get(row);
      p = parts.get(part);
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
        if (nRow < p.rows().size()) {
          p.decreaseRows(nRow);
        } else if (nRow > p.rows().size()) {
          p.increaseRows(nRow);
        }
        if (!sameColumnNumber) {
          updateValues();
          fireTableDataChanged();
          fireTableStructureChanged();
        } else if (!p.rows().isEmpty()) {
          final int nCol = p.getRowAt(0).getColumnCount();
          for (Row r : p.rows()) {
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
          for (Row r : p.rows()) {
            r.setColumnCount(nCol);
          }
        } else {
          int line = mapLine.get(row);
          p.getRowAt(line - 1).setColumnCount(nCol);
        }
    }
  }

  public void setValues(List<Part> parts) {
    this.parts = parts;
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
    for (Part part : parts) {
      if (sameColumnNumber && !part.rows().isEmpty()) {
        // Set the number of columns of the first line to all others lines
        final int col = part.getRowAt(0).getColumnCount();
        for (Row r : part.rows()) {
          r.setColumnCount(col);
        }
      }
      int line = 1;
      for (@SuppressWarnings("unused") Row r : part.rows()) {
        mapPart.put(index, numPart);
        mapLine.put(index, line);
        line++;
        index++;
      }
      if (part.rows().isEmpty()) {
        mapPart.put(index, numPart);
        mapLine.put(index, line);
        part.buildRows(1);
        index++;
      }
      numPart++;
    }
  }

  public boolean isModified() {
    return modified;
  }
}

record Column(int id, String label) {
}
