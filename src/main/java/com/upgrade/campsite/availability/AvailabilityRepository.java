package com.upgrade.campsite.availability;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailabilityRepository extends JpaRepository<Availability, LocalDate> {
  
  @Query("select a.date from Availability a where a.date >= :startDate and a.date <= :endDate and a.reservationId = null")
  List<LocalDate> findByDateRange(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query("select count(a.date) from Availability a where a.date >= :startDate and a.date <= :endDate and a.reservationId != null")
  Long countUnavailableByDateRangeForCreation(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query("select count(a.date) from Availability a where a.date >= :startDate and a.date <= :endDate and (a.reservationId != null and a.reservationId != :oldReservationId)")
  Long countUnavailableByDateRangeForUpdate(@Param("oldReservationId") String oldReservationId, @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}
