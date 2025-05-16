package mycellar.core.exceptions;

import java.io.Serial;

public class NoITunesFileException extends Exception {
  @Serial
  private static final long serialVersionUID = -2596908182034379623L;

  public NoITunesFileException(String message) {
    super(message);
  }

  public NoITunesFileException(String message, Exception e) {
    super(message, e);
  }
}
