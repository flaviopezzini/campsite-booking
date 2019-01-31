package com.upgrade.campsite.reservation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsite.shared.DateFormats;

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

  private ReservationRequest buildReservationRequest(LocalDate arrivalDate,
      LocalDate departureDate) {
    return new ReservationRequest("email", "name", arrivalDate.format(formatter),
        departureDate.format(formatter));
  }

  @Test
  public void shouldSaveWhenDatesAreFine() throws Exception {
    LocalDate arrivalDate = LocalDate.now().plusDays(3);
    LocalDate departureDate = LocalDate.now().plusDays(6);
    ReservationRequest reservationRequest = buildReservationRequest(arrivalDate, departureDate);
    String requestJson = objectMapper.writeValueAsString(reservationRequest);

    this.mockMvc.perform(post(ReservationController.REST_PREFIX).contentType(APPLICATION_JSON_UTF8)
        .content(requestJson)).andDo(print()).andExpect(status().isCreated());
  }

  @Test
  public void shouldFailSaveWhenMoreThan3Days() throws Exception {
    LocalDate arrivalDate = LocalDate.now().plusDays(3);
    LocalDate departureDate = LocalDate.now().plusDays(7);
    ReservationRequest reservationRequest = buildReservationRequest(arrivalDate, departureDate);
    String requestJson = objectMapper.writeValueAsString(reservationRequest);

    this.mockMvc.perform(post(ReservationController.REST_PREFIX).contentType(APPLICATION_JSON_UTF8)
        .content(requestJson)).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void shouldFailSaveWhenArrivalLessThanOneDayAhead() throws Exception {
    LocalDate arrivalDate = LocalDate.now();
    LocalDate departureDate = LocalDate.now().plusDays(2);
    ReservationRequest reservationRequest = buildReservationRequest(arrivalDate, departureDate);
    String requestJson = objectMapper.writeValueAsString(reservationRequest);

    this.mockMvc.perform(post(ReservationController.REST_PREFIX).contentType(APPLICATION_JSON_UTF8)
        .content(requestJson)).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void shouldFailSaveWhenArrivalMoreThanOneMonthAhead() throws Exception {
    LocalDate arrivalDate = LocalDate.now().plusDays(31);
    LocalDate departureDate = LocalDate.now().plusDays(32);
    ReservationRequest reservationRequest = buildReservationRequest(arrivalDate, departureDate);
    String requestJson = objectMapper.writeValueAsString(reservationRequest);

    this.mockMvc.perform(post(ReservationController.REST_PREFIX).contentType(APPLICATION_JSON_UTF8)
        .content(requestJson)).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void shouldFailSaveWhenDepartureBeforeArrival() throws Exception {
    LocalDate arrivalDate = LocalDate.now().plusDays(5);
    LocalDate departureDate = LocalDate.now().plusDays(3);
    ReservationRequest reservationRequest = buildReservationRequest(arrivalDate, departureDate);
    String requestJson = objectMapper.writeValueAsString(reservationRequest);

    this.mockMvc.perform(post(ReservationController.REST_PREFIX).contentType(APPLICATION_JSON_UTF8)
        .content(requestJson)).andDo(print()).andExpect(status().isBadRequest());
  }


}
