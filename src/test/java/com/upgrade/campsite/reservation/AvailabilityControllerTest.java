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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Sql({"/data-h2.sql"})
@AutoConfigureMockMvc
public class AvailabilityControllerTest {

  public static final MediaType APPLICATION_JSON_UTF8 =
      new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
          Charset.forName("utf8"));

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;
  
  private String buildUrl(LocalDate startDate, LocalDate endDate) {
    DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();
    String formattedStartDate = (startDate == null ? StringUtils.EMPTY : startDate.format(formatter));
    String formattedEndDate = (endDate == null ? StringUtils.EMPTY : endDate.format(formatter));
    return String.format("%s?startDate=%s&endDate=%s", AvailabilityController.AVAILABILITY_END_POINT,
        formattedStartDate, formattedEndDate);
  }

  @Test
  public void shouldWorkWithoutParameters() throws Exception {
    ArrayList<String> expectedAvailableDates = new ArrayList<>(30);
    LocalDate workingDate = LocalDate.now().plusDays(1);
    for (int i = 0; i < 30; i++) {
      expectedAvailableDates.add(workingDate.format(DateFormats.LOCAL_DATE.formatter()));
      workingDate = workingDate.plusDays(1);
    }

    String expectedJson = objectMapper.writeValueAsString(expectedAvailableDates);

    this.mockMvc.perform(get(buildUrl(null, null))).andDo(print())
        .andExpect(status().isOk()).andExpect(content().string(equalTo(expectedJson)));
  }

  @Test
  public void shouldWorkWhenHasBookings() throws Exception {
    DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();
    ArrayList<String> expectedAvailableDates = new ArrayList<>(30);
    expectedAvailableDates.add(LocalDate.now().plusDays(2).format(formatter));
    expectedAvailableDates.add(LocalDate.now().plusDays(5).format(formatter));
    expectedAvailableDates.add(LocalDate.now().plusDays(6).format(formatter));
    expectedAvailableDates.add(LocalDate.now().plusDays(7).format(formatter));
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
        .andDo(print()).andExpect(status().isCreated());

    this.mockMvc
        .perform(get(buildUrl(startDate, endDate)))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(equalTo(expectedJson)));
  }

  @Test
  public void shouldFailForMissingEndDate() throws Exception {
    LocalDate startDate = LocalDate.now();
    this.mockMvc
        .perform(get(buildUrl(startDate, null)))
        .andDo(print()).andExpect(status().isBadRequest())
        .andExpect(content().string(equalTo(AvailabilityController.DATES_REQUIRED)));
  }

  @Test
  public void shouldFailForMissingStartDate() throws Exception {
    LocalDate endDate = LocalDate.now().plusDays(5);
    this.mockMvc.perform(get(buildUrl(null, endDate)))
        .andDo(print()).andExpect(status().isBadRequest())
        .andExpect(content().string(equalTo(AvailabilityController.DATES_REQUIRED)));
  }

  @Test
  public void shouldFailForInvalidStartDate() throws Exception {
    LocalDate endDate = LocalDate.now().plusDays(20);
    this.mockMvc
        .perform(get(AvailabilityController.AVAILABILITY_END_POINT + "?startDate=1900-13-32&endDate="
            + endDate.format(DateFormats.LOCAL_DATE.formatter())))
        .andDo(print()).andExpect(status().isBadRequest())
        .andExpect(content().string(equalTo(AvailabilityController.INVALID_DATE_FORMAT)));
  }

  @Test
  public void shouldFailForStartDateInThePast() throws Exception {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    LocalDate aMonthAhead = LocalDate.now().plusDays(30);
    this.mockMvc.perform(get(buildUrl(yesterday, aMonthAhead))).andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldFailForStartDateToday() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate aMonthAhead = LocalDate.now().plusDays(30);
    this.mockMvc.perform(get(buildUrl(today, aMonthAhead))).andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldFailForStartDateAfterEndDate() throws Exception {
    LocalDate startDate = LocalDate.now().plusDays(2);
    LocalDate endDate = LocalDate.now().plusDays(1);
    this.mockMvc.perform(get(buildUrl(startDate, endDate))).andDo(print())
        .andExpect(status().isBadRequest());
  }

}
