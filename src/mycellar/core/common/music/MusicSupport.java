package mycellar.core.common.music;

import static mycellar.MyCellarUtils.isNullOrEmpty;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

public enum MusicSupport {
  NONE(""),
  CD("MusicSupport.Cd"),
  VINYL("MusicSupport.Vinyl"),
  K7("MusicSupport.K7"),
  DIGITAL("MusicSupport.Digital");

  private final String label;

  MusicSupport(String label) {
    this.label = label;
  }

  public static MusicSupport getSupport(String value) {
    if (isNullOrEmpty(value)) {
      return NONE;
    }
    try {
      return valueOf(value);
    } catch (IllegalArgumentException e) {
      if (value.equals(CD.label)) {
        return CD;
      } else if (value.equals(VINYL.label)) {
        return VINYL;
      } else if (value.equals(K7.label)) {
        return K7;
      } else if (value.equals(DIGITAL.label)) {
        return DIGITAL;
      }
    }
    return NONE;
  }

  @Override
  public String toString() {
    if (isNullOrEmpty(label)) {
      return "";
    }
    return getLabel(label);
  }

}
