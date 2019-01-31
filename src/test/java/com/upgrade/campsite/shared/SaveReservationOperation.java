package com.upgrade.campsite.shared;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsite.reservation.ReservationController;
import com.upgrade.campsite.reservation.ReservationRequest;

@Component
public class SaveReservationOperation {

  private ObjectMapper objectMapper;
  private GenericOperation genericOperation;
  
  @Autowired
  public SaveReservationOperation(ObjectMapper objectMapper, GenericOperation genericOperation) {
    super();
    this.objectMapper = objectMapper;
    this.genericOperation = genericOperation;
  }

  public String perform(MockMvc mockMvc, ReservationRequest reservationRequest, int expectedStatusCode,
      String expectedErrorMessage) throws Exception {
    String requestJson = objectMapper.writeValueAsString(reservationRequest);
    ResultActions operation = mockMvc.perform(post(ReservationController.REST_PREFIX)
        .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson));
    return genericOperation.perform(expectedStatusCode, expectedErrorMessage, operation);
  }
  
}
