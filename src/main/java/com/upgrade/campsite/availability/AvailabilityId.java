package com.upgrade.campsite.availability;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.upgrade.campsite.shared.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityId implements Serializable {
  
  private static final long serialVersionUID = 1L;

  @Column(nullable = false)
  private String resourceId;

  @Column(nullable = false)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate date;
  
}
