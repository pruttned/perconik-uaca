package com.gratex.perconik.useractivity.app.dto;

import javax.ws.rs.core.UriBuilder;

public class SystemEventDto extends EventDto {
  public SystemEventDto() {}

  @Override
  protected UriBuilder getDefaultEventTypeUri() {
    return super.getDefaultEventTypeUri().path("system");
  }
}
