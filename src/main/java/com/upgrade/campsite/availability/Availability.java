package com.upgrade.campsite.availability;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Availability {

  @EmbeddedId
  private AvailabilityId id;
  
  @Column(nullable = true)
  private String reservationId;
  
}
