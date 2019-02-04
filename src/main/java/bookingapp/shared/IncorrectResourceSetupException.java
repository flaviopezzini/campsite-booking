package bookingapp.shared;

public class IncorrectResourceSetupException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public IncorrectResourceSetupException(String message) {
    super(message);
  }
}
