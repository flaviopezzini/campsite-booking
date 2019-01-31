package com.upgrade.campsite.availability;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.StringUtils;

@RestController
public class AvailabilityController {

  public static final String AVAILABILITY_END_POINT = "/availability";

  private AvailabilityService availabilityService;

  @Autowired
  public AvailabilityController(AvailabilityService availabilityService) {
    this.availabilityService = availabilityService;
  }

  @CrossOrigin
  @GetMapping(value = AVAILABILITY_END_POINT, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<Object> list(HttpServletRequest request,
      @RequestParam(name = "startDate", required = false) String startDatePar,
      @RequestParam(name = "endDate", required = false) String endDatePar) {
    LocalDate startDate = null;
    LocalDate endDate = null;
    boolean startDateEmpty = StringUtils.isEmpty(startDatePar);
    boolean endDateEmpty = StringUtils.isEmpty(endDatePar);
    boolean bothDatesEmpty = startDateEmpty && endDateEmpty;
    if (!bothDatesEmpty) {
      if (startDateEmpty || endDateEmpty) {
        return new ResponseEntity<>(AvailabilityErrorMessage.DATES_REQUIRED.message(),
            HttpStatus.BAD_REQUEST);
      }

      try {
        DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();
        startDate = LocalDate.parse(startDatePar, formatter);
        endDate = LocalDate.parse(endDatePar, formatter);
        LocalDate minStartDate = LocalDate.now().plusDays(1);
        if (startDate.isBefore(minStartDate)) {
          return new ResponseEntity<>(AvailabilityErrorMessage.START_DATE_PAST.message(),
              HttpStatus.BAD_REQUEST);
        }
        if (startDate.isAfter(endDate)) {
          return new ResponseEntity<>(AvailabilityErrorMessage.START_DATE_AFTER_END_DATE.message(),
              HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        return new ResponseEntity<>(AvailabilityErrorMessage.INVALID_DATE_FORMAT.message(),
            HttpStatus.BAD_REQUEST);
      }
    }

    return new ResponseEntity<>(availabilityService.findByDateRange(startDate, endDate),
        HttpStatus.OK);
  }

}
