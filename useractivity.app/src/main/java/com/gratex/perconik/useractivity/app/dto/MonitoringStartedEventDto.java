package com.gratex.perconik.useractivity.app.dto;

import java.util.UUID;
import javax.ws.rs.core.UriBuilder;
import com.gratex.perconik.useractivity.app.Settings;

public class MonitoringStartedEventDto extends ApplicationEventDto {
	private static final String SESSION_ID = UUID.randomUUID().toString();
	
	public MonitoringStartedEventDto() {
		setAppName("UACA");
		setAppVersion(Settings.getInstance().getVersion());
		setSessionId(SESSION_ID);
	}
	
	@Override	
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("monitoringstarted");
	}
}
