package com.upgrade.campsite.booking;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends PagingAndSortingRepository<Booking, String> {

  @Query("select b from Booking b where b.arrivalDate >= :startDate and b.departureDate <= :endDate")
  List<Booking> findAvailabilityByDateRange(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  // Booking findByUserAndContent(@Param("user") User user, @Param("content") String content);

}
