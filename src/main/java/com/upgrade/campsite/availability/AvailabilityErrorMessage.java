package com.upgrade.campsite.availability;

public enum AvailabilityErrorMessage {

  DATES_REQUIRED("When one date is sent, the other one is required."),
  //
  INVALID_DATE_FORMAT("Invalid date format. Dates should be in yyyy-MM-dd format."),
  //
  START_DATE_PAST("Start date has to be at least a day after today."),
  //
  START_DATE_AFTER_END_DATE("Start date cannot be after End date.");

  private String message;

  AvailabilityErrorMessage(String message) {
    this.message = message;
  }

  public String message() {
    return message;
  }

}
