package mycellar.pdf;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 0.3
 * @since 26/09/18
 */

public class PDFRow {

  private final LinkedList<String> columns = new LinkedList<>();
  private PDFont font;
  private int fontSize;

  public PDFRow() {
  }

  public void addCell(String value) {
    columns.add(value);
  }

  List<String> getCells() {
    return columns;
  }

  int getCellCount() {
    return columns.size();
  }

  public void setFont(PDFont font, int fontSize) {
    this.font = font;
    this.fontSize = fontSize;
  }

  public PDFont getFont() {
    return font;
  }

  int getFontSize() {
    return fontSize;
  }
}
