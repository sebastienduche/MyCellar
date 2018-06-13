package mycellar.pdf;

import mycellar.core.MyCellarFields;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 08/06/18
 */

public class PDFColumn {

	private int index;
	private int width;
	private String title;
	private MyCellarFields field;
	
	PDFColumn(MyCellarFields field, int index, int width, String title) {
		this.index = index;
		this.width = width;
		setField(field);
		setTitle(title);
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

	private void setTitle(String title) {
		this.title = title;
	}

	public MyCellarFields getField() {
		return field;
	}

	private void setField(MyCellarFields field) {
		this.field = field;
	}
}
