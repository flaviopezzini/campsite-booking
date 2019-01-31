package com.upgrade.campsite.shared;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@Component
public class GenericOperation {
  
  public String perform(int expectedStatusCode, String expectedResponse,
      ResultActions operation) throws Exception {

    ResultActions resultActions =
        operation.andDo(print()).andExpect(status().is(expectedStatusCode));
    if (HttpStatus.BAD_REQUEST.value() == expectedStatusCode) {
      resultActions.andExpect(content().string(equalTo(expectedResponse)));
    }
    MvcResult result = resultActions.andReturn();
    return result.getResponse().getContentAsString();
  }

}
