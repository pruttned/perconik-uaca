package com.gratex.perconik.useractivity.app.dto.web;

import javax.ws.rs.core.UriBuilder;

public class WebTabEventDto extends WebEventDto {
	public void setEventType(String eventType) {
		setEventTypeUri(UriBuilder.fromPath(getEventTypeUri()).path(eventType).build().toString());
	}
	
	@Override	
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("tab");
	}	
}