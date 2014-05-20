package com.gratex.perconik.useractivity.app.watchers;

import com.gratex.perconik.useractivity.app.EventCache;
import com.gratex.perconik.useractivity.app.ValidationHelper;
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
	private String lastCopyText;
	private String lastCopyUrl;
	
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
		if(!ValidationHelper.isStringNullOrWhitespace(dto.getContent())) {
			lastCopyText = dto.getContent().trim(); //TODO: remove all whitespaces
			lastCopyUrl = dto.getUrl();
		}
		else {
			lastCopyText = null;
			lastCopyUrl = null;
		}
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
	
	public String getLastCopyText() {
		return lastCopyText;
	}
	
	public String getLastCopyUrl() {
		return lastCopyUrl;
	}
	
	private void addEventIfRunning(WebEventDto dto) {
		if(isRunning) {
			eventCache.addEventOrTrace(dto);
		}
	}
}