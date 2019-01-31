package com.upgrade.campsite.reservation;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
import com.upgrade.campsite.resource.Resource;
import com.upgrade.campsite.resource.ResourceErrorMessage;
import com.upgrade.campsite.resource.ResourceService;
import com.upgrade.campsite.shared.InvalidRecordException;
import com.upgrade.campsite.shared.RecordNotFoundException;
import com.upgrade.campsite.shared.StringUtils;

@RestController
public class ReservationController {

  public static final String REST_PREFIX = "/reservations";
  public static final String REST_PREFIX_ID = REST_PREFIX + "/{id}";

  private ReservationService reservationService;
  private ResourceService resourceService;

  @Autowired
  public ReservationController(ReservationService reservationService,
      ResourceService resourceService) {
    this.reservationService = reservationService;
    this.resourceService = resourceService;
  }

  @PostMapping(value = REST_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<Object> save(HttpServletRequest request,
      @RequestBody ReservationRequest reservationRequest) {

    // Since we currently only have one resource, we are looking it up now
    // and using it by default. If it's ever decided to have more resources we'll have
    // to add it as a parameter
    // TODO remove this block there are more than one resource
    List<Resource> resources = resourceService.findAll();
    if (resources.size() != 1) {
      return new ResponseEntity<>(ResourceErrorMessage.INCORRECT_RESOURCE_SETUP.message(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    Resource resource = resources.get(0);
    reservationRequest.setResourceId(resource.getId());

    try {
      Reservation stored = reservationService.save(reservationRequest);
      return new ResponseEntity<>(stored.getId(), HttpStatus.CREATED);
    } catch (InvalidRecordException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (RecordNotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping(value = REST_PREFIX_ID, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<Object> update(HttpServletRequest request,
      @PathVariable("id") String id, @RequestBody ReservationRequest reservationRequest) {
    if (StringUtils.isEmpty(id)) {
      return new ResponseEntity<>(ReservationErrorMessage.NO_ID_PROVIDED.message(),
          HttpStatus.BAD_REQUEST);
    }
    
    reservationRequest.setId(id);

    try {
      Reservation stored = reservationService.save(reservationRequest);
      return new ResponseEntity<>(stored.getId(), HttpStatus.OK);
    } catch (InvalidRecordException | RecordNotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping(value = REST_PREFIX_ID, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> delete(@PathVariable("id") String id) {
    try {
      reservationService.delete(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (RecordNotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
  }
}
