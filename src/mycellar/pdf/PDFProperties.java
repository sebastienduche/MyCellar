package mycellar.pdf;

import mycellar.core.common.MyCellarFields;

import java.util.LinkedList;

/**
 * <p>Titre : Cave &agrave; vin
 * <p>Description : Votre description
 * <p>Copyright : Copyright (c) 2016
 * <p>Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 28/03/22
 */

public class PDFProperties {

  private final LinkedList<PDFColumn> columns = new LinkedList<>();
  private String title;
  private int titleSize;
  private int fontSize;
  private boolean border;
  private boolean boldTitle;

  public PDFProperties(String title, int titleSize, int fontSize, boolean border, boolean boldTitle) {
    this.title = title;
    this.titleSize = titleSize;
    this.fontSize = fontSize;
    this.border = border;
    setBoldTitle(boldTitle);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getTitleSize() {
    return titleSize;
  }

  public void setTitleSize(int titleSize) {
    this.titleSize = titleSize;
  }

  public int getFontSize() {
    return fontSize;
  }

  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }

  public boolean isBorder() {
    return border;
  }

  public void setBorder(boolean border) {
    this.border = border;
  }

  public void addColumn(MyCellarFields field, int index, int size, String title) {
    columns.add(new PDFColumn(field, index, size, title));
  }

  public LinkedList<PDFColumn> getColumns() {
    return columns;
  }

  float getColumnWidth(int i) {
    return columns.get(i).getWidth();
  }


  public boolean isBoldTitle() {
    return boldTitle;
  }

  private void setBoldTitle(boolean boldTitle) {
    this.boldTitle = boldTitle;
  }

  float getTotalColumnWidth() {
    int val = 0;
    for (PDFColumn c : columns) {
      val += c.getWidth();
    }
    return val;
  }

  PDFRow getPDFHeader() {
    PDFRow row = new PDFRow();
    for (PDFColumn column : columns) {
      row.addCell(column.getTitle());
    }
    return row;
  }
}
