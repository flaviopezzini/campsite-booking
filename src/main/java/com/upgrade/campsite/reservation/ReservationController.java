package com.upgrade.campsite.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
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
import com.upgrade.campsite.resource.Resource;
import com.upgrade.campsite.resource.ResourceService;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.InvalidRecordException;
import com.upgrade.campsite.shared.StringUtils;

@RestController
public class ReservationController {

  static final String NO_ID_PROVIDED = "No ID provided.";
  static final String DATES_REQUIRED = "When one date is sent, the other one is required.";
  static final String INCORRECT_RESOURCE_SETUP = "Incorrect resource setup. Please contact the site administrators.";
  static final String INVALID_DATE_FORMAT =
      "Invalid date format. Dates should be in yyyy-MM-dd format.";
  static final String START_DATE_PAST = "Start date has to be at least a day after today.";
  static final String START_DATE_AFTER_END_DATE = "Start date cannot be after End date.";
  static final String REST_PREFIX = "/reservations";
  static final String REST_PREFIX_ID = REST_PREFIX + "/{id}";
  static final String AVAILABILITY_END_POINT = "/availability";

  private static final String JSON_FORMAT = "application/json";

  private ReservationService reservationService;
  private ResourceService resourceService;

  @Autowired
  public ReservationController(ReservationService reservationService, ResourceService resourceService) {
    this.reservationService = reservationService;
    this.resourceService = resourceService;
  }

  @CrossOrigin
  @GetMapping(value = AVAILABILITY_END_POINT, produces = JSON_FORMAT)
  public @ResponseBody ResponseEntity<Object> list(HttpServletRequest request,
      @RequestParam(name = "startDate", required = false) String startDatePar,
      @RequestParam(name = "endDate", required = false) String endDatePar) {
    LocalDate startDate;
    LocalDate endDate;
    boolean startDateEmpty = StringUtils.isEmpty(startDatePar);
    boolean endDateEmpty = StringUtils.isEmpty(endDatePar);
    if (startDateEmpty && endDateEmpty) {
      startDate = LocalDate.now().plusDays(1);
      final int ONE_MONTH = 31; // Adding one to the end since the end date is not inclusive
      endDate = LocalDate.now().plusDays(ONE_MONTH);
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
        if (startDate.isAfter(endDate)) {
          return new ResponseEntity<>(START_DATE_AFTER_END_DATE, HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        return new ResponseEntity<>(INVALID_DATE_FORMAT, HttpStatus.BAD_REQUEST);
      }
    }

    return new ResponseEntity<>(reservationService.findAvailabilityByDateRange(startDate, endDate),
        HttpStatus.OK);
  }

  @PostMapping(value = REST_PREFIX, produces = JSON_FORMAT)
  public @ResponseBody ResponseEntity<Object> save(HttpServletRequest request,
      HttpServletResponse response, @RequestBody ReservationRequest reservationRequest) {
    
    // Since we currently only have one resource, we are looking it up now
    // and using it by default. If it's ever decided to have more resources we'll have
    // to add it as a parameter
    List<Resource> resources = resourceService.findAll();
    if (resources.size() != 1) {
      return new ResponseEntity<>(INCORRECT_RESOURCE_SETUP, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    Resource resource = resources.get(0);
    reservationRequest.setResourceId(resource.getId());
    
    try {
      Reservation stored = reservationService.save(reservationRequest);
      return new ResponseEntity<>(stored, HttpStatus.CREATED);
    } catch (InvalidRecordException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping(value = REST_PREFIX_ID, produces = JSON_FORMAT)
  public @ResponseBody ResponseEntity<Object> update(HttpServletRequest request,
      HttpServletResponse response, @PathVariable("id") String id,
      @RequestBody ReservationRequest reservationRequest) {
    if (StringUtils.isEmpty(id)) {
      return new ResponseEntity<>(NO_ID_PROVIDED, HttpStatus.BAD_REQUEST);
    }

    try {
      Reservation stored = reservationService.save(reservationRequest);
      return new ResponseEntity<>(stored, HttpStatus.OK);
    } catch (InvalidRecordException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping(value = REST_PREFIX_ID, produces = JSON_FORMAT)
  public ResponseEntity<Void> delete(@PathVariable("id") String id) {
    Reservation dbReservation = reservationService.findById(id);
    if (dbReservation == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      reservationService.delete(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }
}
