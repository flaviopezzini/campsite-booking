package bookingapp.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import bookingapp.resource.Resource;
import bookingapp.shared.DateFormats;
import bookingapp.shared.InvalidRecordException;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationRequest {

  private String id;
  @NotEmpty(message = "E-mail cannot be empty.")
  @Size(max = 40)
  private String email;
  @NotEmpty(message = "Name cannot be empty.")
  @Size(max = 40)
  private String name;
  @NotEmpty(message = "Arrival Date cannot be empty.")
  private String arrivalDate;
  @NotEmpty(message = "Departure Date cannot be empty.")
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

  public Reservation createNewReservation(Resource resource) {
    return new Reservation(null, email, name, parsedArrivalDate, parsedDepartureDate, true, resource);
  }

  public Reservation updateReservation(Reservation oldReservation) {
    return new Reservation(oldReservation.getId(), email, name, parsedArrivalDate, parsedDepartureDate, true, oldReservation.getResource());
  }
  
}
