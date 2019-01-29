package com.upgrade.campsite.booking;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.springframework.util.StringUtils;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.InvalidRecordException;
import lombok.Data;

@Data
public class BookingRequest {

  private String id;
  private String email;
  private String name;
  private String arrivalDate;
  private String departureDate;
  
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
  
  public Booking createNewBooking() throws InvalidRecordException {
    validate();
    return new Booking(null, email, name, parsedArrivalDate, parsedDepartureDate);
  }
  
  public Booking updateBooking(Booking oldBooking) throws InvalidRecordException {
    validate();
    oldBooking.setId(id);
    oldBooking.setEmail(email);
    oldBooking.setName(name);
    oldBooking.setArrivalDate(parsedArrivalDate);
    oldBooking.setDepartureDate(parsedDepartureDate);
    return oldBooking;
  }
  
}
