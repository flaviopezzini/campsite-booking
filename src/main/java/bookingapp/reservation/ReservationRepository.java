package bookingapp.reservation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

  @Query("select r from Reservation r where r.arrivalDate < :endDate and r.departureDate > :startDate")
  List<Reservation> findByDateRange(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

}
