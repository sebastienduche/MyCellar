package mycellar.core.exceptions;

public class UnableToOpenFileException extends Exception {

  private static final long serialVersionUID = -2848544818450200119L;

  public UnableToOpenFileException(String message) {
    super(message);
  }
}
