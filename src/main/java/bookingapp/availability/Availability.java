package bookingapp.availability;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import bookingapp.shared.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Availability {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(columnDefinition = "VARCHAR(36)")
  private String id;
  
  @Column(nullable = false)
  private String resourceId;

  @Column(nullable = false)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate date;
  
  @Column(nullable = true)
  private String reservationId;
  
}
