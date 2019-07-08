package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.core.MyCellarFields;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.8
 * @since 08/07/19
 */

abstract class ShowFileColumn<T> {

	private MyCellarFields properties;
	private int width;
	private boolean editable;
	private Type type;
	private String buttonLabel;
	private final Map<Bouteille, T> value = new HashMap<>();
	
	ShowFileColumn(MyCellarFields properties) {
		this.properties = properties;
		width = 100;
		setEditable(true);
		seType(Type.DEFAULT);
	}
	
	ShowFileColumn(MyCellarFields properties, int width) {
		this.properties = properties;
		this.width = width;
		setEditable(true);
		seType(Type.DEFAULT);
	}
	
	ShowFileColumn(MyCellarFields properties, int width, boolean editable) {
		this.properties = properties;
		this.width = width;
		setEditable(editable);
		seType(Type.DEFAULT);
	}

	ShowFileColumn(int width, boolean editable, String buttonLabel) {
		this.width = width;
		this.buttonLabel = buttonLabel;
		setEditable(editable);
		seType(Type.BUTTON);
	}

	ShowFileColumn(int width, boolean editable, boolean checkbox, String checkBoxLabel) {
		this.width = width;
		setEditable(editable);
		setButtonLabel(checkBoxLabel);
		if (checkbox) {
			seType(Type.CHECK);
		} else {
			seType(Type.DEFAULT);
		}
	}
	
	public String getLabel() {
		if (!isDefault()) {
			return "";
		}
		return properties.toString();
	}

	public MyCellarFields getField() {
		if (properties == null) {
			return MyCellarFields.EMPTY;
		}
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

	public boolean isButton() {
		return type == Type.BUTTON;
	}

	public boolean isCheckBox() {
		return type == Type.CHECK;
	}

	public boolean isDefault() {
		return type == Type.DEFAULT;
	}

	public void seType(Type type) {
		this.type = type;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}

	public void setButtonLabel(String buttonLabel) {
		this.buttonLabel = buttonLabel;
	}

	public boolean execute(Bouteille b, int row, int column) {
		return true;
	}

	public T getMapValue(Bouteille b) {
		return value.get(b);
	}

	public void setMapValue(Bouteille b, T value) {
		this.value.put(b, value);
	}

	public enum Type {
		BUTTON,
		CHECK,
		DEFAULT
	}
}
