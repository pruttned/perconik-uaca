package com.gratex.perconik.useractivity.app.dto;

import java.util.Date;
import java.util.UUID;

import com.gratex.perconik.useractivity.app.Settings;

public class EventDto {
	private UUID eventId = UUID.randomUUID();
	private Date time = new Date(); //UTC now
	private String user = Settings.getInstance().getUserName();
	private String workstation = Settings.getInstance().getWorkstationName();
	private String eventTypeUri;
	
	public UUID getEventId() { return this.eventId; }	
	public void setEventId(UUID eventId) { this.eventId = eventId; }	
	public Date getTime() { return this.time; }
	public void setTime(Date time) { this.time = time; }
	public String getUser() { return this.user; }
	public void setUser(String user) { this.user = user; }
	public String getWorkstation() { return this.workstation; }
	public void setWorkstation(String workstation) { this.workstation = workstation; }
	public String getEventTypeUri() { return this.eventTypeUri; }
	public void setEventTypeUri(String eventTypeUri) { this.eventTypeUri = eventTypeUri; }
}