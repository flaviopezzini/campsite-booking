package bookingapp.reservation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import bookingapp.shared.IncorrectResourceSetupException;
import bookingapp.shared.InvalidRecordException;
import bookingapp.shared.RecordNotFoundException;
import bookingapp.shared.ReservationConflictException;
import bookingapp.shared.StringUtils;

@RestController
public class ReservationController {

  public static final String REST_PREFIX = "/reservations";
  public static final String REST_PREFIX_ID = REST_PREFIX + "/{id}";

  private ReservationService reservationService;

  @Autowired
  public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
  }

  @PostMapping(value = REST_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<Object> save(HttpServletRequest request,
      @Valid @RequestBody ReservationRequest reservationRequest) {

    try {
      Reservation stored = reservationService.save(reservationRequest);
      return new ResponseEntity<>(stored.getId(), HttpStatus.CREATED);
    } catch (InvalidRecordException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (ReservationConflictException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    } catch (RecordNotFoundException | IncorrectResourceSetupException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping(value = REST_PREFIX_ID, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<Object> update(HttpServletRequest request,
      @PathVariable("id") String id, @Valid @RequestBody ReservationRequest reservationRequest) {
    if (StringUtils.isEmpty(id)) {
      return new ResponseEntity<>(ReservationErrorMessage.NO_ID_PROVIDED.message(),
          HttpStatus.BAD_REQUEST);
    }
    
    reservationRequest.setId(id);

    try {
      Reservation stored = reservationService.save(reservationRequest);
      return new ResponseEntity<>(stored.getId(), HttpStatus.OK);
    } catch (InvalidRecordException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (RecordNotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (ReservationConflictException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    } catch (IncorrectResourceSetupException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping(value = REST_PREFIX_ID, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> delete(@PathVariable("id") String id) {
    try {
      reservationService.cancel(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (RecordNotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
  }
}
