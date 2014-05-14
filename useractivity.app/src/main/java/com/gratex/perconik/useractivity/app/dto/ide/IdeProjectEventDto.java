package com.gratex.perconik.useractivity.app.dto.ide;

import javax.ws.rs.core.UriBuilder;

public class IdeProjectEventDto extends IdeEventDto{
	public void setEventType(String eventType) {
		setEventTypeUri(UriBuilder.fromPath(getEventTypeUri()).path(eventType).build().toString());
	}
	
	@Override
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("project");
	}
}