package com.upgrade.campsite.shared;

import java.util.Date;
import lombok.Getter;

/**
 * Error model for interacting with client.
 */
@Getter
public class ErrorResponse {

  // General Error message
  private final String message;

  private final Date timestamp;

  protected ErrorResponse(final String message) {
    this.message = message;
    this.timestamp = new java.util.Date();
  }

  public static ErrorResponse of(final String message) {
    return new ErrorResponse(message);
  }
}
