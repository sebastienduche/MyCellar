package mycellar.showfile;

import mycellar.Program;
import mycellar.core.IMyCellarObject;
import mycellar.core.common.MyCellarFields;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.5
 * @since 21/03/25
 */

abstract class ShowFileColumn<T> {

  private final Map<Integer, T> value = new HashMap<>();
  private final int width;
  private final boolean editable;
  private T defaultValue = null;
  private MyCellarFields field;
  private Type type;
  private String buttonLabel;

  ShowFileColumn(MyCellarFields field) {
    this.field = field;
    width = 100;
    editable = true;
    seType(Type.DEFAULT);
  }

  ShowFileColumn(MyCellarFields field, int width) {
    this.field = field;
    this.width = width;
    editable = true;
    seType(Type.DEFAULT);
  }

  ShowFileColumn(MyCellarFields field, int width, boolean editable) {
    this.field = field;
    this.width = width;
    this.editable = editable;
    seType(Type.DEFAULT);
  }

  ShowFileColumn(int width, boolean editable, String buttonLabel) {
    this.width = width;
    this.buttonLabel = buttonLabel;
    this.editable = editable;
    seType(Type.BUTTON);
  }

  ShowFileColumn(int width, boolean editable, boolean checkbox, String checkBoxLabel, T defaultValue) {
    this.width = width;
    this.editable = editable;
    buttonLabel = checkBoxLabel;
    if (checkbox) {
      seType(Type.CHECK);
      this.defaultValue = defaultValue;
    } else {
      seType(Type.DEFAULT);
    }
  }

  String getColumnName() {
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

  int getWidth() {
    return width;
  }

  boolean isEditable() {
    return editable;
  }

  abstract void setValue(IMyCellarObject b, T value);
  abstract Object getDisplayValue(IMyCellarObject b);

  void setModelValue(IMyCellarObject myCellarObject, Object value) {
    setValue(myCellarObject, (T) value);
  }

  void setStringValue(IMyCellarObject b, String value) {
      b.setValue(field, value);
      Program.setModified();
      b.updateStatus();
  }

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

  public boolean execute(IMyCellarObject b, int row, int column) {
    return true;
  }

  T getMapValue(IMyCellarObject b) {
    if (value.containsKey(b.getId())) {
      return value.get(b.getId());
    }
    if (isCheckBox()) {
      return defaultValue;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ShowFileColumn<?> that = (ShowFileColumn<?>) o;
    return field == that.field && type == that.type && Objects.equals(buttonLabel, that.buttonLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, type, buttonLabel);
  }

  @Override
  public String toString() {
    return "ShowFileColumn{" +
        "field=" + field +
        ", type=" + type +
        ", buttonLabel='" + buttonLabel + '\'' +
        '}';
  }
}
