package mycellar.importer;

public class NoITunesFileException extends Exception {
  public NoITunesFileException(String message) {
    super(message);
  }

  public NoITunesFileException(String message, Exception e) {
    super(message, e);
  }
}
