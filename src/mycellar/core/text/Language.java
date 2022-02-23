package mycellar.core.text;

public enum Language {
  FRENCH('F'),
  ENGLISH('U');

  private final char language;

  Language(char language) {
    this.language = language;
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

  public char getLanguage() {
    return language;
  }
}
