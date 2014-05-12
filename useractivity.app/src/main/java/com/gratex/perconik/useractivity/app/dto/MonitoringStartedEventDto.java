package com.gratex.perconik.useractivity.app.dto;

import java.util.UUID;

import com.gratex.perconik.useractivity.app.TypeUriBuilder;

public class MonitoringStartedEventDto extends ApplicationEventDto {
	private static final String SESSION_ID = UUID.randomUUID().toString();
	
	public MonitoringStartedEventDto() {
		setEventTypeUri(TypeUriBuilder.getEventUri("System/MonitoringStarted"));
		setApplicationName("UACA");
		setApplicationVersion("2.0"); //TODO: get dynamically
		setSessionId(SESSION_ID);		
	}
}
