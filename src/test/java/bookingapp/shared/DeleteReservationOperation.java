package bookingapp.shared;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import bookingapp.reservation.ReservationController;

@Component
public class DeleteReservationOperation {

  private GenericOperation genericOperation;

  @Autowired
  public DeleteReservationOperation(GenericOperation genericOperation) {
    super();
    this.genericOperation = genericOperation;
  }

  public void perform(MockMvc mockMvc, String reservationId, int expectedStatusCode,
      String expectedErrorMessage) throws Exception {
    ResultActions operation = mockMvc
        .perform(delete(String.format("%s/%s", ReservationController.REST_PREFIX, reservationId))
            .contentType(MediaType.APPLICATION_JSON_VALUE));
    genericOperation.perform(expectedStatusCode, expectedErrorMessage, operation);
  }

}
