package com.upgrade.campsite.shared;

public class RecordNotFoundException extends Exception {
  private static final long serialVersionUID = 1L;

  public RecordNotFoundException(String message) {
    super(message);
  }
}
