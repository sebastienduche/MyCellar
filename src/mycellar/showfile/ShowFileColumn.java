package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.core.MyCellarFields;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 15/03/18
 */

public abstract class ShowFileColumn {

	private MyCellarFields properties;
	private int width;
	private boolean editable;
	
	ShowFileColumn(MyCellarFields properties) {
		this.properties = properties;
		width = 100;
		setEditable(true);
	}
	
	ShowFileColumn(MyCellarFields properties, int width) {
		this.properties = properties;
		this.width = width;
		setEditable(true);
	}
	
	ShowFileColumn(MyCellarFields properties, int width, boolean editable) {
		this.properties = properties;
		this.width = width;
		setEditable(editable);
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

	private void setEditable(boolean editable) {
		this.editable = editable;
	}

	abstract void setValue(Bouteille b, Object value);
	abstract Object getDisplayValue(Bouteille b);
}
