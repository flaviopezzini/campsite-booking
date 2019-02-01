package com.upgrade.campsite.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.upgrade.campsite.resource.Resource;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.InvalidRecordException;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationRequest {

  private String id;
  @NotNull
  @Size(max = 40)
  private String email;
  @NotNull
  @Size(max = 40)
  private String name;
  @NotNull
  private String arrivalDate;
  @NotNull
  private String departureDate;
  private String resourceId;

  private LocalDate parsedArrivalDate;
  private LocalDate parsedDepartureDate;
  
  public void validate() throws InvalidRecordException {
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
    return new Reservation(null, email, name, parsedArrivalDate, parsedDepartureDate, true, resource);
  }

  public Reservation updateReservation(Reservation oldReservation) throws InvalidRecordException {
    return new Reservation(oldReservation.getId(), email, name, parsedArrivalDate, parsedDepartureDate, true, oldReservation.getResource());
  }
  
}
