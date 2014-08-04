package com.gratex.perconik.useractivity.app.watchers;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.gratex.perconik.useractivity.app.TypeUriHelper;

@Singleton
@Path("/bash")
@SuppressWarnings("static-method")
public class BashCommandWatcherSvc {
  static final WatcherSvcReqHandler watcherSvcReqHandler = new WatcherSvcReqHandler();

  public BashCommandWatcherSvc() {}

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/command")
  public Response postGenericEvent(String eventData) throws Exception {
    return watcherSvcReqHandler.handle(eventData, UriBuilder.fromPath(TypeUriHelper.EVENT_BASE_URI).path("bash/command").build().toString());
  }
}
