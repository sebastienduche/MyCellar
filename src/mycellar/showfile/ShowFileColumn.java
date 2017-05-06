package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.core.MyCellarFields;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 06/03/16
 */

public abstract class ShowFileColumn {

	private MyCellarFields properties;
	private int width;
	private boolean editable;
	
	public ShowFileColumn(MyCellarFields properties) {
		this.properties = properties;
		this.setWidth(100);
		this.setEditable(true);
	}
	
	public ShowFileColumn(MyCellarFields properties, int width) {
		this.properties = properties;
		this.setWidth(width);
		this.setEditable(true);
	}
	
	public ShowFileColumn(MyCellarFields properties, int width, boolean editable) {
		this.properties = properties;
		this.setWidth(width);
		this.setEditable(editable);
	}
	
	public String getLabel() {
		return properties.toString();
	}

	public MyCellarFields getField() {
		return properties;
	}

	public void setField(MyCellarFields properties) {
		this.properties = properties;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	abstract void setValue(Bouteille b, Object value);
	abstract Object getValue(Bouteille b);
}
