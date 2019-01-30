package com.upgrade.campsite.reservation;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsite.shared.DateFormats;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReservationControllerAvailabilityTest {

  public static final MediaType APPLICATION_JSON_UTF8 =
      new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
          Charset.forName("utf8"));

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void shouldWorkWithoutParameters() throws Exception {
    ArrayList<String> expectedAvailableDates = new ArrayList<>(30);
    LocalDate workingDate = LocalDate.now().plusDays(1);
    for (int i = 0; i < 30; i++) {
      expectedAvailableDates.add(workingDate.format(DateFormats.LOCAL_DATE.formatter()));
      workingDate = workingDate.plusDays(1);
    }

    String expectedJson = objectMapper.writeValueAsString(expectedAvailableDates);

    this.mockMvc.perform(get(ReservationController.AVAILABILITY_END_POINT)).andDo(print())
        .andExpect(status().isOk()).andExpect(content().string(equalTo(expectedJson)));
  }

  @Test
  public void shouldWorkWhenHasBookings() throws Exception {
    DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();
    ArrayList<String> expectedAvailableDates = new ArrayList<>(30);
    expectedAvailableDates.add(LocalDate.now().plusDays(2).format(formatter));
    expectedAvailableDates.add(LocalDate.now().plusDays(5).format(formatter));
    expectedAvailableDates.add(LocalDate.now().plusDays(6).format(formatter));
    String expectedJson = objectMapper.writeValueAsString(expectedAvailableDates);

    LocalDate startDate = LocalDate.now().plusDays(2);
    LocalDate endDate = LocalDate.now().plusDays(7);
    
    LocalDate reservationArrivalDate = LocalDate.now().plusDays(3);
    LocalDate reservationDepartureDate = LocalDate.now().plusDays(5);
    
    ReservationRequest reservationRequest = 
        new ReservationRequest("email", "name", reservationArrivalDate.format(formatter), reservationDepartureDate.format(formatter));
    
    String requestJson = objectMapper.writeValueAsString(reservationRequest);

    // Create a reservation
    this.mockMvc
        .perform(post(ReservationController.REST_PREFIX).contentType(APPLICATION_JSON_UTF8)
            .content(requestJson))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(equalTo(expectedJson)));

    this.mockMvc
        .perform(get(ReservationController.AVAILABILITY_END_POINT + "?startDate="
            + startDate.format(formatter) + "&endDate=" + endDate.format(formatter)))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(equalTo(expectedJson)));
  }

  @Test
  public void shouldFailForMissingEndDate() throws Exception {
    this.mockMvc
        .perform(get(ReservationController.AVAILABILITY_END_POINT + "?startDate=1900-13-32"))
        .andDo(print()).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().string(equalTo(ReservationController.DATES_REQUIRED)));
  }

  @Test
  public void shouldFailForMissingStartDate() throws Exception {
    this.mockMvc.perform(get(ReservationController.AVAILABILITY_END_POINT + "?endDate=1900-13-32"))
        .andDo(print()).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().string(equalTo(ReservationController.DATES_REQUIRED)));
  }

  @Test
  public void shouldFailForInvalidStartDate() throws Exception {
    LocalDate endDate = LocalDate.now().plusDays(20);
    this.mockMvc
        .perform(get(ReservationController.AVAILABILITY_END_POINT + "?startDate=1900-13-32&endDate="
            + endDate.format(DateFormats.LOCAL_DATE.formatter())))
        .andDo(print()).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().string(equalTo(ReservationController.INVALID_DATE_FORMAT)));
  }

  @Test
  public void shouldFailForStartDateInThePast() throws Exception {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    LocalDate aMonthAhead = LocalDate.now().plusDays(30);
    DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();
    String url = ReservationController.AVAILABILITY_END_POINT + "?startDate="
        + yesterday.format(formatter) + "&endDate=" + aMonthAhead.format(formatter);
    this.mockMvc.perform(get(url)).andDo(print())
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  public void shouldFailForStartDateToday() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate aMonthAhead = LocalDate.now().plusDays(30);
    DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();
    String url = ReservationController.AVAILABILITY_END_POINT + "?startDate="
        + today.format(formatter) + "&endDate=" + aMonthAhead.format(formatter);
    this.mockMvc.perform(get(url)).andDo(print())
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  public void shouldFailForStartDateAfterEndDate() throws Exception {
    LocalDate startDate = LocalDate.now().plusDays(2);
    LocalDate endDate = LocalDate.now().plusDays(1);
    DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();
    String url = ReservationController.AVAILABILITY_END_POINT + "?startDate="
        + startDate.format(formatter) + "&endDate=" + endDate.format(formatter);
    this.mockMvc.perform(get(url)).andDo(print())
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
  }

}
