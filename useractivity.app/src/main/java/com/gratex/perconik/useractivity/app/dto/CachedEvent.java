package com.gratex.perconik.useractivity.app.dto;

import javax.xml.datatype.XMLGregorianCalendar;

public final class CachedEvent {
  private final int id;
  private final String eventId;
  private final XMLGregorianCalendar timestamp;

  private String data;

  public CachedEvent(int id, String eventId, XMLGregorianCalendar timestamp, String data) {
    this.id = id;
    this.eventId = eventId;
    this.timestamp = timestamp;
    this.data = data;
  }

  public int getId() {
    return this.id;
  }

  public String getEventId() {
    return this.eventId;
  }

  public XMLGregorianCalendar getTimestamp() {
    return this.timestamp;
  }

  public String getData() {
    return this.data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
