package com.gratex.perconik.useractivity.app.dto;

import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import com.gratex.perconik.useractivity.app.Settings;

public class MonitoringStartedEventDto extends SystemEventDto {
  private static final String SESSION_ID = UUID.randomUUID().toString();
  private String appName;
  private String appVersion;
  private String sessionId;

  public MonitoringStartedEventDto() {
    this.setAppName("UACA");
    this.setAppVersion(Settings.getInstance().getVersion());
    this.setSessionId(SESSION_ID);
  }

  public String getAppName() {
    return this.appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppVersion() {
    return this.appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getSessionId() {
    return this.sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  @Override
  protected UriBuilder getDefaultEventTypeUri() {
    return super.getDefaultEventTypeUri().path("monitoringstarted");
  }
}
