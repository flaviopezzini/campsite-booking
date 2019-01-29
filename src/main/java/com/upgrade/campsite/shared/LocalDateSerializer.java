package com.upgrade.campsite.shared;

import java.io.IOException;
import java.time.LocalDate;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LocalDateSerializer extends StdSerializer<LocalDate> {

  private static final long serialVersionUID = 1L;
  
  public LocalDateSerializer() {
    this(null);
  }

  public LocalDateSerializer(Class<LocalDate> t) {
    super(t);
  }

  @Override
  public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider arg2)
      throws IOException, JsonProcessingException {
    gen.writeString(DateFormats.LOCAL_DATE.formatter().format(value));
  }
}
