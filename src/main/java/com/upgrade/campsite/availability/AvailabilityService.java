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
  
  public void lockDates(LocalDate startDate, LocalDate endDate) {
    adjustAvailability(startDate, endDate, false);
  }

  public void freeDates(LocalDate startDate, LocalDate endDate) {
    adjustAvailability(startDate, endDate, true);
  }
  
  private void adjustAvailability(LocalDate startDate, LocalDate endDate, boolean available) {
    LocalDate workingDate = startDate;
    while (workingDate.isBefore(endDate)) {
      Availability availability = findById(workingDate);
      availability.setAvailable(available);
      save(availability);
      workingDate = workingDate.plusDays(1);
    }
  }

}
