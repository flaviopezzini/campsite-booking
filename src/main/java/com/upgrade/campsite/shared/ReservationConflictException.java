package com.upgrade.campsite.shared;

public class ReservationConflictException extends Exception {
  private static final long serialVersionUID = 1L;

  public ReservationConflictException(String message) {
    super(message);
  }
}
