package com.gratex.perconik.useractivity.app.watchers;

import com.gratex.perconik.useractivity.app.EventCache;
import com.gratex.perconik.useractivity.app.watchers.web.dto.WebBookmarkEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.WebCopyEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.WebNavigateEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.WebSaveEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.WebTabEventRequest;

public class WebWatcher implements IWatcher {
	// http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	private static class LazyHolder {
		private static final WebWatcher INSTANCE = new WebWatcher();
	}

	private boolean isStarted = false;

	public static WebWatcher getInstance() {
		return LazyHolder.INSTANCE;
	}

	public boolean isStarted() {
		return isStarted;
	}
	
	public void postNavigateEvent(WebNavigateEventRequest req) {
	}	
	
	public void postCopyEvent(WebCopyEventRequest req) {
	}
	
	public void postSaveEvent(WebSaveEventRequest req) {
	}
	
	public void postBookmarkEvent(WebBookmarkEventRequest req) {
	}
	
	public void postTabEvent(WebTabEventRequest req, String eventType) {
	}	

	@Override
	public String getDisplayName() {
		return "WebBrowser";
	}

	@Override
	public void start(EventCache eventCache) {
		isStarted = true;
	}

	@Override
	public void stop() {
		isStarted = false;
	}
	
	WebWatcher() {
	}
}
