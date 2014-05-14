package com.gratex.perconik.useractivity.app.watchers;

import com.gratex.perconik.useractivity.app.EventCache;
import com.gratex.perconik.useractivity.app.dto.ide.IdeCheckinEventRequest;
import com.gratex.perconik.useractivity.app.dto.ide.IdeCodeElementEventRequest;
import com.gratex.perconik.useractivity.app.dto.ide.IdeCodeEventRequest;
import com.gratex.perconik.useractivity.app.dto.ide.IdeDocumentEventRequest;
import com.gratex.perconik.useractivity.app.dto.ide.IdeEventRequest;
import com.gratex.perconik.useractivity.app.dto.ide.IdeFindEventRequest;
import com.gratex.perconik.useractivity.app.dto.ide.IdeProjectEventRequest;
import com.gratex.perconik.useractivity.app.dto.ide.IdeStateChangeEventRequest;

public class IdeWatcher implements IWatcher {
	// http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	private static class LazyHolder {
		private static final IdeWatcher INSTANCE = new IdeWatcher();
	}

	private boolean isRunning = false;
	private EventCache eventCache;
	
	private IdeWatcher() {
	}
	
	public static IdeWatcher getInstance() {
		return LazyHolder.INSTANCE;
	}

	@Override
	public String getDisplayName() {
		return "IDE";
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
	
	public void postCheckinEvent(IdeCheckinEventRequest dto) {
		addEventIfRunning(dto);
	}
	
	public void postFindEvent(IdeFindEventRequest dto) {
		addEventIfRunning(dto);
	}
	
	public void postCodeEvent(IdeCodeEventRequest dto, String eventType) {
		dto.setEventType(eventType);
		addEventIfRunning(dto);
	}
	
	public void postCodeElementEvent(IdeCodeElementEventRequest dto, String eventType) {
		dto.setEventType(eventType);
		addEventIfRunning(dto);
	}
	
	public void postDocumentEvent(IdeDocumentEventRequest dto, String eventType) {
		dto.setEventType(eventType);
		addEventIfRunning(dto);
	}
	
	public void postProjectEvent(IdeProjectEventRequest dto, String eventType) {
		dto.setEventType(eventType);
		addEventIfRunning(dto);
	}
	
	public void postIdeStateChangeEvent(IdeStateChangeEventRequest dto) {		
		addEventIfRunning(dto);
	}

	private void addEventIfRunning(IdeEventRequest dto) {
		if(isRunning) {
			eventCache.addEventOrTrace(dto);
		}
	}
}
