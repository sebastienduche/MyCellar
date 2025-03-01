package mycellar.placesmanagement.places;

import java.util.LinkedList;
import java.util.Objects;

public record Part(int number, LinkedList<Row> rows) {
  public Row getRowAt(int j) {
    if (j >= rows.size()) {
      throw new RuntimeException("Row j=" + j + " doesn't exist in the list with size=" + rows.size());
    }
    return rows.get(j);
  }

  public void increaseRows(int count) {
    if (rows.size() >= count) {
      throw new RuntimeException("Number of rows " + rows.size() + " is larger than the parameter: " + count);
    }
    for (int i = rows.size(); i < count; i++) {
      rows.add(new Row(i + 1));
    }
  }

  public void decreaseRows(int count) {
    if (rows.size() <= count) {
      throw new RuntimeException("Number of rows " + rows.size() + " is smaller than the parameter: " + count);
    }

    while (rows.size() > count) {
      rows.removeLast();
    }
  }

  public void buildRows(int count) {
    if (!rows.isEmpty()) {
      throw new RuntimeException("The part has already been initialised!");
    }
    for (int j = 0; j < count; j++) {
      rows.add(new Row(j + 1));
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[Part ").append(number).append(':');
    for (Row row : rows) {
      builder.append(row).append(',');
    }
    builder.append("]");
    return builder.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Part part = (Part) o;
    return number == part.number && Objects.equals(rows, part.rows);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, rows);
  }

  public static LinkedList<Row> generateRows(int count) {
    LinkedList<Row> rows = new LinkedList<>();
    for (int j = 1; j <= count; j++) {
      rows.add(new Row(j));
    }
    return rows;
  }
}
