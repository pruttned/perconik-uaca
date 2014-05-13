package com.gratex.perconik.useractivity.app.dto.web;

public class WebCopyEventDto extends WebEventDto {
	private String tabId;
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getTabId() {
		return tabId;
	}

	public void setTabId(String tabId) {
		this.tabId = tabId;
	}
}