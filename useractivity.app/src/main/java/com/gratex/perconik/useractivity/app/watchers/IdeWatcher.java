package com.gratex.perconik.useractivity.app.watchers;

import javax.ws.rs.PathParam;
import com.gratex.perconik.useractivity.app.EventCache;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeCheckinEventDto;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeCodeElementEventDto;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeCodeEventDto;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeDocumentEventDto;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeFindEventDto;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeProjectEventDto;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeStateChangeEventDto;

public class IdeWatcher implements IWatcher {
	// http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	private static class LazyHolder {
		private static final IdeWatcher INSTANCE = new IdeWatcher();
	}

	private boolean isStarted = false;

	public static IdeWatcher getInstance() {
		return LazyHolder.INSTANCE;
	}

	public boolean isStarted() {
		return isStarted;
	}
	
	public void postCheckinEvent(IdeCheckinEventDto req) {
	}
	
	public void postFindEvent(IdeFindEventDto req) {
	}
	
	public void postCodeEvent(IdeCodeEventDto req, String eventType) {
	}
	
	public void postCodeElementEvent(IdeCodeElementEventDto req, String eventType) {
	}
	
	public void postDocumentEvent(IdeDocumentEventDto req, String eventType) {
	}
	
	public void postProjectEvent(IdeProjectEventDto req, @PathParam("eventType") String eventType) {
	}
	
	public void postIdeStateChangeEvent(IdeStateChangeEventDto req) {
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
	
	IdeWatcher() {
	}
}
