package com.upgrade.campsite.reservation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends PagingAndSortingRepository<Reservation, String> {

  @Query("select r from Reservation r where r.arrivalDate < :endDate and r.departureDate > :startDate")
  List<Reservation> findByDateRange(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
  
}
