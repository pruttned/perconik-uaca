package com.gratex.perconik.useractivity.app.watchers.web.dto;

public class WebTabEventRequest extends BaseWebEventRequest{
	private String tabId;
	private String url;
	
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
}