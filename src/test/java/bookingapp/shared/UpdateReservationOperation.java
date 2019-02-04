package bookingapp.shared;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import bookingapp.reservation.ReservationController;
import bookingapp.reservation.ReservationRequest;

@Component
public class UpdateReservationOperation {

  private ObjectMapper objectMapper;
  private SaveReservationOperation saveReservationOperation;
  private GenericOperation genericOperation;
  private ReservationRequestBuilder reservationRequestBuilder;

  @Autowired
  public UpdateReservationOperation(ObjectMapper objectMapper,
      SaveReservationOperation saveReservationOperation, GenericOperation genericOperation,
      ReservationRequestBuilder reservationRequestBuilder) {
    super();
    this.objectMapper = objectMapper;
    this.saveReservationOperation = saveReservationOperation;
    this.genericOperation = genericOperation;
    this.reservationRequestBuilder = reservationRequestBuilder;
  }

  public void perform(MockMvc mockMvc, String reservationId, ReservationRequest reservationRequest,
      int expectedStatusCode, String expectedErrorMessage) throws Exception {
    String requestJson = objectMapper.writeValueAsString(reservationRequest);
    ResultActions operation = mockMvc
        .perform(put(String.format("%s/%s", ReservationController.REST_PREFIX, reservationId))
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestJson));
    genericOperation.perform(expectedStatusCode, expectedErrorMessage, operation);
  }

  public void performWithId(MockMvc mockMvc, ReservationRequest reservationRequest,
      int expectedStatusCode, String expectedErrorMessage) throws Exception {
    String reservationId = saveReservationOperation.perform(mockMvc, reservationRequestBuilder
        .createMockFromDates(LocalDate.now().plusDays(3), LocalDate.now().plusDays(6)),
        HttpStatus.CREATED.value(), null);
    reservationRequest.setId(reservationId);
    perform(mockMvc, reservationId, reservationRequest, expectedStatusCode, expectedErrorMessage);
  }

}
