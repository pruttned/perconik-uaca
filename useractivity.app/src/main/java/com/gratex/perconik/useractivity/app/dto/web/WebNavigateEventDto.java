package com.gratex.perconik.useractivity.app.dto.web;

import javax.ws.rs.core.UriBuilder;

public class WebNavigateEventDto extends WebEventDto {
	private String transitionTypeUri;
	
	public String getTransitionTypeUri() {
		return transitionTypeUri;
	}

	public void setTransitionTypeUri(String type) {
		this.transitionTypeUri = type;
	}
	
	@Override	
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("navigation");
	}
}