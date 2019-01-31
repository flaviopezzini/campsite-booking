package com.upgrade.campsite.reservation;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.upgrade.campsite.availability.AvailabilityService;
import com.upgrade.campsite.resource.Resource;
import com.upgrade.campsite.resource.ResourceService;
import com.upgrade.campsite.shared.InvalidRecordException;
import com.upgrade.campsite.shared.RecordNotFoundException;

@Service
public class ReservationService {

  private ReservationRepository reservationRepository;
  private ResourceService resourceService;
  private AvailabilityService availabilityService;

  @Autowired
  public ReservationService(ReservationRepository reservationRepository,
      ResourceService resourceService,
      AvailabilityService availabilityService) {
    super();
    this.reservationRepository = reservationRepository;
    this.resourceService = resourceService;
    this.availabilityService = availabilityService;
  }

  public Reservation save(ReservationRequest record) throws InvalidRecordException {
    Reservation toSave = null;
    if (record.getId() == null) {
      Resource resource = resourceService.findById(record.getResourceId());
      toSave = record.createNewReservation(resource);
    } else {
      Reservation oldReservation = findById(record.getId());
      toSave = record.updateReservation(oldReservation);
      availabilityService.freeDates(oldReservation.getArrivalDate(), oldReservation.getDepartureDate());
    }
    availabilityService.lockDates(toSave.getArrivalDate(), toSave.getDepartureDate());
    return reservationRepository.save(toSave);
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
      availabilityService.freeDates(dbReservation.getArrivalDate(), dbReservation.getDepartureDate());
      reservationRepository.deleteById(id);
    }
  }
}
