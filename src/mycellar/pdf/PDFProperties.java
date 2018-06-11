package mycellar.pdf;

import mycellar.core.MyCellarFields;

import java.util.LinkedList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 08/06/18
 */

public class PDFProperties {
	
	private String title;
	private int titleSize;
	private int fontSize;
	private boolean border;
	private final LinkedList<PDFColumn> column = new LinkedList<PDFColumn>();
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
		column.add(new PDFColumn(field, index, size, title));
	}
	
	public LinkedList<PDFColumn> getColumns() {
		return column;
	}

	float getColumnWidth(int i) {
		return column.get(i).getWidth();
	}
	
	public String getColumnTitle(int i) {
		return column.get(i).getTitle();
	}

	public boolean isBoldTitle() {
		return boldTitle;
	}

	private void setBoldTitle(boolean boldTitle) {
		this.boldTitle = boldTitle;
	}

	float getTotalColumnWidth() {
		int val = 0;
		for(PDFColumn c : column)
			val += c.getWidth();
		return val;
	}
}
