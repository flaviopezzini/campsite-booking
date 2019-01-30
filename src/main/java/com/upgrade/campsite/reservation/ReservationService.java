package com.upgrade.campsite.reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.upgrade.campsite.resource.Resource;
import com.upgrade.campsite.resource.ResourceService;
import com.upgrade.campsite.shared.DateFormats;
import com.upgrade.campsite.shared.InvalidRecordException;

@Service
public class ReservationService {

  private ReservationRepository reservationRepository;
  private ResourceService resourceService;

  @Autowired
  public ReservationService(ReservationRepository reservationRepository,
      ResourceService resourceService) {
    super();
    this.reservationRepository = reservationRepository;
    this.resourceService = resourceService;
  }

  public List<String> findAvailabilityByDateRange(LocalDate startDate, LocalDate endDate) {
    List<Reservation> reservations = reservationRepository.findByDateRange(startDate, endDate);
    
    List<String> availableDates = new ArrayList<>(0);
    LocalDate workingDate = startDate;
    while(workingDate.isBefore(endDate)) {
      boolean available = true;
      for (Reservation reservation : reservations) {
        if (workingDate.isAfter(reservation.getArrivalDate()) || workingDate.isBefore(reservation.getDepartureDate())) {
          available = false;
          break;
        }
      }
      if (available) {
        availableDates.add(workingDate.format(DateFormats.LOCAL_DATE.formatter()));
      }
      workingDate = workingDate.plusDays(1);
    }
    
    return availableDates;
  }

  public Reservation save(ReservationRequest record) throws InvalidRecordException {
    Reservation toSave = null;
    if (record.getId() == null) {
      Resource resource = resourceService.findById(record.getResourceId());
      toSave = record.createNewReservation(resource);
    } else {
      Reservation oldReservation = findById(record.getId());
      toSave = record.updateReservation(oldReservation);
    }
    return reservationRepository.save(toSave);
  }
  
  public Reservation findById(String id) {
    Optional<Reservation> record = reservationRepository.findById(id);

    return record.isPresent() ? record.get() : null;
  }

  public void delete(String id) {
    reservationRepository.deleteById(id);
  }
}
