package mycellar;

import mycellar.general.IResource;

import static mycellar.general.ResourceKey.PROGRAM_BOOKS;
import static mycellar.general.ResourceKey.PROGRAM_DISCS;
import static mycellar.general.ResourceKey.PROGRAM_WINES;

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

  public IResource getResource() {
   return switch (this) {
      case BOOK: yield PROGRAM_BOOKS;
      case MUSIC: yield PROGRAM_DISCS;
     case WINE: yield PROGRAM_WINES;
    };
  }
}
