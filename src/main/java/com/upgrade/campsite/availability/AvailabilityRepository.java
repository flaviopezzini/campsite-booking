package com.upgrade.campsite.availability;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailabilityRepository extends JpaRepository<Availability, LocalDate> {
  
  @Query("select a.date from Availability a where a.date >= :startDate and a.date <= :endDate and a.reservationId is null")
  List<LocalDate> findByDateRange(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from Availability a where a.date >= :startDate and a.date < :endDate")
  List<Availability> blockChangesByDateRange(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query("select distinct(a.reservationId) from Availability a where a.date >= :startDate and a.date < :endDate and a.reservationId is not null")
  List<String> findUnavailableByDateRange(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
  
}
