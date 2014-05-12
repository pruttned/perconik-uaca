package com.gratex.perconik.useractivity.app.watchers.web.dto;

public class WebCopyEventRequest extends BaseWebEventRequest{
	private String tabId;
	private String url;
	private String content;
	
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}