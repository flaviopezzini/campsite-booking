package com.upgrade.campsite.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.equalTo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.upgrade.campsite.shared.DateFormats;

@RunWith(SpringRunner.class)
@WebMvcTest
public class BookingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BookingService service;

  @Test
  public void shouldWorkWithoutParameters() throws Exception {
    when(service.findAvailabilityByDateRange(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(new ArrayList<>(0));
    this.mockMvc.perform(get("/bookings")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  public void shouldFailForMissingEndDate() throws Exception {
    when(service.findAvailabilityByDateRange(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(new ArrayList<>(0));
    this.mockMvc
        .perform(get("/bookings?startDate=1900-13-32"))
        .andDo(print()).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().string(equalTo(BookingController.DATES_REQUIRED)));
  }

  @Test
  public void shouldFailForMissingStartDate() throws Exception {
    when(service.findAvailabilityByDateRange(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(new ArrayList<>(0));
    this.mockMvc
        .perform(get("/bookings?endDate=1900-13-32"))
        .andDo(print()).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().string(equalTo(BookingController.DATES_REQUIRED)));
  }
  
  @Test
  public void shouldFailForInvalidStartDate() throws Exception {
    when(service.findAvailabilityByDateRange(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(new ArrayList<>(0));
    LocalDate endDate = LocalDate.now().plusDays(20);
    this.mockMvc
        .perform(get("/bookings?startDate=1900-13-32&endDate="
            + endDate.format(DateFormats.LOCAL_DATE.formatter())))
        .andDo(print()).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().string(equalTo(BookingController.INVALID_DATE_FORMAT)));
  }

  @Test
  public void shouldFailForStartDateInThePast() throws Exception {
    when(service.findAvailabilityByDateRange(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(new ArrayList<>(0));
    LocalDate yesterday = LocalDate.now().minusDays(1);
    LocalDate aMonthAhead = LocalDate.now().plusDays(30);
    DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();
    String url = "/bookings?startDate=" + yesterday.format(formatter) + "&endDate="
        + aMonthAhead.format(formatter);
    this.mockMvc.perform(get(url)).andDo(print())
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  public void shouldFailForStartDateToday() throws Exception {
    when(service.findAvailabilityByDateRange(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(new ArrayList<>(0));
    LocalDate today = LocalDate.now();
    LocalDate aMonthAhead = LocalDate.now().plusDays(30);
    DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();
    String url = "/bookings?startDate=" + today.format(formatter) + "&endDate="
        + aMonthAhead.format(formatter);
    this.mockMvc.perform(get(url)).andDo(print())
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
  }
}
