package com.gratex.perconik.useractivity.app.docfilters;

import java.util.ArrayList;

import com.gratex.perconik.useractivity.app.EventDocument;

public class EventDocumentFilterManager {

  private static final EventDocumentFilterManager instance = new EventDocumentFilterManager();
  private ArrayList<EventDocumentFilter> eventDocumentFilters = new ArrayList<EventDocumentFilter>();

  public static EventDocumentFilterManager getInstance() {
    return instance;
  }

  public void filter(EventDocument doc) {
    for (EventDocumentFilter filter: this.eventDocumentFilters) {
      filter.filter(doc);
    }
  }

  private EventDocumentFilterManager() {
    //TODO: dynamic registration
    this.eventDocumentFilters.add(new EventDocumentTextNodeFilter());
    this.eventDocumentFilters.add(new BashCurlAnonymizeEventDocumentFilter());
    this.eventDocumentFilters.add(new GitCloneAnonymizeEventDocumentFilter());
  }
}
