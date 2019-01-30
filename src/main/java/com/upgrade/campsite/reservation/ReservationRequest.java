package com.upgrade.campsite.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
    String errorMessage = null;
    if (StringUtils.isEmpty(email)) {
      errorMessage = "Email is required.";
    }
    
    if (StringUtils.isEmpty(name)) {
      errorMessage = "Name is required.";
    }
    
    if (StringUtils.isEmpty(arrivalDate)) {
      errorMessage = "Arrival Date is required.";
    }
    
    if (StringUtils.isEmpty(departureDate)) {
      errorMessage = "Departure Date is required.";
    }
    
    try {
      parsedArrivalDate = LocalDate.parse(arrivalDate, DateFormats.LOCAL_DATE.formatter());
    } catch (DateTimeParseException e) {
      errorMessage = "Arrival Date has an an invalid format";
    }
    
    try {
      parsedDepartureDate = LocalDate.parse(departureDate, DateFormats.LOCAL_DATE.formatter());
    } catch (DateTimeParseException e) {
      errorMessage = "Departure Date has an invalid format";
    }
    
    if (errorMessage != null) {
      throw new InvalidRecordException(errorMessage);
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
