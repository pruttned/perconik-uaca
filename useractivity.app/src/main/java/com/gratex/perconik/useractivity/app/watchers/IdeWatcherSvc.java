package com.gratex.perconik.useractivity.app.watchers;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import com.gratex.perconik.useractivity.app.dto.ide.*;

@Path("/ide")
public class IdeWatcherSvc {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/checkin")
	public void postCheckinEvent(IdeCheckinEventRequest dto) {
		IdeWatcher.getInstance().postCheckinEvent(dto);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/find")
	public void postFindEvent(IdeFindEventRequest dto) {
		IdeWatcher.getInstance().postFindEvent(dto);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/code/{eventType:(selectionchanged|paste|copy|cut)}")
	public void postCodeEvent(IdeCodeEventRequest dto, @PathParam("eventType") String eventType) {
		IdeWatcher.getInstance().postCodeEvent(dto, eventType);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/codeelement/{eventType:(visiblestart|visibleend|editstart|editend)}")
	public void postCodeElementEvent(IdeCodeElementEventRequest dto, @PathParam("eventType") String eventType) {
		IdeWatcher.getInstance().postCodeElementEvent(dto, eventType);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/document/{eventType:(switchto|add|open|close|remove|rename|save)}")
	public void postDocumentEvent(IdeDocumentEventRequest dto, @PathParam("eventType") String eventType) {
		IdeWatcher.getInstance().postDocumentEvent(dto, eventType);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/project/{eventType:(switchto|add|remove|rename|open|close|refresh)}")
	public void postProjectEvent(IdeProjectEventRequest dto, @PathParam("eventType") String eventType) {
		IdeWatcher.getInstance().postProjectEvent(dto, eventType);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/idestatechange")
	public void postIdeStateChangeEvent(IdeStateChangeEventRequest dto) {
		IdeWatcher.getInstance().postIdeStateChangeEvent(dto);
	}
}
