package com.upgrade.campsite.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import com.upgrade.campsite.reservation.ReservationRequest;

@Component
public class ReservationRequestBuilder {

  private DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();

  public ReservationRequest createMockFromDates(LocalDate arrivalDate, LocalDate departureDate) {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("mock email");
    reservationRequest.setName("mock name");
    reservationRequest.setArrivalDate(arrivalDate.format(formatter));
    reservationRequest.setDepartureDate(departureDate.format(formatter));
    return reservationRequest;
  }
}
