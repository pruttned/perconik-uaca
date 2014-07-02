package com.gratex.perconik.useractivity.app;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class Settings {	
	private final static Settings INSTANCE = new Settings();
	private final String preferencesPackageName = "com/gratex/perconik/useractivity/app"; //for Preferences API
	private final String userFolder = Paths.get(System.getProperty("user.home"), "GratexInternational", "UserActivity").toString();
	private final String defaultSvcUrl = "http://perconikprod.hq.gratex.com:9090/web/api/UserActivity"; 
	private final int defaultLocalSvcPort = 16375;
	private final int defaultMaxRowCountInLog = 1000;
	
	private Settings() {
	}
	
	public static Settings getInstance() {
		return INSTANCE;
	}
	
	public long getEventCommitInterval() {
		return getPreferencesNode().getLong("event_commit_interval", 60000 * 30); //default is 30 minutes
	}
	
	public void setEventCommitInterval(long eventCommitInterval) {
		getPreferencesNode().putLong("event_commit_interval", eventCommitInterval);
	}
	
	public long getEventAgeToCommit() {
		return getPreferencesNode().getLong("event_age_to_commit", 60000 * 60); //default is 60 minutes
	}
	
	public void setEventAgeToCommit(long eventAgeToCommit) {
		getPreferencesNode().putLong("event_age_to_commit", eventAgeToCommit);
	}
	
	public String getUserName() {
		return getPreferencesNode().get("user_name", System.getProperty("user.name"));
	}
	
	public void setUserName(String userName) {
		ValidationHelper.checkStringArgNotNullOrWhitespace(userName, "userName");
		
		getPreferencesNode().put("user_name", userName);
	}
	
	public void clearUserName() {
		getPreferencesNode().remove("user_name");
	}

	public String getUserFolder() {
		return this.userFolder;
	}
	
	public String getWorkstationName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			return "UNKNOWN"; //TODO: find a better solution
		}
	}
	
	public String getSvcUrl() {
		return getPreferencesNode().get("svc_url", defaultSvcUrl);
	}
	
	public void setSvcUrl(String url) {
		ValidationHelper.checkStringArgNotNullOrWhitespace(url, "url");
		
		getPreferencesNode().put("svc_url", url);
	}
	
	public String getVersion() {
		return "2.0.6"; //TODO: find a standard way
	}
	
	public int getLocalSvcPort() {
		return getPreferencesNode().getInt("local_svc_port", defaultLocalSvcPort);
	}
	
	public void setLocalSvcPort(int localSvcPort) {
		getPreferencesNode().putInt("local_svc_port", localSvcPort);
	}
	
	public int getMaxRowCountInLog() {
		return getPreferencesNode().getInt("max_row_count_in_log", defaultMaxRowCountInLog);
	}
	
	public void setMaxRowCountInLog(int maxRowCountInLog) {
		getPreferencesNode().putInt("max_row_count_in_log", maxRowCountInLog);
		AppTracer.getInstance().ensureMaxRowCount();
	}
	
	private Preferences getPreferencesNode() {
		return Preferences.userRoot().node(preferencesPackageName);
	}
}
