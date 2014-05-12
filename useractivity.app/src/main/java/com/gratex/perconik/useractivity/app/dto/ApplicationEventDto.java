package com.gratex.perconik.useractivity.app.dto;

public class ApplicationEventDto extends EventDto {
	private String applicationName;
	private String applicationVersion;
	private String sessionId;
	
	public String getApplicationName() {
		return applicationName;
	}
	
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public String getApplicationVersion() {
		return applicationVersion;
	}
	
	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
