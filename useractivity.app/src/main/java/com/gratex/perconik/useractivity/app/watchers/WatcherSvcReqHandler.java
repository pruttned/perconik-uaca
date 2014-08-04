package com.gratex.perconik.useractivity.app.watchers;

import java.io.IOException;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.gratex.perconik.useractivity.app.App;
import com.gratex.perconik.useractivity.app.AppTracer;
import com.gratex.perconik.useractivity.app.EventDocument;
import com.gratex.perconik.useractivity.app.TypeUriHelper;

public class WatcherSvcReqHandler {
  private static final String EventTypeUriNotSetErrMsg = "EventTypeUri is not set";
  private static final String EventTypeUriWrongBaseErrMsg = String.format("EventTypeUri does not start with '%s'", TypeUriHelper.EVENT_BASE_URI);

  public WatcherSvcReqHandler() {}

  /**
   *
   * @param eventData
   * @param eventTypeUri - optionally overrides eventTypeUri in eventData. Must be set if the eventData doesn't contains the eventTypeUri
   * @return
   * @throws Exception
   */
  public Response handle(String eventData, String eventTypeUri) throws Exception {

    if (App.getInstance().isCollecting()) {

      //load document
      EventDocument eventDoc = null;
      try {
        eventDoc = EventDocument.loadFromRequest(eventData);
      } catch (IOException ex) {
        AppTracer.getInstance().writeError(String.format("Bad json event data:%n%n%s", eventData), ex);
        return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
      } catch (IllegalArgumentException ex) {
        AppTracer.getInstance().writeError(String.format("Bad json event data:%n%n%s", eventData), ex);
        return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
      }

      //event type uri
      if (eventTypeUri != null) {
        eventDoc.setEventTypeUri(eventTypeUri);
      }

      //prepare - svc specific
      if (this.beforeAddToCache(eventDoc)) {
        Response errResponse = checkEventTypeUri(eventDoc);
        if (errResponse != null) {
          return errResponse;
        }

        //commit to cache
        App.getInstance().getEventCache().addEventOrTrace(eventDoc.getEventId(), eventDoc.getTimestamp(), eventDoc.toJsonString());
      }
    }

    return Response.noContent().build();
  }

  private static Response checkEventTypeUri(EventDocument eventDoc) throws JsonProcessingException {
    if (!eventDoc.hasEventTypeUri()) {
      AppTracer.getInstance().writeError(String.format("%s%n%nEvent:%n%n%s", EventTypeUriNotSetErrMsg, eventDoc.toJsonString()));
      return Response.status(Response.Status.BAD_REQUEST).entity(EventTypeUriNotSetErrMsg).build();
    } else if (!eventDoc.getEventTypeUri().startsWith(TypeUriHelper.EVENT_BASE_URI)) {
      AppTracer.getInstance().writeError(String.format("%s%n%nEvent:%n%n%s", EventTypeUriWrongBaseErrMsg, eventDoc.toJsonString()));
      return Response.status(Response.Status.BAD_REQUEST).entity(EventTypeUriWrongBaseErrMsg).build();
    }

    return null;
  }

  /**
   * Executes svc specific modifications to event document
   * @param doc
   * @return Whether should be event saved
   */
  @SuppressWarnings("static-method")
  protected boolean beforeAddToCache(EventDocument doc) {
    return true;
  }
}
