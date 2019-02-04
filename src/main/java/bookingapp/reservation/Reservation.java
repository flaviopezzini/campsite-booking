package bookingapp.reservation;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import bookingapp.resource.Resource;
import bookingapp.shared.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(columnDefinition = "VARCHAR(36)")
  private String id;
  
  @Column(columnDefinition = "VARCHAR(40)", nullable = false)
  private String email;
  
  @Column(columnDefinition = "VARCHAR(40)", nullable = false)
  private String name;
  
  @Column(nullable = false)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate arrivalDate;
  
  @Column(nullable = false)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate departureDate;
  
  @Column(nullable = false)
  private boolean active;
  
  @ManyToOne
  @JoinColumn(name = "resourceId")
  private Resource resource;
  
}
