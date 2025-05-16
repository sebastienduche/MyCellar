package mycellar.core;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.BOTTLESSTATUS_CREATED;
import static mycellar.general.ResourceKey.BOTTLESSTATUS_MODIFIED;
import static mycellar.general.ResourceKey.BOTTLESSTATUS_TOCHECK;
import static mycellar.general.ResourceKey.BOTTLESSTATUS_VERIFIED;

public enum BottlesStatus {
  NONE(""),
  CREATED(getLabel(BOTTLESSTATUS_CREATED)),
  MODIFIED(getLabel(BOTTLESSTATUS_MODIFIED)),
  VERIFIED(getLabel(BOTTLESSTATUS_VERIFIED)),
  TOCHECK(getLabel(BOTTLESSTATUS_TOCHECK));

  private final String label;

  BottlesStatus(String label) {
    this.label = label;
  }

  public static BottlesStatus getStatus(String value) {
    if (value.isEmpty()) {
      return NONE;
    }
    try {
      return valueOf(value);
    } catch (IllegalArgumentException e) {
      if (value.equals(CREATED.label)) {
        return CREATED;
      } else if (value.equals(MODIFIED.label)) {
        return MODIFIED;
      } else if (value.equals(VERIFIED.label)) {
        return VERIFIED;
      } else if (value.equals(TOCHECK.label)) {
        return TOCHECK;
      }
    }
    return NONE;
  }

  @Override
  public String toString() {
    return label;
  }

}
