package com.gratex.perconik.useractivity.app.dto.web;

import javax.ws.rs.core.UriBuilder;
import com.gratex.perconik.useractivity.app.dto.ApplicationEventDto;

public class WebEventDto extends ApplicationEventDto {
	private String url;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override	
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("web");
	}
}