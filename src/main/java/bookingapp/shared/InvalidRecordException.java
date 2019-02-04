package bookingapp.shared;

public class InvalidRecordException extends Exception {
  private static final long serialVersionUID = 1L;

  public InvalidRecordException(String message) {
    super(message);
  }
}
