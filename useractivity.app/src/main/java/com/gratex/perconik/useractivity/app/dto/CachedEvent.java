package com.gratex.perconik.useractivity.app.dto;

import javax.xml.datatype.XMLGregorianCalendar;

public class CachedEvent {
	private String eventId;
	private XMLGregorianCalendar timestamp;
	private String data;
	
	public CachedEvent(String eventId, XMLGregorianCalendar timestamp, String data) {
		this.eventId = eventId;
		this.timestamp = timestamp;
		this.data = data;
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
