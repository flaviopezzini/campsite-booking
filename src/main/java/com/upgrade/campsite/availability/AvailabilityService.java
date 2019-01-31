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
  
  public boolean isDateRangeAvailableForCreation(LocalDate startDate, LocalDate endDate) {
    Long count = availabilityRepository.countUnavailableByDateRangeForCreation(startDate, endDate);
    List<Availability> list = availabilityRepository.findAll();
    System.out.println("***************************** CREATION *********************************************************");
    System.out.println("************************************************************************************************");
    System.out.println("************************************************************************************************");
    for (Availability av : list) {
      System.out.println(av.getDate() + "     " + av.getReservationId());
    }
    System.out.println("************************************************************************************************");
    System.out.println("************************************************************************************************");
    System.out.println("************************************************************************************************");
    return count == 0;
  }
  
  public boolean isDateRangeAvailableForUpdate(String oldReservationId, LocalDate startDate, LocalDate endDate) {
    Long count = availabilityRepository.countUnavailableByDateRangeForUpdate(oldReservationId, startDate, endDate);
    List<Availability> list = availabilityRepository.findAll();
    System.out.println("****************************** UPDATE **********************************************************");
    System.out.println("************************************************************************************************");
    System.out.println("************************************************************************************************");
    for (Availability av : list) {
      System.out.println(av.getDate() + "     " + av.getReservationId());
    }
    System.out.println("************************************************************************************************");
    System.out.println("************************************************************************************************");
    System.out.println("************************************************************************************************");
    return count == 0;
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
