package com.gratex.perconik.useractivity.app.watchers;

import com.gratex.perconik.useractivity.app.EventCache;
import com.gratex.perconik.useractivity.app.dto.BashCommandEventDto;

public class BashCommandWatcher implements IWatcher {
	// http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	private static class LazyHolder {
		private static final BashCommandWatcher INSTANCE = new BashCommandWatcher();
	}

	private boolean isRunning = false;
	private EventCache eventCache;
		
	private BashCommandWatcher() {
	}
	
	public static BashCommandWatcher getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	@Override
	public String getDisplayName() {
		return "BashCommand";
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
	
	public void postBashCommandEvent(BashCommandEventDto dto) {
		addEventIfRunning(dto);
	}
	
	private void addEventIfRunning(BashCommandEventDto dto) {
		if(isRunning) {
			eventCache.addEventOrTrace(dto);
		}
	}
}