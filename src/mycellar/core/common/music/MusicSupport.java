package mycellar.core.common.music;

import mycellar.Program;

public enum MusicSupport {
  NONE(""),
  CD(Program.getLabel("MusicSupport.cd")),
  VINYL(Program.getLabel("MusicSupport.vinyl")),
  K7(Program.getLabel("MusicSupport.k7")),
  DIGITAL(Program.getLabel("MusicSupport.digital"));

  private final String label;

  MusicSupport(String label) {
    this.label = label;
  }

  public static MusicSupport getSupport(String value) {
    if (value == null || value.isEmpty()) {
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
