package mycellar.core;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

public enum BottlesStatus {
  NONE(""),
  CREATED(getLabel("BottlesStatus.Created")),
  MODIFIED(getLabel("BottlesStatus.Modified")),
  VERIFIED(getLabel("BottlesStatus.Verified")),
  TOCHECK(getLabel("BottlesStatus.ToCheck"));

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
