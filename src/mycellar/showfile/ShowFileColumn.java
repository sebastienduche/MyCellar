package mycellar.showfile;

import mycellar.Program;
import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarObject;
import mycellar.core.common.MyCellarFields;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 16/04/21
 */

abstract class ShowFileColumn<T> {

  private final Map<Integer, T> value = new HashMap<>();
  private MyCellarFields field;
  private int width;
  private boolean editable;
  private Type type;
  private String buttonLabel;

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

  void setValue(MyCellarObject b, Object value) {
    if (value instanceof String) {
      b.setValue(field, (String) value);
      Program.setModified();
      b.updateStatus();
    }
  }

  abstract Object getDisplayValue(MyCellarObject b);

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

  public boolean execute(MyCellarObject b, int row, int column) {
    return true;
  }

  T getMapValue(MyCellarObject b) {
    if (value.containsKey(b.getId())) {
      return value.get(b.getId());
    }
    if (isCheckBox()) {
      return (T) Boolean.FALSE;
    }
    return null;
  }

  void setMapValue(IMyCellarObject b, T value) {
    this.value.put(b.getId(), value);
  }

  public enum Type {
    BUTTON,
    CHECK,
    DEFAULT
  }
}
