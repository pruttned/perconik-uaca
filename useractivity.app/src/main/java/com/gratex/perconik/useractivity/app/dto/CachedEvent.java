package com.gratex.perconik.useractivity.app.dto;

import java.util.Date;
import java.util.UUID;

public class CachedEvent {
	private UUID eventId;
	private Date time;
	private String data;
	
	public CachedEvent(UUID eventId, Date time, String data) {
		this.eventId = eventId;
		this.time = time;
		this.data = data;
	}
	
	public UUID getEventId() {
		return this.eventId;
	}
	
	public Date getTime() {
		return this.time;
	}
	
	public String getData() {
		return this.data;
	}
}
