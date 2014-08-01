package com.gratex.perconik.useractivity.app;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class Settings {
  private final static Settings INSTANCE = new Settings();
  private final static String preferencesPackageName = "com/gratex/perconik/useractivity/app"; //for Preferences API
  private final static String userFolder = Paths.get(System.getProperty("user.home"), "GratexInternational", "UserActivity").toString();
  private final static String defaultSvcUrl = "http://perconikprod.hq.gratex.com:9090/web/api/UserActivity";
  private final static int defaultLocalSvcPort = 16375;
  private final static int defaultMaxRowCountInLog = 1000;

  private Settings() {}

  public static Settings getInstance() {
    return INSTANCE;
  }

  public long getEventCommitInterval() {
    return this.getPreferencesNode().getLong("event_commit_interval", 60000 * 30); //default is 30 minutes
  }

  public void setEventCommitInterval(long eventCommitInterval) {
    this.getPreferencesNode().putLong("event_commit_interval", eventCommitInterval);
  }

  public long getEventAgeToCommit() {
    return this.getPreferencesNode().getLong("event_age_to_commit", 60000 * 60); //default is 60 minutes
  }

  public void setEventAgeToCommit(long eventAgeToCommit) {
    this.getPreferencesNode().putLong("event_age_to_commit", eventAgeToCommit);
  }

  public String getUserName() {
    return this.getPreferencesNode().get("user_name", System.getProperty("user.name"));
  }

  public void setUserName(String userName) {
    ValidationHelper.checkStringArgNotNullOrWhitespace(userName, "userName");

    this.getPreferencesNode().put("user_name", userName);
  }

  public void clearUserName() {
    this.getPreferencesNode().remove("user_name");
  }

  public String getUserFolder() {
    return Settings.userFolder;
  }

  public String getWorkstationName() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException ex) {
      return "UNKNOWN"; //TODO: find a better solution
    }
  }

  public String getSvcUrl() {
    return this.getPreferencesNode().get("svc_url", Settings.defaultSvcUrl);
  }

  public void setSvcUrl(String url) {
    ValidationHelper.checkStringArgNotNullOrWhitespace(url, "url");

    this.getPreferencesNode().put("svc_url", url);
  }

  public String getVersion() {
    return "2.0.8"; //TODO: find a standard way
  }

  public int getLocalSvcPort() {
    return this.getPreferencesNode().getInt("local_svc_port", Settings.defaultLocalSvcPort);
  }

  public void setLocalSvcPort(int localSvcPort) {
    this.getPreferencesNode().putInt("local_svc_port", localSvcPort);
  }

  public int getMaxRowCountInLog() {
    return this.getPreferencesNode().getInt("max_row_count_in_log", Settings.defaultMaxRowCountInLog);
  }

  public void setMaxRowCountInLog(int maxRowCountInLog) {
    this.getPreferencesNode().putInt("max_row_count_in_log", maxRowCountInLog);
    AppTracer.getInstance().ensureMaxRowCount();
  }

  private Preferences getPreferencesNode() {
    return Preferences.userRoot().node(Settings.preferencesPackageName);
  }
}
