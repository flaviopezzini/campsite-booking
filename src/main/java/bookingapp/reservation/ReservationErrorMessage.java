package bookingapp.reservation;

public enum ReservationErrorMessage {

  HAS_INVALID_FORMAT("%s has an invalid format."),
  MAXIMUM_STAY_IS_3_DAYS("Maximum stay is 3 days."),
  DEPARTURE_DATE_HAS_TO_BE_AFTER_ARRIVAL_DATE("Departure Date has to be after Arrival Date."),
  RESERVATION_HAS_TO_BE_A_MINIMUM_1_DAY_AHEAD("Reservation has to be a minimum 1 day(s) ahead of arrival."),
  RESERVATION_CAN_BE_UP_TO_A_MONTH_IN_ADVANCE("Reservation can be up to a month in advance."),
  NO_ID_PROVIDED("No ID provided."),
  RECORD_NOT_FOUND("Trying ot update reservation with id: %s but the record was not found."),
  CONFLICT("The period between %s and %s is no longer available.");
  
  private String message;
  
  ReservationErrorMessage(String message) {
    this.message = message;
  }
  
  public String message() {
    return message;
  }
}
