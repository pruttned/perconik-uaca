package com.gratex.perconik.useractivity.app.watchers;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.gratex.perconik.useractivity.app.EventDocument;
import com.gratex.perconik.useractivity.app.TypeUriHelper;
import com.gratex.perconik.useractivity.app.ValidationHelper;

@Singleton
@Path("/ide")
public class IdeWatcherSvc {
	static final WatcherSvcReqHandler watcherSvcReqHandler = new WatcherSvcReqHandler(); 
	static final WatcherSvcReqHandler codePasteWatcherSvcReqHandler = new WatcherSvcReqHandler(){
		@Override
		protected boolean beforeAddToCache(EventDocument doc)
		{
			//handle paste from web
			String pastedTextIntoIde = doc.getText();
			if(!ValidationHelper.isStringNullOrWhitespace(pastedTextIntoIde)){
				WebWatcherState webWatcherState = WebWatcherState.getInstance(); 
				
				pastedTextIntoIde =  pastedTextIntoIde.trim();
				String copiedTextFromWeb = webWatcherState.getLastCopyText();
				
				if(copiedTextFromWeb != null && copiedTextFromWeb.equals(pastedTextIntoIde)){
					doc.setWebUrl(webWatcherState.getLastCopyUrl());
					doc.setEventTypeUri(UriBuilder.fromPath(TypeUriHelper.EVENT_BASE_URI).path("ide").path("code").path("pastefromweb").build().toString());
				}
			}
			
			return true;
		};
	};
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/checkin")
	public Response postCheckinEvent(String eventData)  throws Exception {
		return watcherSvcReqHandler.handle(eventData, getBaseUri().path("checkin").build().toString());
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/find")
	public Response postFindEvent(String eventData)  throws Exception {
		return watcherSvcReqHandler.handle(eventData, getBaseUri().path("find").build().toString());
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/code/{eventType:(selectionchanged|paste|copy|cut)}")
	public Response postCodeEvent(String eventData, @PathParam("eventType") String eventType) throws Exception {
		if(eventType.equals("paste")){ //handle paste from web
			return codePasteWatcherSvcReqHandler.handle(eventData, getBaseUri().path("code").path(eventType).build().toString());	
		}
		return watcherSvcReqHandler.handle(eventData, getBaseUri().path("code").path(eventType).build().toString());
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/codeelement/{eventType:(visiblestart|visibleend|editstart|editend)}")
	public Response postCodeElementEvent(String eventData, @PathParam("eventType") String eventType) throws Exception {
		return watcherSvcReqHandler.handle(eventData, getBaseUri().path("codeelement").path(eventType).build().toString());
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/document/{eventType:(switchto|add|open|close|remove|rename|save)}")
	public Response postDocumentEvent(String eventData, @PathParam("eventType") String eventType) throws Exception {
		return watcherSvcReqHandler.handle(eventData, getBaseUri().path("document").path(eventType).build().toString());
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/project/{eventType:(switchto|add|remove|rename|open|close|refresh)}")
	public Response postProjectEvent(String eventData, @PathParam("eventType") String eventType)  throws Exception {
		return watcherSvcReqHandler.handle(eventData, getBaseUri().path("project").path(eventType).build().toString());
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/idestatechange")
	public Response postIdeStateChangeEvent(String eventData) throws Exception {
		return watcherSvcReqHandler.handle(eventData, getBaseUri().path("statechange").build().toString());
	}
	
	private UriBuilder getBaseUri(){
		return UriBuilder.fromPath(TypeUriHelper.EVENT_BASE_URI).path("ide");
	}
}
