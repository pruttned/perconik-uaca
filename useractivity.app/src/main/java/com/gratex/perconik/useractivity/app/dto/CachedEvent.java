package com.gratex.perconik.useractivity.app.dto;

import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

public class CachedEvent {
	private UUID eventId;
	private XMLGregorianCalendar timestamp;
	private String data;
	
	public CachedEvent(UUID eventId, XMLGregorianCalendar timestamp, String data) {
		this.eventId = eventId;
		this.timestamp = timestamp;
		this.data = data;
	}
	
	public UUID getEventId() {
		return this.eventId;
	}
	
	public XMLGregorianCalendar getTimestamp() {
		return this.timestamp;
	}
	
	public String getData() {
		return this.data;
	}
}
