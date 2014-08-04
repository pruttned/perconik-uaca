package com.gratex.perconik.useractivity.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.gratex.perconik.useractivity.app.dto.EventDto;

public final class EventSerializer {
  private static final ObjectMapper mapper = new ObjectMapper(); //thread safe

  public EventSerializer() {}

  public String serialize(EventDto event) throws JsonProcessingException {
    ValidationHelper.checkArgNotNull(event, "event");

    return mapper.writeValueAsString(event);
  }
}
