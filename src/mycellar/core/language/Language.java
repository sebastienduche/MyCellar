package mycellar.core.language;

public enum Language {
  FRENCH('F'),
  ENGLISH('U');

  private final char language;

  Language(char language) {
    this.language = language;
  }

  public char getLanguage() {
    return language;
  }

  public static Language getLanguage(char language) {
    if (language == ENGLISH.getLanguage()) {
      return ENGLISH;
    }
    if (language == FRENCH.getLanguage()) {
      return FRENCH;
    }
    return ENGLISH;
  }
}
