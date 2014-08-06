package com.gratex.perconik.useractivity.app;

import javax.ws.rs.core.UriBuilder;

public final class TypeUris {
  //TODO: add other uris

  public static final String BASH_COMMAND = UriBuilder.fromUri(TypeUriHelper.EVENT_BASE_URI).path("bash/command").toString();
}
