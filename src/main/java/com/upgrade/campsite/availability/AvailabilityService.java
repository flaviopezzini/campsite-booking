package com.upgrade.campsite.availability;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {

  private AvailabilityRepository availabilityRepository;

  @Autowired
  public AvailabilityService(AvailabilityRepository resourceRepository) {
    super();
    this.availabilityRepository = resourceRepository;
  }

  public Availability findById(LocalDate id) {
    Optional<Availability> record = availabilityRepository.findById(id);

    return record.isPresent() ? record.get() : null;
  }
  
  public Availability save(Availability record) {
    return availabilityRepository.save(record);
  }
  
  public List<LocalDate> findByDateRange(LocalDate startDate, LocalDate endDate) {
    // Using default values if both dates come null
    if (startDate == null) {
      startDate = LocalDate.now().plusDays(1);
    }
    if (endDate == null) {
      final int ONE_MONTH = 31; // Adding one to the end since the end date is not inclusive
      endDate = LocalDate.now().plusDays(ONE_MONTH);
    }
    
    return availabilityRepository.findByDateRange(startDate, endDate);
  }
  
  /**
   * Blocks the records for these dates to ensure other threads can't make changes to it.
   * @param startDate
   * @param endDate
   * @return
   */
  public List<Availability> blockChangesByDateRange(LocalDate startDate, LocalDate endDate) {
    return availabilityRepository.blockChangesByDateRange(startDate, endDate);
  }
  
  public List<Availability> findAll() {
    return availabilityRepository.findAll();
  }
  
  public boolean isDateRangeAvailable(String oldReservationId, LocalDate startDate, LocalDate endDate) {
    List<String> reservationIdListFromUnavailable = availabilityRepository.findUnavailableByDateRange(startDate, endDate);
    
    if (oldReservationId != null && !reservationIdListFromUnavailable.isEmpty()) {
      reservationIdListFromUnavailable.remove(oldReservationId);
    }

    return reservationIdListFromUnavailable.isEmpty();
  }
  
  public void lockDates(String reservationId, LocalDate startDate, LocalDate endDate) {
    adjustAvailability(startDate, endDate, reservationId);
  }

  public void freeDates(LocalDate startDate, LocalDate endDate) {
    adjustAvailability(startDate, endDate, null);
  }
  
  private void adjustAvailability(LocalDate startDate, LocalDate endDate, String reservationId) {
    LocalDate workingDate = startDate;
    while (workingDate.isBefore(endDate)) {
      Availability availability = findById(workingDate);
      availability.setReservationId(reservationId);
      save(availability);
      workingDate = workingDate.plusDays(1);
    }
  }
  
}
