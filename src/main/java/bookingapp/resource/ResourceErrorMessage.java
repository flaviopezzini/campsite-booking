package bookingapp.resource;

public enum ResourceErrorMessage {
  INCORRECT_RESOURCE_SETUP("Incorrect resource setup. Please contact the site administrators.");

  private String message;
  
  ResourceErrorMessage(String message) {
    this.message = message;
  }
  
  public String message() {
    return message;
  }
  
}
