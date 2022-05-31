package mycellar.placesmanagement;

import java.util.LinkedList;
import java.util.Objects;

public class Part {
  private final int num;
  private final LinkedList<Row> rows;

  public Part(int num) {
    this.num = num;
    rows = new LinkedList<>();
  }

  public LinkedList<Row> getRows() {
    return rows;
  }

  public void setRows(int nb) {
    if (nb > rows.size()) {
      while (rows.size() < nb) {
        rows.add(new Row(rows.size() + 1));
      }
    } else {
      while (rows.size() > nb) {
        rows.removeLast();
      }
    }
  }

  public int getRowSize() {
    return rows.size();
  }

  public Row getRow(int n) {
    if (rows.size() <= n) {
      return null;
    }
    return rows.get(n);
  }

  public int getNum() {
    return num;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[ Part ").append(num).append('\n');
    for (Row row : rows) {
      builder.append(row).append('\n');
    }
    builder.append("]");
    return builder.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Part part = (Part) o;
    return num == part.num && Objects.equals(rows, part.rows);
  }

  @Override
  public int hashCode() {
    return Objects.hash(num, rows);
  }
}
