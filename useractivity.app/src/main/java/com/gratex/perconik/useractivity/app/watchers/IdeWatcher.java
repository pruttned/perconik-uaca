package com.gratex.perconik.useractivity.app.watchers;

import com.gratex.perconik.useractivity.app.EventCache;
import com.gratex.perconik.useractivity.app.dto.ide.IdeCheckinEventDto;
import com.gratex.perconik.useractivity.app.dto.ide.IdeCodeElementEventDto;
import com.gratex.perconik.useractivity.app.dto.ide.IdeCodeEventDto;
import com.gratex.perconik.useractivity.app.dto.ide.IdeDocumentEventDto;
import com.gratex.perconik.useractivity.app.dto.ide.IdeEventDto;
import com.gratex.perconik.useractivity.app.dto.ide.IdeFindEventDto;
import com.gratex.perconik.useractivity.app.dto.ide.IdeProjectEventDto;
import com.gratex.perconik.useractivity.app.dto.ide.IdeStateChangeEventDto;

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
	
	public void postCheckinEvent(IdeCheckinEventDto dto) {
		addEventIfRunning(dto);
	}
	
	public void postFindEvent(IdeFindEventDto dto) {
		addEventIfRunning(dto);
	}
	
	public void postCodeEvent(IdeCodeEventDto dto, String eventType) {
		dto.setEventType(eventType);
		addEventIfRunning(dto);
	}
	
	public void postCodeElementEvent(IdeCodeElementEventDto dto, String eventType) {
		dto.setEventType(eventType);
		addEventIfRunning(dto);
	}
	
	public void postDocumentEvent(IdeDocumentEventDto dto, String eventType) {
		dto.setEventType(eventType);
		addEventIfRunning(dto);
	}
	
	public void postProjectEvent(IdeProjectEventDto dto, String eventType) {
		dto.setEventType(eventType);
		addEventIfRunning(dto);
	}
	
	public void postIdeStateChangeEvent(IdeStateChangeEventDto dto) {		
		addEventIfRunning(dto);
	}

	private void addEventIfRunning(IdeEventDto dto) {
		if(isRunning) {
			eventCache.addEventOrTrace(dto);
		}
	}
}
