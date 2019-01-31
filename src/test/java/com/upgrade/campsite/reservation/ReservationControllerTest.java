package com.upgrade.campsite.reservation;

import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.upgrade.campsite.shared.DeleteReservationOperation;
import com.upgrade.campsite.shared.ReservationRequestBuilder;
import com.upgrade.campsite.shared.SaveReservationOperation;
import com.upgrade.campsite.shared.UpdateReservationOperation;
import com.upgrade.campsite.shared.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Sql({"/data-h2.sql"})
@AutoConfigureMockMvc
public class ReservationControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private SaveReservationOperation saveReservationOperation;
  @Autowired
  private UpdateReservationOperation updateReservationOperation;
  @Autowired
  private DeleteReservationOperation deleteReservationOperation;
  @Autowired
  private ReservationRequestBuilder reservationRequestBuilder;

  @Test
  public void shouldSaveWhenDatesAreFine() throws Exception {
    saveReservationOperation.perform(mockMvc, reservationRequestBuilder
        .createMockFromDates(LocalDate.now().plusDays(3), LocalDate.now().plusDays(6)),
        HttpStatus.CREATED.value(), null);
  }

  @Test
  public void shouldFailSaveWhenEmptyEmail() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    saveReservationOperation.perform(mockMvc, reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Email"));
  }

  @Test
  public void shouldFailSaveWhenEmptyName() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    saveReservationOperation.perform(mockMvc, reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Name"));
  }

  @Test
  public void shouldFailSaveWhenEmptyArrivalDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    saveReservationOperation.perform(mockMvc, reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Arrival Date"));
  }

  @Test
  public void shouldFailSaveWhenEmptyDepartureDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-03-03");
    saveReservationOperation.perform(mockMvc, reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Departure Date"));
  }

  @Test
  public void shouldFailSaveWhenInvalidArrivalDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-13-03");
    reservationRequest.setDepartureDate("2018-03-03");
    saveReservationOperation.perform(mockMvc, reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.HAS_INVALID_FORMAT.message(), "Arrival Date"));
  }

  @Test
  public void shouldFailSaveWhenInvalidDepartureDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-03-03");
    reservationRequest.setDepartureDate("2018-13-03");
    saveReservationOperation.perform(mockMvc, reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.HAS_INVALID_FORMAT.message(), "Departure Date"));
  }

  @Test
  public void shouldFailSaveWhenMoreThan3Days() throws Exception {
    saveReservationOperation.perform(mockMvc,
        reservationRequestBuilder.createMockFromDates(LocalDate.now().plusDays(3),
            LocalDate.now().plusDays(7)),
        HttpStatus.BAD_REQUEST.value(), ReservationErrorMessage.MAXIMUM_STAY_IS_3_DAYS.message());
  }

  @Test
  public void shouldFailSaveWhenArrivalLessThanOneDayAhead() throws Exception {
    saveReservationOperation.perform(mockMvc,
        reservationRequestBuilder.createMockFromDates(LocalDate.now(), LocalDate.now().plusDays(2)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.RESERVATION_HAS_TO_BE_A_MINIMUM_1_DAY_AHEAD.message());
  }

  @Test
  public void shouldFailSaveWhenArrivalMoreThanOneMonthAhead() throws Exception {
    saveReservationOperation.perform(mockMvc,
        reservationRequestBuilder.createMockFromDates(LocalDate.now().plusDays(31),
            LocalDate.now().plusDays(32)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.RESERVATION_CAN_BE_UP_TO_A_MONTH_IN_ADVANCE.message());
  }

  @Test
  public void shouldFailSaveWhenDepartureBeforeArrival() throws Exception {
    saveReservationOperation.perform(mockMvc,
        reservationRequestBuilder.createMockFromDates(LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(3)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.DEPARTURE_DATE_HAS_TO_BE_AFTER_ARRIVAL_DATE.message());
  }

  @Test
  public void shouldUpdateWhenDatesAreFine() throws Exception {
    updateReservationOperation.performWithId(mockMvc, reservationRequestBuilder.createMockFromDates(
        LocalDate.now().plusDays(3), LocalDate.now().plusDays(6)), HttpStatus.OK.value(), null);
  }

  @Test
  public void shouldFailUpdateWhenEmptyEmail() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    updateReservationOperation.performWithId(mockMvc, reservationRequest,
        HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Email"));
  }

  @Test
  public void shouldFailUpdateWhenEmptyName() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    updateReservationOperation.performWithId(mockMvc, reservationRequest,
        HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Name"));
  }

  @Test
  public void shouldFailUpdateWhenEmptyArrivalDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    updateReservationOperation.performWithId(mockMvc, reservationRequest,
        HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Arrival Date"));
  }

  @Test
  public void shouldFailUpdateWhenEmptyDepartureDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-03-03");
    updateReservationOperation.performWithId(mockMvc, reservationRequest,
        HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Departure Date"));
  }

  @Test
  public void shouldFailUpdateWhenInvalidArrivalDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-13-03");
    reservationRequest.setDepartureDate("2018-03-03");
    updateReservationOperation.performWithId(mockMvc, reservationRequest,
        HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.HAS_INVALID_FORMAT.message(), "Arrival Date"));
  }

  @Test
  public void shouldFailUpdateWhenInvalidDepartureDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-03-03");
    reservationRequest.setDepartureDate("2018-13-03");
    updateReservationOperation.performWithId(mockMvc, reservationRequest,
        HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.HAS_INVALID_FORMAT.message(), "Departure Date"));
  }

  @Test
  public void shouldFailUpdateWhenMoreThan3Days() throws Exception {
    updateReservationOperation.performWithId(mockMvc,
        reservationRequestBuilder.createMockFromDates(LocalDate.now().plusDays(3),
            LocalDate.now().plusDays(7)),
        HttpStatus.BAD_REQUEST.value(), ReservationErrorMessage.MAXIMUM_STAY_IS_3_DAYS.message());
  }

  @Test
  public void shouldFailUpdateWhenArrivalLessThanOneDayAhead() throws Exception {
    updateReservationOperation.performWithId(mockMvc,
        reservationRequestBuilder.createMockFromDates(LocalDate.now(), LocalDate.now().plusDays(2)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.RESERVATION_HAS_TO_BE_A_MINIMUM_1_DAY_AHEAD.message());
  }

  @Test
  public void shouldFailUpdateWhenArrivalMoreThanOneMonthAhead() throws Exception {
    updateReservationOperation.performWithId(mockMvc,
        reservationRequestBuilder.createMockFromDates(LocalDate.now().plusDays(31),
            LocalDate.now().plusDays(32)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.RESERVATION_CAN_BE_UP_TO_A_MONTH_IN_ADVANCE.message());
  }

  @Test
  public void shouldFailUpdateWhenDepartureBeforeArrival() throws Exception {
    updateReservationOperation.performWithId(mockMvc,
        reservationRequestBuilder.createMockFromDates(LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(3)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.DEPARTURE_DATE_HAS_TO_BE_AFTER_ARRIVAL_DATE.message());
  }

  @Test
  public void shouldFailUpdateWhenNoIdInPath() throws Exception {
    ReservationRequest reservationRequest = reservationRequestBuilder
        .createMockFromDates(LocalDate.now().plusDays(3), LocalDate.now().plusDays(5));
    reservationRequest.setId(StringUtils.EMPTY);
    updateReservationOperation.perform(mockMvc, StringUtils.EMPTY, reservationRequest,
        HttpStatus.METHOD_NOT_ALLOWED.value(), null);
  }

  @Test
  public void shouldFailUpdateWhenInvalidId() throws Exception {
    ReservationRequest reservationRequest = reservationRequestBuilder
        .createMockFromDates(LocalDate.now().plusDays(3), LocalDate.now().plusDays(5));
    final String INVALID_ID = "SOME INVALID ID";
    reservationRequest.setId(INVALID_ID);
    updateReservationOperation.perform(mockMvc, INVALID_ID, reservationRequest,
        HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.RECORD_NOT_FOUND.message(), INVALID_ID));
  }

  @Test
  public void shouldWorkDeleteWhenIdIsFine() throws Exception {
    String reservationId = saveReservationOperation.perform(mockMvc, reservationRequestBuilder
        .createMockFromDates(LocalDate.now().plusDays(3), LocalDate.now().plusDays(6)),
        HttpStatus.CREATED.value(), null);

    deleteReservationOperation.perform(mockMvc, reservationId, HttpStatus.NO_CONTENT.value(), null);
  }

  @Test
  public void shouldFailDeleteWhenNoId() throws Exception {
    deleteReservationOperation.perform(mockMvc, StringUtils.EMPTY,
        HttpStatus.METHOD_NOT_ALLOWED.value(), null);
  }

  @Test
  public void shouldFailDeleteWhenInvalidId() throws Exception {
    deleteReservationOperation.perform(mockMvc, "INVALID ID", HttpStatus.NOT_FOUND.value(), null);
  }

}
