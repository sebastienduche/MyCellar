package mycellar.core.common.bottle;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

public enum BottleColor {
  NONE(""),
  RED(getLabel("BottleColor.red")),
  PINK(getLabel("BottleColor.pink")),
  WHITE(getLabel("BottleColor.white"));

  private final String label;

  BottleColor(String label) {
    this.label = label;
  }

  public static BottleColor getColor(String value) {
    if (value.isEmpty()) {
      return NONE;
    }
    try {
      return valueOf(value);
    } catch (IllegalArgumentException e) {
      if (value.equals(RED.label)) {
        return RED;
      } else if (value.equals(WHITE.label)) {
        return WHITE;
      } else if (value.equals(PINK.label)) {
        return PINK;
      }
    }
    return NONE;
  }

  @Override
  public String toString() {
    return label;
  }

}
