package com.gratex.perconik.useractivity.app.watchers.web.dto;

public class PostNavigateEventRequest extends BaseWebEventRequest{
	private String tabId;
	private String url;
	private String transitionType;
	
	public String getTabId() {
		return tabId;
	}

	public void setTabId(String tabId) {
		this.tabId = tabId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTransitionType() {
		return transitionType;
	}

	public void setTransitionType(String type) {
		this.transitionType = type;
	}
}