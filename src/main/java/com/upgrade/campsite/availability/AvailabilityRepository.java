package com.upgrade.campsite.availability;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailabilityRepository extends JpaRepository<Availability, AvailabilityId> {
  
  @Query("select a.id.date from Availability a where a.id.resourceId = :resourceId and a.id.date >= :startDate and a.id.date <= :endDate and a.reservationId is null")
  List<LocalDate> findByDateRange(@Param("resourceId") String resourceId, @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from Availability a where a.id.resourceId = :resourceId and a.id.date >= :startDate and a.id.date < :endDate")
  List<Availability> blockChangesByDateRange(@Param("resourceId") String resourceId, @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query("select distinct(a.reservationId) from Availability a where a.id.resourceId = :resourceId and a.id.date >= :startDate and a.id.date < :endDate and a.reservationId is not null")
  List<String> findUnavailableByDateRange(@Param("resourceId") String resourceId, @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
  
}
