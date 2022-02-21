package mycellar;

public enum ProgramType {
  WINE,
  BOOK,
  MUSIC;

  static ProgramType typeOf(String value) {
    try {
      return valueOf(value);
    } catch (IllegalArgumentException e) {
      return WINE;
    }
  }
}
