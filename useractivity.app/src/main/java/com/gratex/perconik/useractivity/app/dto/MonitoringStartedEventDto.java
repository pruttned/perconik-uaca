package com.gratex.perconik.useractivity.app.dto;

import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import com.gratex.perconik.useractivity.app.Settings;

public class MonitoringStartedEventDto extends SystemEventDto {
	private static final String SESSION_ID = UUID.randomUUID().toString();
	private String appName;
	private String appVersion;
	private String sessionId;
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getAppVersion() {
		return appVersion;
	}
	
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
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
