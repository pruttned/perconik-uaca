package com.gratex.perconik.useractivity.app;

import javax.ws.rs.core.UriBuilder;

public class TypeUriHelper {
  public static final String BASE_URI = "http://perconik.gratex.com/useractivity";
  public static final String EVENT_BASE_URI = UriBuilder.fromPath(BASE_URI).path("event").build().toString(); //!! EventDto does not use this constant !!

  /**
   * e.g: '...event/ide/switchto' -> 'ide/switchto'
   */
  public static String getEventTypeShortUri(String eventTypeUri) {
    ValidationHelper.checkStringArgNotNullOrWhitespace(eventTypeUri, "eventTypeUri");
    return eventTypeUri.substring(EVENT_BASE_URI.length() + 1);
  }
}
