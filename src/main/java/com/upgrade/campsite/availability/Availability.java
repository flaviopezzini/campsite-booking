package com.upgrade.campsite.availability;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.upgrade.campsite.shared.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Availability {

  @Id
  @Column(nullable = false)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate date;
  
  @Column(nullable = true)
  private String reservationId;
  
}
