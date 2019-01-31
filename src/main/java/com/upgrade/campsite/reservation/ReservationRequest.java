package com.upgrade.campsite.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import org.springframework.util.StringUtils;
import com.upgrade.campsite.resource.Resource;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.InvalidRecordException;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ReservationRequest {

  private String id;
  @NonNull
  private String email;
  @NonNull
  private String name;
  @NonNull
  private String arrivalDate;
  @NonNull
  private String departureDate;
  private String resourceId;

  private LocalDate parsedArrivalDate;
  private LocalDate parsedDepartureDate;

  private void validate() throws InvalidRecordException {
    if (StringUtils.isEmpty(email)) {
      throw new InvalidRecordException("Email is required.");
    }

    if (StringUtils.isEmpty(name)) {
      throw new InvalidRecordException("Name is required.");
    }

    if (StringUtils.isEmpty(arrivalDate)) {
      throw new InvalidRecordException("Arrival Date is required.");
    }

    if (StringUtils.isEmpty(departureDate)) {
      throw new InvalidRecordException("Departure Date is required.");
    }

    try {
      parsedArrivalDate = LocalDate.parse(arrivalDate, DateFormats.LOCAL_DATE.formatter());
    } catch (DateTimeParseException e) {
      throw new InvalidRecordException("Arrival Date has an an invalid format.");
    }

    try {
      parsedDepartureDate = LocalDate.parse(departureDate, DateFormats.LOCAL_DATE.formatter());
    } catch (DateTimeParseException e) {
      throw new InvalidRecordException("Departure Date has an invalid format.");
    }

    long daysBetweenArrivalAndDeparture = ChronoUnit.DAYS.between(parsedArrivalDate, parsedDepartureDate);
    if (daysBetweenArrivalAndDeparture > 3L) {
      throw new InvalidRecordException("Maximum stay is 3 days.");
    } else if (daysBetweenArrivalAndDeparture < 1L) {
      throw new InvalidRecordException("Departure Date has to be after Arrival Date.");
    }
    
    LocalDate today = LocalDate.now();
    long daysBetweenArrivalAndToday = ChronoUnit.DAYS.between(today, parsedArrivalDate);
    if (daysBetweenArrivalAndToday < 1L) {
      throw new InvalidRecordException("Reservation has to be a minimum 1 day(s) ahead of arrival.");
    } else if (daysBetweenArrivalAndToday > 30L) {
      throw new InvalidRecordException("Reservation has to be up to a month in advance.");
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
