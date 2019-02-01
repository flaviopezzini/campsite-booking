package com.upgrade.campsite.reservation;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.upgrade.campsite.availability.AvailabilityService;
import com.upgrade.campsite.resource.Resource;
import com.upgrade.campsite.resource.ResourceErrorMessage;
import com.upgrade.campsite.resource.ResourceService;
import com.upgrade.campsite.shared.IncorrectResourceSetupException;
import com.upgrade.campsite.shared.InvalidRecordException;
import com.upgrade.campsite.shared.RecordNotFoundException;
import com.upgrade.campsite.shared.ReservationConflictException;

@Service
public class ReservationService {

  private ReservationRepository reservationRepository;
  private ResourceService resourceService;
  private AvailabilityService availabilityService;

  @Autowired
  public ReservationService(ReservationRepository reservationRepository,
      ResourceService resourceService, AvailabilityService availabilityService) {
    super();
    this.reservationRepository = reservationRepository;
    this.resourceService = resourceService;
    this.availabilityService = availabilityService;
  }

  @Transactional(isolation=Isolation.SERIALIZABLE)
  public Reservation save(ReservationRequest record)
      throws InvalidRecordException, RecordNotFoundException, ReservationConflictException,
      IncorrectResourceSetupException {
    
    // Since we currently only have one resource, we are looking it up now
    // and using it by default. If it's ever decided to have more resources we'll have
    // to add it as a parameter
    // TODO remove this block there are more than one resource
    List<Resource> resources = resourceService.findAll();
    if (resources.size() != 1) {
      throw new IncorrectResourceSetupException(ResourceErrorMessage.INCORRECT_RESOURCE_SETUP.message());
    }
    Resource resource = resources.get(0);
    record.setResourceId(resource.getId());

    record.validate();

    availabilityService.blockChangesByDateRange(record.getParsedArrivalDate(),
        record.getParsedDepartureDate());

    if (!availabilityService.isDateRangeAvailable(record.getId(), record.getParsedArrivalDate(),
        record.getParsedDepartureDate())) {
      throw new ReservationConflictException(
          String.format(ReservationErrorMessage.CONFLICT.message(), record.getArrivalDate(),
              record.getDepartureDate()));
    }
    
    Reservation toSave = null;
    if (record.getId() == null) {
      toSave = record.createNewReservation(resource);
    } else {
      Reservation oldReservation = findById(record.getId());
      if (oldReservation == null) {
        throw new RecordNotFoundException(
            String.format(ReservationErrorMessage.RECORD_NOT_FOUND.message(), record.getId()));
      }

      // Block dates for the previous reservation
      availabilityService.blockChangesByDateRange(oldReservation.getArrivalDate(),
          oldReservation.getDepartureDate());
      
      toSave = record.updateReservation(oldReservation);
      availabilityService.freeDates(oldReservation.getArrivalDate(),
          oldReservation.getDepartureDate());
    }
    Reservation stored = reservationRepository.save(toSave);
    availabilityService.lockDates(stored.getId(), toSave.getArrivalDate(),
        toSave.getDepartureDate());

    return stored;
  }

  public Reservation findById(String id) {
    Optional<Reservation> record = reservationRepository.findById(id);

    return record.isPresent() ? record.get() : null;
  }

  @Transactional(isolation=Isolation.SERIALIZABLE)
  public void delete(String id) throws RecordNotFoundException {
    Reservation dbReservation = findById(id);
    if (dbReservation == null) {
      throw new RecordNotFoundException("Reservation not found when trying to delete: " + id);
    } else {
      availabilityService.freeDates(dbReservation.getArrivalDate(),
          dbReservation.getDepartureDate());
      reservationRepository.deleteById(id);
    }
  }
}
