package com.gratex.perconik.useractivity.app.watchers;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/generic")
public class GenericEventWatcherSvc {
	static final WatcherSvcReqHandler watcherSvcReqHandler = new WatcherSvcReqHandler(); 
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/event")
	public Response postGenericEvent(String eventData) throws Exception {
		return watcherSvcReqHandler.handle(eventData, null);
	}
}



