package com.upgrade.campsite.reservation;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Sql({"/data-h2.sql"})
@AutoConfigureMockMvc
public class ReservationControllerTest {

  public static final MediaType APPLICATION_JSON_UTF8 =
      new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
          Charset.forName("utf8"));

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();

  private String performOperation(ReservationRequest reservationRequest, int expectedStatusCode,
      String expectedErrorMessage, ResultActions operation) throws Exception {

    ResultActions resultActions =
        operation.andDo(print()).andExpect(status().is(expectedStatusCode));
    if (HttpStatus.BAD_REQUEST.value() == expectedStatusCode) {
      resultActions.andExpect(content().string(equalTo(expectedErrorMessage)));
    }
    MvcResult result = resultActions.andReturn();
    return result.getResponse().getContentAsString();
  }

  private ReservationRequest buildReservationRequest(LocalDate arrivalDate,
      LocalDate departureDate) {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate(arrivalDate.format(formatter));
    reservationRequest.setDepartureDate(departureDate.format(formatter));
    return reservationRequest;
  }

  private String tryToSave(ReservationRequest reservationRequest, int expectedStatusCode,
      String expectedErrorMessage) throws Exception {
    String requestJson = objectMapper.writeValueAsString(reservationRequest);
    ResultActions operation = this.mockMvc.perform(post(ReservationController.REST_PREFIX)
        .contentType(APPLICATION_JSON_UTF8).content(requestJson));
    return performOperation(reservationRequest, expectedStatusCode, expectedErrorMessage,
        operation);
  }

  private void tryToUpdate(String idToUpdate, ReservationRequest reservationRequest,
      int expectedStatusCode, String expectedErrorMessage) throws Exception {
    String requestJson = objectMapper.writeValueAsString(reservationRequest);
    ResultActions operation = this.mockMvc
        .perform(put(String.format("%s/%s", ReservationController.REST_PREFIX, idToUpdate))
            .contentType(APPLICATION_JSON_UTF8).content(requestJson));
    performOperation(reservationRequest, expectedStatusCode, expectedErrorMessage, operation);
  }

  private void tryToUpdateWithId(ReservationRequest reservationRequest, int expectedStatusCode,
      String expectedErrorMessage) throws Exception {
    String reservationId = tryToSave(reservationRequest, expectedStatusCode, expectedErrorMessage);
    tryToUpdate(reservationId, reservationRequest, expectedStatusCode, expectedErrorMessage);
  }

  @Test
  public void shouldSaveWhenDatesAreFine() throws Exception {
    tryToSave(buildReservationRequest(LocalDate.now().plusDays(3), LocalDate.now().plusDays(6)),
        HttpStatus.CREATED.value(), null);
  }

  @Test
  public void shouldFailSaveWhenEmptyEmail() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    tryToSave(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Email"));
  }

  @Test
  public void shouldFailSaveWhenEmptyName() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    tryToSave(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Name"));
  }

  @Test
  public void shouldFailSaveWhenEmptyArrivalDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    tryToSave(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Arrival Date"));
  }

  @Test
  public void shouldFailSaveWhenEmptyDepartureDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-03-03");
    tryToSave(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Departure Date"));
  }

  @Test
  public void shouldFailSaveWhenInvalidArrivalDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-13-03");
    reservationRequest.setDepartureDate("2018-03-03");
    tryToSave(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.HAS_INVALID_FORMAT.message(), "Arrival Date"));
  }

  @Test
  public void shouldFailSaveWhenInvalidDepartureDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-03-03");
    reservationRequest.setDepartureDate("2018-13-03");
    tryToSave(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.HAS_INVALID_FORMAT.message(), "Departure Date"));
  }

  @Test
  public void shouldFailSaveWhenMoreThan3Days() throws Exception {
    tryToSave(buildReservationRequest(LocalDate.now().plusDays(3), LocalDate.now().plusDays(7)),
        HttpStatus.BAD_REQUEST.value(), ReservationErrorMessage.MAXIMUM_STAY_IS_3_DAYS.message());
  }

  @Test
  public void shouldFailSaveWhenArrivalLessThanOneDayAhead() throws Exception {
    tryToSave(buildReservationRequest(LocalDate.now(), LocalDate.now().plusDays(2)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.RESERVATION_HAS_TO_BE_A_MINIMUM_1_DAY_AHEAD.message());
  }

  @Test
  public void shouldFailSaveWhenArrivalMoreThanOneMonthAhead() throws Exception {
    tryToSave(buildReservationRequest(LocalDate.now().plusDays(31), LocalDate.now().plusDays(32)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.RESERVATION_CAN_BE_UP_TO_A_MONTH_IN_ADVANCE.message());
  }

  @Test
  public void shouldFailSaveWhenDepartureBeforeArrival() throws Exception {
    tryToSave(buildReservationRequest(LocalDate.now().plusDays(5), LocalDate.now().plusDays(3)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.DEPARTURE_DATE_HAS_TO_BE_AFTER_ARRIVAL_DATE.message());
  }

  @Test
  public void shouldUpdateWhenDatesAreFine() throws Exception {
    tryToUpdateWithId(
        buildReservationRequest(LocalDate.now().plusDays(3), LocalDate.now().plusDays(6)),
        HttpStatus.CREATED.value(), null);
  }

  @Test
  public void shouldFailUpdateWhenEmptyEmail() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    tryToUpdateWithId(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Email"));
  }

  @Test
  public void shouldFailUpdateWhenEmptyName() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    tryToUpdateWithId(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Name"));
  }

  @Test
  public void shouldFailUpdateWhenEmptyArrivalDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    tryToUpdateWithId(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Arrival Date"));
  }

  @Test
  public void shouldFailUpdateWhenEmptyDepartureDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-03-03");
    tryToUpdateWithId(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.IS_REQUIRED.message(), "Departure Date"));
  }

  @Test
  public void shouldFailUpdateWhenInvalidArrivalDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-13-03");
    reservationRequest.setDepartureDate("2018-03-03");
    tryToUpdateWithId(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.HAS_INVALID_FORMAT.message(), "Arrival Date"));
  }

  @Test
  public void shouldFailUpdateWhenInvalidDepartureDate() throws Exception {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setEmail("email");
    reservationRequest.setName("name");
    reservationRequest.setArrivalDate("2018-03-03");
    reservationRequest.setDepartureDate("2018-13-03");
    tryToUpdateWithId(reservationRequest, HttpStatus.BAD_REQUEST.value(),
        String.format(ReservationErrorMessage.HAS_INVALID_FORMAT.message(), "Departure Date"));
  }

  @Test
  public void shouldFailUpdateWhenMoreThan3Days() throws Exception {
    tryToUpdateWithId(
        buildReservationRequest(LocalDate.now().plusDays(3), LocalDate.now().plusDays(7)),
        HttpStatus.BAD_REQUEST.value(), ReservationErrorMessage.MAXIMUM_STAY_IS_3_DAYS.message());
  }

  @Test
  public void shouldFailUpdateWhenArrivalLessThanOneDayAhead() throws Exception {
    tryToUpdateWithId(buildReservationRequest(LocalDate.now(), LocalDate.now().plusDays(2)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.RESERVATION_HAS_TO_BE_A_MINIMUM_1_DAY_AHEAD.message());
  }

  @Test
  public void shouldFailUpdateWhenArrivalMoreThanOneMonthAhead() throws Exception {
    tryToUpdateWithId(
        buildReservationRequest(LocalDate.now().plusDays(31), LocalDate.now().plusDays(32)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.RESERVATION_CAN_BE_UP_TO_A_MONTH_IN_ADVANCE.message());
  }

  @Test
  public void shouldFailUpdateWhenDepartureBeforeArrival() throws Exception {
    tryToUpdateWithId(
        buildReservationRequest(LocalDate.now().plusDays(5), LocalDate.now().plusDays(3)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.DEPARTURE_DATE_HAS_TO_BE_AFTER_ARRIVAL_DATE.message());
  }

  @Test
  public void shouldFailUpdateWhenNoId() throws Exception {
    tryToUpdate(StringUtils.EMPTY,
        buildReservationRequest(LocalDate.now().plusDays(5), LocalDate.now().plusDays(3)),
        HttpStatus.BAD_REQUEST.value(), ReservationErrorMessage.NO_ID_PROVIDED.message());
  }

  @Test
  public void shouldFailUpdateWhenInvalidId() throws Exception {
    tryToUpdate("SOME INVALID ID",
        buildReservationRequest(LocalDate.now().plusDays(5), LocalDate.now().plusDays(3)),
        HttpStatus.BAD_REQUEST.value(),
        ReservationErrorMessage.DEPARTURE_DATE_HAS_TO_BE_AFTER_ARRIVAL_DATE.message());
  }

}
