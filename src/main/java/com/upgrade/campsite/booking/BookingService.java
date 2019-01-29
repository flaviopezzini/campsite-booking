package com.upgrade.campsite.booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.upgrade.campsite.shared.InvalidRecordException;

@Service
public class BookingService {

  private BookingRepository bookingRepository;

  @Autowired
  public BookingService(BookingRepository ideaRepository) {
    super();
    this.bookingRepository = ideaRepository;
  }

  public List<Booking> findAvailabilityByDateRange(LocalDate startDate, LocalDate endDate) {
    return bookingRepository.findAvailabilityByDateRange(startDate, endDate);
  }

  public Booking save(BookingRequest record) throws InvalidRecordException {
    Booking toSave = null;
    if (record.getId() == null) {
      toSave = record.createNewBooking();
    } else {
      Booking oldBooking = findById(record.getId());
      toSave = record.updateBooking(oldBooking);
    }
    return bookingRepository.save(toSave);
  }
  
  public Booking findById(String id) {
    Optional<Booking> record = bookingRepository.findById(id);

    return record.isPresent() ? record.get() : null;
  }

  public void delete(String id) {
    bookingRepository.deleteById(id);
  }
}
