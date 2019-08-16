package mycellar.showfile;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.MyCellarFields;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.1
 * @since 09/08/19
 */

abstract class ShowFileColumn<T> {

	private MyCellarFields field;
	private int width;
	private boolean editable;
	private Type type;
	private String buttonLabel;
	private final Map<Integer, T> value = new HashMap<>();
	
	ShowFileColumn(MyCellarFields field) {
		this.field = field;
		width = 100;
		setEditable(true);
		seType(Type.DEFAULT);
	}
	
	ShowFileColumn(MyCellarFields field, int width) {
		this.field = field;
		this.width = width;
		setEditable(true);
		seType(Type.DEFAULT);
	}
	
	ShowFileColumn(MyCellarFields field, int width, boolean editable) {
		this.field = field;
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
	
	String getLabel() {
		if (!isDefault()) {
			return "";
		}
		return field.toString();
	}

	MyCellarFields getField() {
		if (field == null) {
			return MyCellarFields.EMPTY;
		}
		return field;
	}

	void setField(MyCellarFields field) {
		this.field = field;
	}

	int getWidth() {
		return width;
	}

	void setWidth(int width) {
		this.width = width;
	}

	boolean isEditable() {
		return editable;
	}

	private void setEditable(boolean editable) {
		this.editable = editable;
	}

	void setValue(Bouteille b, Object value) {
		if (value instanceof String) {
			b.setValue(field, (String) value);
			Program.setModified();
			b.updateStatus();
		}
	}
	abstract Object getDisplayValue(Bouteille b);

	boolean isButton() {
		return type == Type.BUTTON;
	}

	boolean isCheckBox() {
		return type == Type.CHECK;
	}

	boolean isDefault() {
		return type == Type.DEFAULT;
	}

	private void seType(Type type) {
		this.type = type;
	}

	String getButtonLabel() {
		return buttonLabel;
	}

	private void setButtonLabel(String buttonLabel) {
		this.buttonLabel = buttonLabel;
	}

	public boolean execute(Bouteille b, int row, int column) {
		return true;
	}

	T getMapValue(Bouteille b) {
		if (value.containsKey(b.getId())) {
			return value.get(b.getId());
		}
		if (isCheckBox()) {
			return (T) Boolean.FALSE;
		}
		return null;
	}

	void setMapValue(Bouteille b, T value) {
		this.value.put(b.getId(), value);
	}

	public enum Type {
		BUTTON,
		CHECK,
		DEFAULT
	}
}
