package com.gratex.perconik.useractivity.app.dto;

import java.util.UUID;

import javax.ws.rs.core.UriBuilder;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gratex.perconik.useractivity.app.Settings;
import com.gratex.perconik.useractivity.app.TypeUriHelper;
import com.gratex.perconik.useractivity.app.XMLGregorianCalendarHelper;

public class EventDto {	 
	@JsonSerialize(using = ToStringSerializer.class)
	private XMLGregorianCalendar timestamp = XMLGregorianCalendarHelper.createUtcNow();
	private UUID eventId = UUID.randomUUID();	
	private String user = Settings.getInstance().getUserName();
	private String workstation = Settings.getInstance().getWorkstationName();
	private String eventTypeUri = getDefaultEventTypeUri().build().toString();
	
	public UUID getEventId() {
		return this.eventId;
	}

	public void setEventId(UUID eventId) {
		this.eventId = eventId;
	}

	public XMLGregorianCalendar getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(XMLGregorianCalendar timestamp) {
		this.timestamp = timestamp;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getWorkstation() {
		return this.workstation;
	}

	public void setWorkstation(String workstation) {
		this.workstation = workstation;
	}

	public String getEventTypeUri() {
		return this.eventTypeUri;
	}

	public void setEventTypeUri(String eventTypeUri) {
		this.eventTypeUri = eventTypeUri;
	}
	
	protected UriBuilder getDefaultEventTypeUri() {		
		return UriBuilder.fromPath(TypeUriHelper.BASE_URI).path("Event");
	}
}