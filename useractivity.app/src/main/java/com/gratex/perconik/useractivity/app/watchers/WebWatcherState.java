package com.gratex.perconik.useractivity.app.watchers;

import com.gratex.perconik.useractivity.app.ValidationHelper;

public class WebWatcherState {
	// http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	private static class LazyHolder {
		private static final WebWatcherState INSTANCE = new WebWatcherState();
	}

	private String lastCopyText;
	private String lastCopyUrl;
	
	private WebWatcherState() {
	}
	
	public static WebWatcherState getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void setCopiedText(String url, String text) {
		if(!ValidationHelper.isStringNullOrWhitespace(text)) {
			lastCopyUrl = url;
			lastCopyText = text.trim(); //TODO: remove all whitespaces
		}
		else {
			lastCopyText = null;
			lastCopyUrl = null;
		}
	}
	
	public String getLastCopyText() {
		return lastCopyText;
	}
	
	public String getLastCopyUrl() {
		return lastCopyUrl;
	}
}