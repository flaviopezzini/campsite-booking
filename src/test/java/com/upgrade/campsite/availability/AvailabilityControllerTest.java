package com.upgrade.campsite.availability;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.DeleteReservationOperation;
import com.upgrade.campsite.shared.GetAvailabilityOperation;
import com.upgrade.campsite.shared.ReservationRequestBuilder;
import com.upgrade.campsite.shared.SaveReservationOperation;
import com.upgrade.campsite.shared.UpdateReservationOperation;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Sql({"/data-h2.sql"})
@AutoConfigureMockMvc
public class AvailabilityControllerTest {

  private DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private SaveReservationOperation saveReservationOperation;
  @Autowired
  private UpdateReservationOperation updateReservationOperation;
  @Autowired
  private DeleteReservationOperation deleteReservationOperation;
  @Autowired
  private ReservationRequestBuilder reservationRequestBuilder;
  @Autowired
  private GetAvailabilityOperation getAvailabilityOperation;

  @Test
  public void shouldWorkWithoutParameters() throws Exception {
    ArrayList<String> expectedAvailableDates = new ArrayList<>(30);
    LocalDate workingDate = LocalDate.now().plusDays(1);
    for (int i = 0; i < 30; i++) {
      expectedAvailableDates.add(workingDate.format(DateFormats.LOCAL_DATE.formatter()));
      workingDate = workingDate.plusDays(1);
    }

    String expectedJson = objectMapper.writeValueAsString(expectedAvailableDates);
    LocalDate startDate = null;
    LocalDate endDate = null;
    getAvailabilityOperation.perform(mockMvc, startDate, endDate, HttpStatus.OK.value(),
        expectedJson);
  }

  @Test
  public void shouldWorkWhenHasBookings() throws Exception {
    LocalDate reservationArrivalDate = LocalDate.now().plusDays(3);
    LocalDate reservationDepartureDate = LocalDate.now().plusDays(5);

    saveReservationOperation.perform(mockMvc, reservationRequestBuilder.createMockFromDates(
        reservationArrivalDate, reservationDepartureDate), HttpStatus.CREATED.value(), null);

    ArrayList<String> expectedAvailableDates = new ArrayList<>(0);
    expectedAvailableDates.add(LocalDate.now().plusDays(2).format(formatter));
    expectedAvailableDates.add(LocalDate.now().plusDays(5).format(formatter));
    expectedAvailableDates.add(LocalDate.now().plusDays(6).format(formatter));
    expectedAvailableDates.add(LocalDate.now().plusDays(7).format(formatter));
    String expectedJson = objectMapper.writeValueAsString(expectedAvailableDates);

    getAvailabilityOperation.perform(mockMvc, LocalDate.now().plusDays(2),
        LocalDate.now().plusDays(7), HttpStatus.OK.value(), expectedJson);
  }

  @Test
  public void shouldWorkAfterSavingUpdatingDeleting() throws Exception {
    String reservation1 = saveReservationOperation.perform(mockMvc, reservationRequestBuilder
        .createMockFromDates(LocalDate.now().plusDays(3), LocalDate.now().plusDays(5)),
        HttpStatus.CREATED.value(), null);
    String reservation2 = saveReservationOperation.perform(mockMvc, reservationRequestBuilder
        .createMockFromDates(LocalDate.now().plusDays(5), LocalDate.now().plusDays(7)),
        HttpStatus.CREATED.value(), null);
    String reservation3 = saveReservationOperation.perform(mockMvc, reservationRequestBuilder
        .createMockFromDates(LocalDate.now().plusDays(8), LocalDate.now().plusDays(10)),
        HttpStatus.CREATED.value(), null);

    updateReservationOperation
        .perform(
            mockMvc, reservation1, reservationRequestBuilder
                .createMockFromDates(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4)),
            HttpStatus.OK.value(), null);
    updateReservationOperation
        .perform(
            mockMvc, reservation2, reservationRequestBuilder
                .createMockFromDates(LocalDate.now().plusDays(4), LocalDate.now().plusDays(6)),
            HttpStatus.OK.value(), null);
    updateReservationOperation.perform(
        mockMvc, reservation3, reservationRequestBuilder
            .createMockFromDates(LocalDate.now().plusDays(11), LocalDate.now().plusDays(13)),
        HttpStatus.OK.value(), null);

    deleteReservationOperation.perform(mockMvc, reservation1, HttpStatus.NO_CONTENT.value(), null);

    final int FIRST_DAY = 1;
    final int LAST_DAY = 14;
    ArrayList<String> expectedAvailableDates = new ArrayList<>(0);
    LocalDate startDate = LocalDate.now().plusDays(FIRST_DAY);
    LocalDate endDate = LocalDate.now().plusDays(LAST_DAY);
    Set<Integer> bookedDays = new HashSet<>();
    bookedDays.add(4);
    bookedDays.add(5);
    bookedDays.add(11);
    bookedDays.add(12);
    for (int i = FIRST_DAY; i <= LAST_DAY; i++) {
      if (bookedDays.contains(i)) {
        continue;
      }
      expectedAvailableDates.add(LocalDate.now().plusDays(i).format(formatter));
    }
    String expectedJson = objectMapper.writeValueAsString(expectedAvailableDates);

    getAvailabilityOperation.perform(mockMvc, startDate, endDate, HttpStatus.OK.value(),
        expectedJson);
  }

  @Test
  public void shouldFailForMissingEndDate() throws Exception {
    LocalDate startDate = LocalDate.now();
    getAvailabilityOperation.perform(mockMvc, startDate, null, HttpStatus.BAD_REQUEST.value(),
        AvailabilityErrorMessage.DATES_REQUIRED.message());
  }

  @Test
  public void shouldFailForMissingStartDate() throws Exception {
    LocalDate endDate = LocalDate.now().plusDays(5);
    getAvailabilityOperation.perform(mockMvc, null, endDate, HttpStatus.BAD_REQUEST.value(),
        AvailabilityErrorMessage.DATES_REQUIRED.message());
  }

  @Test
  public void shouldFailForInvalidStartDate() throws Exception {
    getAvailabilityOperation.perform(mockMvc, "2030-13-32", "2030-01-01",
        HttpStatus.BAD_REQUEST.value(), AvailabilityErrorMessage.INVALID_DATE_FORMAT.message());
  }

  @Test
  public void shouldFailForStartDateInThePast() throws Exception {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    LocalDate aMonthAhead = LocalDate.now().plusDays(30);
    getAvailabilityOperation.perform(mockMvc, yesterday, aMonthAhead,
        HttpStatus.BAD_REQUEST.value(), AvailabilityErrorMessage.START_DATE_PAST.message());
  }

  @Test
  public void shouldFailForStartDateToday() throws Exception {
    LocalDate today = LocalDate.now();
    LocalDate aMonthAhead = LocalDate.now().plusDays(30);
    getAvailabilityOperation.perform(mockMvc, today, aMonthAhead, HttpStatus.BAD_REQUEST.value(),
        AvailabilityErrorMessage.START_DATE_PAST.message());
  }

  @Test
  public void shouldFailForStartDateAfterEndDate() throws Exception {
    LocalDate startDate = LocalDate.now().plusDays(2);
    LocalDate endDate = LocalDate.now().plusDays(1);
    getAvailabilityOperation.perform(mockMvc, startDate, endDate, HttpStatus.BAD_REQUEST.value(),
        AvailabilityErrorMessage.START_DATE_AFTER_END_DATE.message());
  }

}
