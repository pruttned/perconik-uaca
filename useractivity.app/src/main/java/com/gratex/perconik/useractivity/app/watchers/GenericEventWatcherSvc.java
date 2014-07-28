package com.gratex.perconik.useractivity.app.watchers;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/generic")
public class GenericEventWatcherSvc {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/event")
	public void postGenericEvent(String serializedEvent) {
		//TODO: return 'bad request' on error
		GenericEventWatcher.getInstance().postGenericEvent(serializedEvent);
	}
}
