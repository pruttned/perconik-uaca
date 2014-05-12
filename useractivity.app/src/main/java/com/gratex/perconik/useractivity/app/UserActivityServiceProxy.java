package com.gratex.perconik.useractivity.app;

import com.gratex.perconik.useractivity.app.dto.CachedEvent;

public class UserActivityServiceProxy {

	public void commitEvent(CachedEvent cachedEvent) {
		ValidationHelper.checkArgNotNull(cachedEvent, "cachedEvent");

		// TODO
		AppTracer.getInstance().writeInfo("PROXY - sending: " + cachedEvent.getEventId());
	}
}
