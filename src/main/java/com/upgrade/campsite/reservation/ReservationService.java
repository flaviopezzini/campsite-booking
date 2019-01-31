package com.upgrade.campsite.reservation;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.upgrade.campsite.availability.AvailabilityService;
import com.upgrade.campsite.resource.Resource;
import com.upgrade.campsite.resource.ResourceService;
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

  public Reservation save(ReservationRequest record)
      throws InvalidRecordException, RecordNotFoundException, ReservationConflictException {

    record.validate();

    Reservation toSave = null;
    if (record.getId() == null) {
      Resource resource = resourceService.findById(record.getResourceId());
      toSave = record.createNewReservation(resource);
      if (!availabilityService.isDateRangeAvailableForCreation(toSave.getArrivalDate(),
          toSave.getDepartureDate())) {
        throw new ReservationConflictException(
            String.format(ReservationErrorMessage.CONFLICT.message(), record.getArrivalDate(),
                record.getDepartureDate()));
      }
    } else {
      Reservation oldReservation = findById(record.getId());
      if (oldReservation == null) {
        throw new RecordNotFoundException(
            String.format(ReservationErrorMessage.RECORD_NOT_FOUND.message(), record.getId()));
      }
      toSave = record.updateReservation(oldReservation);
      if (!availabilityService.isDateRangeAvailableForUpdate(record.getId(),
          toSave.getArrivalDate(), toSave.getDepartureDate())) {
        throw new ReservationConflictException(
            String.format(ReservationErrorMessage.CONFLICT.message(), record.getArrivalDate(),
                record.getDepartureDate()));
      }
      availabilityService.freeDates(oldReservation.getArrivalDate(),
          oldReservation.getDepartureDate());
    }
    Reservation stored = reservationRepository.save(toSave);
    availabilityService.lockDates(stored.getId(), toSave.getArrivalDate(), toSave.getDepartureDate());
    return stored;
  }

  public Reservation findById(String id) {
    Optional<Reservation> record = reservationRepository.findById(id);

    return record.isPresent() ? record.get() : null;
  }

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
