package mycellar.core.exceptions;

public class UnableToOpenMyCellarFileException extends UnableToOpenFileException {

  private static final long serialVersionUID = 762089306016388275L;

  public UnableToOpenMyCellarFileException(String message) {
    super(message);
  }
}
