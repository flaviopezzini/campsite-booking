package com.upgrade.campsite.shared;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Error model for interacting with client.
 */
@Getter
public class ErrorResponse {

  // General Error message
  private final String message;

  private final LocalDateTime timestamp;

  protected ErrorResponse(final String message) {
    this.message = message;
    this.timestamp = LocalDateTime.now();
  }

  public static ErrorResponse of(final String message) {
    return new ErrorResponse(message);
  }
}
