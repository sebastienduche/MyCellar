package mycellar.core.common.music;

import mycellar.MyCellarUtils;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

public enum MusicSupport {
  NONE(""),
  CD(getLabel("MusicSupport.cd")),
  VINYL(getLabel("MusicSupport.vinyl")),
  K7(getLabel("MusicSupport.k7")),
  DIGITAL(getLabel("MusicSupport.digital"));

  private final String label;

  MusicSupport(String label) {
    this.label = label;
  }

  public static MusicSupport getSupport(String value) {
    if (MyCellarUtils.isNullOrEmpty(value)) {
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
    return label;
  }

}
