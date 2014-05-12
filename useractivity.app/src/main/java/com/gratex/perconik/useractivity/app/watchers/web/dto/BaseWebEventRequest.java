package com.gratex.perconik.useractivity.app.watchers.web.dto;

import javax.xml.datatype.XMLGregorianCalendar;

public class BaseWebEventRequest{
	private XMLGregorianCalendar timestamp;
	private String sessionId;
	private String appName;
	private String appVersion;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

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
	
	public XMLGregorianCalendar getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(XMLGregorianCalendar timestamp) {
		this.timestamp = timestamp;
	}
}