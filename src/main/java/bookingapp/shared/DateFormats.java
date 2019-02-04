package bookingapp.shared;

import java.time.format.DateTimeFormatter;

public enum DateFormats {
  LOCAL_DATE(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
  LOCAL_DATE_TIME(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
  
  private DateTimeFormatter dateTimeFormatter;
  
  DateFormats(DateTimeFormatter dateTimeFormatter) {
    this.dateTimeFormatter= dateTimeFormatter;
  }
  
  public DateTimeFormatter formatter() {
    return dateTimeFormatter;
  }

}
