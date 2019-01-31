package com.upgrade.campsite.availability;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailabilityRepository extends JpaRepository<Availability, LocalDate> {
  
  @Query("select a.date from Availability a where a.date >= :startDate and a.date <= :endDate and a.available = true")
  List<LocalDate> findByDateRange(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

}
