package com.upgrade.campsite.booking;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.upgrade.campsite.shared.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Booking {

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
  @JsonProperty("arrivalDate")
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate arrivalDate;
  
  @Column(nullable = false)
  @JsonProperty("departureDate")
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate departureDate;
  
}
