package com.gratex.perconik.useractivity.app;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class Settings {
	private final static Settings INSTANCE = new Settings();
	private final String userFolder = Paths.get(System.getProperty("user.home"), "GratexInternational", "UserActivity").toString();
	
	private Settings() {
	}
	
	public static Settings getInstance() {
		return INSTANCE;
	}
	
	public long getEventCommitInterval() {
		return Preferences.userRoot().getLong("EventCommitInterval", 60000 * 30); //default is 30 minutes
	}
	
	public void setEventCommitInterval(long eventCommitInterval) {
		Preferences.userRoot().putLong("EventCommitInterval", eventCommitInterval);
	}
	
	public long getEventAgeToCommit() {
		return Preferences.userRoot().getLong("EventAgeToCommit", 60000 * 30); //default is 30 minutes
	}
	
	public void setEventAgeToCommit(long eventAgeToCommit) {
		Preferences.userRoot().putLong("EventAgeToCommit", eventAgeToCommit);
	}
	
	public String getUserName() {
		return Preferences.userRoot().get("UserName", System.getProperty("user.name"));
	}
	
	public void setUserName(String userName) {
		ValidationHelper.checkStringArgNotNullOrWhitespace(userName, "userName");
		
		Preferences.userRoot().put("UserName", userName);
	}
	
	public void clearUserName() {
		Preferences.userRoot().remove("UserName");
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
}