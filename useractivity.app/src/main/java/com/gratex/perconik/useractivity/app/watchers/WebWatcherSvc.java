package com.gratex.perconik.useractivity.app.watchers;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.gratex.perconik.useractivity.app.EventDocument;
import com.gratex.perconik.useractivity.app.TypeUriHelper;

@Singleton
@Path("/web")
public class WebWatcherSvc {
  static final WatcherSvcReqHandler watcherSvcReqHandler = new WatcherSvcReqHandler();
  static final WatcherSvcReqHandler copyWatcherSvcReqHandler = new WatcherSvcReqHandler() {
    @Override
    protected boolean beforeAddToCache(EventDocument doc) {
      //handle paste from web
      WebWatcherState.getInstance().setCopiedText(doc.getUrl(), doc.getContent());
      return false;//don't save - web copy should be saved only for coping into an ide
    };
  };

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/navigate")
  public Response postNavigateEvent(String eventData) throws Exception {
    return watcherSvcReqHandler.handle(eventData, this.getBaseUri().path("navigate").build().toString());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/copy")
  public Response postCopyEvent(String eventData) throws Exception {
    return copyWatcherSvcReqHandler.handle(eventData, this.getBaseUri().path("copy").build().toString());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/save")
  public Response postSaveEvent(String eventData) throws Exception {
    return watcherSvcReqHandler.handle(eventData, this.getBaseUri().path("save").build().toString());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/bookmark")
  public Response postBookmarkEvent(String eventData) throws Exception {
    return watcherSvcReqHandler.handle(eventData, this.getBaseUri().path("bookmark").build().toString());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/tab/{eventType:(switchto|open|close)}")
  public Response postTabEvent(String eventData, @PathParam("eventType") String eventType) throws Exception {
    return watcherSvcReqHandler.handle(eventData, this.getBaseUri().path("tab").path(eventType).build().toString());
  }

  private UriBuilder getBaseUri() {
    return UriBuilder.fromPath(TypeUriHelper.EVENT_BASE_URI).path("web");
  }
}
