package mycellar.core.common.bottle;

import mycellar.general.ResourceKey;

import static mycellar.MyCellarUtils.isNullOrEmpty;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.BOTTLECOLOR_PINK;
import static mycellar.general.ResourceKey.BOTTLECOLOR_RED;
import static mycellar.general.ResourceKey.BOTTLECOLOR_WHITE;
import static mycellar.general.ResourceKey.EMPTY;

public enum BottleColor {
  NONE(EMPTY),
  RED(BOTTLECOLOR_RED),
  PINK(BOTTLECOLOR_PINK),
  WHITE(BOTTLECOLOR_WHITE);

  private final ResourceKey resourceKey;

  BottleColor(ResourceKey resourceKey) {
    this.resourceKey = resourceKey;
  }

  public static BottleColor getColor(String value) {
    if (value.isEmpty()) {
      return NONE;
    }
    try {
      return valueOf(value);
    } catch (IllegalArgumentException e) {
      if (value.equals(RED.resourceKey.getKey())) {
        return RED;
      } else if (value.equals(WHITE.resourceKey.getKey())) {
        return WHITE;
      } else if (value.equals(PINK.resourceKey.getKey())) {
        return PINK;
      }
    }
    return NONE;
  }

  @Override
  public String toString() {
    if (isNullOrEmpty(resourceKey.getKey())) {
      return "";
    }
    return getLabel(resourceKey);
  }

}
