package com.upgrade.campsite.booking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.ErrorResponse;
import com.upgrade.campsite.shared.InvalidRecordException;
import com.upgrade.campsite.shared.StringUtils;

@RestController
public class BookingController {

  static final String DATES_REQUIRED = "When one date is sent, the other one is required.";
  static final String INVALID_DATE_FORMAT =
      "Invalid date format. Dates should be in yyyy-MM-dd format.";
  static final String START_DATE_PAST = "Start date has to be at least a day after today.";
  protected static final String REST_PREFIX = "/bookings";
  protected static final String REST_PREFIX_ID = REST_PREFIX + "/{id}";

  private static final String JSON_FORMAT = "application/json";

  private BookingService bookingService;

  @Autowired
  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @CrossOrigin
  @GetMapping(value = REST_PREFIX, produces = JSON_FORMAT)
  public @ResponseBody ResponseEntity<Object> list(HttpServletRequest request,
      @RequestParam(name = "startDate", required = false) String startDatePar,
      @RequestParam(name = "endDate", required = false) String endDatePar) {
    LocalDate startDate;
    LocalDate endDate;
    boolean startDateEmpty = StringUtils.isEmpty(startDatePar);
    boolean endDateEmpty = StringUtils.isEmpty(endDatePar);
    if (startDateEmpty && endDateEmpty) {
      startDate = LocalDate.now().plusDays(1);
      endDate = LocalDate.now().plusDays(30);
    } else {
      if (startDateEmpty || endDateEmpty) {
        return new ResponseEntity<>(DATES_REQUIRED, HttpStatus.BAD_REQUEST);
      }

      try {
        DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();
        startDate = LocalDate.parse(startDatePar, formatter);
        endDate = LocalDate.parse(endDatePar, formatter);
        LocalDate minStartDate = LocalDate.now().plusDays(1);
        if (startDate.isBefore(minStartDate)) {
          return new ResponseEntity<>(START_DATE_PAST, HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        return new ResponseEntity<>(INVALID_DATE_FORMAT, HttpStatus.BAD_REQUEST);
      }
    }

    return new ResponseEntity<>(bookingService.findAvailabilityByDateRange(startDate, endDate),
        HttpStatus.OK);
  }

  @PostMapping(value = REST_PREFIX, produces = JSON_FORMAT)
  public @ResponseBody ResponseEntity<Object> save(HttpServletRequest request,
      HttpServletResponse response, @RequestBody BookingRequest booking) {

    try {
      Booking stored = bookingService.save(booking);
      return new ResponseEntity<>(stored, HttpStatus.CREATED);
    } catch (InvalidRecordException e) {
      return new ResponseEntity<>(ErrorResponse.of(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping(value = REST_PREFIX_ID, produces = JSON_FORMAT)
  public @ResponseBody ResponseEntity<Object> update(HttpServletRequest request,
      HttpServletResponse response, @PathVariable("id") String id,
      @RequestBody BookingRequest booking) {
    if (StringUtils.isEmpty(id)) {
      return new ResponseEntity<>(ErrorResponse.of("No ID provided."), HttpStatus.BAD_REQUEST);
    }

    try {
      Booking stored = bookingService.save(booking);
      return new ResponseEntity<>(stored, HttpStatus.OK);
    } catch (InvalidRecordException e) {
      return new ResponseEntity<>(ErrorResponse.of(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping(value = REST_PREFIX_ID, produces = JSON_FORMAT)
  public ResponseEntity<Void> delete(@PathVariable("id") String id) {
    Booking dbBooking = bookingService.findById(id);
    if (dbBooking == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      bookingService.delete(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }
}
