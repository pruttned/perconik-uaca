package com.gratex.perconik.useractivity.app.watchers.web.dto;

public class WebNavigateEventRequest extends BaseWebEventRequest{
	private String tabId;
	private String url;
	private String transitionTypeUri;
	
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

	public String getTransitionTypeUri() {
		return transitionTypeUri;
	}

	public void setTransitionTypeUri(String type) {
		this.transitionTypeUri = type;
	}
}