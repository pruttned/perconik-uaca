package com.gratex.perconik.useractivity.app.watchers;

import com.gratex.perconik.useractivity.app.EventCache;
import com.gratex.perconik.useractivity.app.dto.web.WebBookmarkEventDto;
import com.gratex.perconik.useractivity.app.dto.web.WebCopyEventDto;
import com.gratex.perconik.useractivity.app.dto.web.WebEventDto;
import com.gratex.perconik.useractivity.app.dto.web.WebNavigateEventDto;
import com.gratex.perconik.useractivity.app.dto.web.WebSaveDocumentEventDto;
import com.gratex.perconik.useractivity.app.dto.web.WebTabEventDto;

public class WebWatcher implements IWatcher {
	// http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	private static class LazyHolder {
		private static final WebWatcher INSTANCE = new WebWatcher();
	}

	private boolean isRunning = false;
	private EventCache eventCache;

	private WebWatcher() {
	}
	
	public static WebWatcher getInstance() {
		return LazyHolder.INSTANCE;
	}

	@Override
	public String getDisplayName() {
		return "Web";
	}
	
	@Override
	public void start(EventCache eventCache) {
		this.eventCache = eventCache;
		isRunning = true;
	}

	@Override
	public void stop() {
		isRunning = false;
	}
	
	public void postNavigateEvent(WebNavigateEventDto dto) {
		addEventIfRunning(dto);
	}
	
	public void postCopyEvent(WebCopyEventDto dto) {
		//TODO: pozri, ako je to v starom
	}
	
	public void postSaveEvent(WebSaveDocumentEventDto dto) {
		addEventIfRunning(dto);
	}
	
	public void postBookmarkEvent(WebBookmarkEventDto dto) {
		addEventIfRunning(dto);
	}
	
	public void postTabEvent(WebTabEventDto dto, String eventType) {
		dto.setEventType(eventType);
		addEventIfRunning(dto);
	}
	
	private void addEventIfRunning(WebEventDto dto) {
		if(isRunning) {
			eventCache.addEventOrTrace(dto);
		}
	}
}