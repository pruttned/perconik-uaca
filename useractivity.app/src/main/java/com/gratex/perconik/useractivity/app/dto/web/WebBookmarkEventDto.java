package com.gratex.perconik.useractivity.app.dto.web;

import javax.ws.rs.core.UriBuilder;

public class WebBookmarkEventDto extends WebEventDto {
	@Override	
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("bookmark");
	}
}