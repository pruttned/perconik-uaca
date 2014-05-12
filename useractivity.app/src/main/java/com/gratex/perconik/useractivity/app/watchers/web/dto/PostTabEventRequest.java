package com.gratex.perconik.useractivity.app.watchers.web.dto;

public class PostTabEventRequest extends BaseWebEventRequest{
	private String tabId;
	private String url;
	private String operation;
	
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
}