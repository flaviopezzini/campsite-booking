package com.upgrade.campsite.shared;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.upgrade.campsite.availability.AvailabilityController;

@Component
public class GetAvailabilityOperation {

  private DateTimeFormatter formatter = DateFormats.LOCAL_DATE.formatter();

  private GenericOperation genericOperation;

  @Autowired
  public GetAvailabilityOperation(GenericOperation genericOperation) {
    super();
    this.genericOperation = genericOperation;
  }

  public void perform(MockMvc mockMvc, LocalDate startDate, LocalDate endDate,
      int expectedStatusCode, String expectedResponse) throws Exception {
    String formattedStartDate =
        (startDate == null ? StringUtils.EMPTY : startDate.format(formatter));
    String formattedEndDate = (endDate == null ? StringUtils.EMPTY : endDate.format(formatter));

    perform(mockMvc, formattedStartDate, formattedEndDate, expectedStatusCode, expectedResponse);
  }

  public void perform(MockMvc mockMvc, String startDate, String endDate, int expectedStatusCode,
      String expectedResponse) throws Exception {
    ResultActions operation = mockMvc.perform(get(buildUrl(startDate, endDate))).andDo(print())
        .andExpect(status().is(expectedStatusCode));

    genericOperation.perform(expectedStatusCode, expectedResponse, operation);
  }

  private String buildUrl(String startDate, String endDate) {
    return String.format("%s?startDate=%s&endDate=%s",
        AvailabilityController.AVAILABILITY_END_POINT, startDate, endDate);
  }

}
