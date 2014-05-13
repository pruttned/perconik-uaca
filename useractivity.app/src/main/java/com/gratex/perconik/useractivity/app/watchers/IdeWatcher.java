package com.gratex.perconik.useractivity.app.watchers;

import javax.ws.rs.PathParam;
import com.gratex.perconik.useractivity.app.EventCache;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeCheckinEventRequest;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeCodeElementEventRequest;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeCodeEventRequest;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeDocumentEventRequest;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeFindEventRequest;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeProjectEventRequest;
import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeStateChangeEventRequest;

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
	
	public void postCheckinEvent(IdeCheckinEventRequest req) {
	}
	
	public void postFindEvent(IdeFindEventRequest req) {
	}
	
	public void postCodeEvent(IdeCodeEventRequest req, String eventType) {
	}
	
	public void postCodeElementEvent(IdeCodeElementEventRequest req, String eventType) {
	}
	
	public void postDocumentEvent(IdeDocumentEventRequest req, String eventType) {
	}
	
	public void postProjectEvent(IdeProjectEventRequest req, @PathParam("eventType") String eventType) {
	}
	
	public void postIdeStateChangeEvent(IdeStateChangeEventRequest req) {
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
