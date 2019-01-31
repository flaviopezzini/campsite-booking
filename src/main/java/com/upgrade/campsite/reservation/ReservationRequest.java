package com.upgrade.campsite.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import org.springframework.util.StringUtils;
import com.upgrade.campsite.resource.Resource;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.InvalidRecordException;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationRequest {

  private String id;
  private String email;
  private String name;
  private String arrivalDate;
  private String departureDate;
  private String resourceId;

  private LocalDate parsedArrivalDate;
  private LocalDate parsedDepartureDate;

  private void validate() throws InvalidRecordException {
    if (StringUtils.isEmpty(email)) {
      throw new InvalidRecordException(
          String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Email"));
    }

    if (StringUtils.isEmpty(name)) {
      throw new InvalidRecordException(
          String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Name"));
    }

    if (StringUtils.isEmpty(arrivalDate)) {
      throw new InvalidRecordException(
          String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Arrival Date"));
    }

    if (StringUtils.isEmpty(departureDate)) {
      throw new InvalidRecordException(
          String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Departure Date"));
    }

    try {
      parsedArrivalDate = LocalDate.parse(arrivalDate, DateFormats.LOCAL_DATE.formatter());
    } catch (DateTimeParseException e) {
      throw new InvalidRecordException(
          String.format(ReservationErrorMessage.HAS_INVALID_FORMAT.message(), "Arrival Date"));
    }

    try {
      parsedDepartureDate = LocalDate.parse(departureDate, DateFormats.LOCAL_DATE.formatter());
    } catch (DateTimeParseException e) {
      throw new InvalidRecordException(
          String.format(ReservationErrorMessage.HAS_INVALID_FORMAT.message(), "Departure Date"));
    }

    long daysBetweenArrivalAndDeparture =
        ChronoUnit.DAYS.between(parsedArrivalDate, parsedDepartureDate);
    if (daysBetweenArrivalAndDeparture > 3L) {
      throw new InvalidRecordException(ReservationErrorMessage.MAXIMUM_STAY_IS_3_DAYS.message());
    } else if (daysBetweenArrivalAndDeparture < 1L) {
      throw new InvalidRecordException(
          ReservationErrorMessage.DEPARTURE_DATE_HAS_TO_BE_AFTER_ARRIVAL_DATE.message());
    }

    LocalDate today = LocalDate.now();
    long daysBetweenArrivalAndToday = ChronoUnit.DAYS.between(today, parsedArrivalDate);
    if (daysBetweenArrivalAndToday < 1L) {
      throw new InvalidRecordException(
          ReservationErrorMessage.RESERVATION_HAS_TO_BE_A_MINIMUM_1_DAY_AHEAD.message());
    } else if (daysBetweenArrivalAndToday > 30L) {
      throw new InvalidRecordException(
          ReservationErrorMessage.RESERVATION_CAN_BE_UP_TO_A_MONTH_IN_ADVANCE.message());
    }

  }

  public Reservation createNewReservation(Resource resource) throws InvalidRecordException {
    validate();
    return new Reservation(null, email, name, parsedArrivalDate, parsedDepartureDate, resource);
  }

  public Reservation updateReservation(Reservation oldReservation) throws InvalidRecordException {
    validate();
    oldReservation.setId(id);
    oldReservation.setEmail(email);
    oldReservation.setName(name);
    oldReservation.setArrivalDate(parsedArrivalDate);
    oldReservation.setDepartureDate(parsedDepartureDate);
    return oldReservation;
  }

}
