package mycellar.importer;

public class NoITunesFileException extends Exception {
  private static final long serialVersionUID = -2596908182034379623L;

  public NoITunesFileException(String message) {
    super(message);
  }

  public NoITunesFileException(String message, Exception e) {
    super(message, e);
  }
}
