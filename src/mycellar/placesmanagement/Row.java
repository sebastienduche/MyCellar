package mycellar.placesmanagement;

import java.util.Objects;

public class Row {
  private final int num;
  private int col;

  public Row(int num) {
    this.num = num;
  }

  public int getNum() {
    return num;
  }

  public int getCol() {
    return col;
  }

  public void setCol(int col) {
    this.col = col;
  }

  @Override
  public String toString() {
    return "Row " + num + " columns: " + col;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Row row = (Row) o;
    return num == row.num && col == row.col;
  }

  @Override
  public int hashCode() {
    return Objects.hash(num, col);
  }
}
