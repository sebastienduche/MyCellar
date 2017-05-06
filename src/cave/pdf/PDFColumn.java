package Cave.pdf;

import Cave.core.MyCellarFields;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 16/04/16
 */

public class PDFColumn {

	private int index;
	private int width;
	private String title;
	private MyCellarFields field;
	
	public PDFColumn(MyCellarFields field, int index, int width, String title) {
		this.index = index;
		this.width = width;
		this.setField(field);
		this.setTitle(title);
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public MyCellarFields getField() {
		return field;
	}

	public void setField(MyCellarFields field) {
		this.field = field;
	}
}
