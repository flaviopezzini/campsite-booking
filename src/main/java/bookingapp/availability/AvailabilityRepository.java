package bookingapp.availability;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AvailabilityRepository extends JpaRepository<Availability, String> {
  
  @Query("select a.date from Availability a where a.resourceId = :resourceId and a.date >= :startDate and a.date <= :endDate and a.reservationId is null")
  List<LocalDate> findByDateRange(@Param("resourceId") String resourceId, @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from Availability a where a.resourceId = :resourceId and a.date >= :startDate and a.date < :endDate")
  List<Availability> blockChangesByDateRange(@Param("resourceId") String resourceId, @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query("select distinct(a.reservationId) from Availability a where a.resourceId = :resourceId and a.date >= :startDate and a.date < :endDate and a.reservationId is not null")
  List<String> findUnavailableByDateRange(@Param("resourceId") String resourceId, @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
  
  @Transactional
  @Modifying
  //@Query("update Availability a set a.reservationId = :reservationId where a.id in (select b.id from (SELECT * FROM Availability) b where b.resourceId = :resourceId and b.date >= :startDate and b.date < :endDate order by b.id)")
  @Query(value = "update Availability set reservationId = :reservationId where id in (select id from (select * from Availability where resourceId = :resourceId and date >= :startDate and date < :endDate) as x)",
      nativeQuery = true)
  void adjustAvailability(@Param("resourceId") String resourceId, @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate, @Param("reservationId") String reservationId);
  
}
