package mycellar.placesmanagement.places;

import java.util.Objects;

public class Row {
  private final int number;
  private int columnCount;

  public Row(int number) {
    this.number = number;
  }

  public int getNumber() {
    return number;
  }

  public int getColumnCount() {
    return columnCount;
  }

  public void setColumnCount(int columnCount) {
    this.columnCount = columnCount;
  }

  @Override
  public String toString() {
    return "Row " + number + " columns: " + columnCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Row row = (Row) o;
    return number == row.number && columnCount == row.columnCount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, columnCount);
  }
}
