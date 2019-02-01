package com.upgrade.campsite.reservation;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.upgrade.campsite.availability.AvailabilityService;
import com.upgrade.campsite.resource.Resource;
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

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Reservation save(ReservationRequest record) throws InvalidRecordException,
      RecordNotFoundException, ReservationConflictException, IncorrectResourceSetupException {
    Resource resource = resourceService.getOrDefault(record.getResourceId());
    record.setResourceId(resource.getId());

    record.validate();

    availabilityService.blockChangesByDateRange(resource.getId(), record.getParsedArrivalDate(),
        record.getParsedDepartureDate());

    if (!availabilityService.isDateRangeAvailable(resource.getId(), record.getId(),
        record.getParsedArrivalDate(), record.getParsedDepartureDate())) {
      throw new ReservationConflictException(
          String.format(ReservationErrorMessage.CONFLICT.message(), record.getArrivalDate(),
              record.getDepartureDate()));
    }

    Reservation toSave = null;
    if (record.getId() == null) {
      toSave = record.createNewReservation(resource);
    } else {
      Reservation oldReservation = findById(record.getId());
      if (oldReservation == null || !oldReservation.isActive()) {
        throw new RecordNotFoundException(
            String.format(ReservationErrorMessage.RECORD_NOT_FOUND.message(), record.getId()));
      }

      // Block dates for the previous reservation
      availabilityService.blockChangesByDateRange(resource.getId(), oldReservation.getArrivalDate(),
          oldReservation.getDepartureDate());

      toSave = record.updateReservation(oldReservation);
      availabilityService.freeDates(resource.getId(), oldReservation.getArrivalDate(),
          oldReservation.getDepartureDate());
    }
    Reservation stored = reservationRepository.save(toSave);
    availabilityService.lockDates(resource.getId(), stored.getId(), toSave.getArrivalDate(),
        toSave.getDepartureDate());
    
    return stored;
  }

  public Reservation findById(String id) {
    Optional<Reservation> record = reservationRepository.findById(id);

    return record.isPresent() ? record.get() : null;
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public void cancel(String id) throws RecordNotFoundException {
    Reservation dbReservation = findById(id);
    if (dbReservation == null || !dbReservation.isActive()) {
      throw new RecordNotFoundException("Reservation not found when trying to delete: " + id);
    } else {
      availabilityService.freeDates(dbReservation.getResource().getId(),
          dbReservation.getArrivalDate(), dbReservation.getDepartureDate());
      dbReservation.setActive(false);
      reservationRepository.save(dbReservation);
    }
  }
}
