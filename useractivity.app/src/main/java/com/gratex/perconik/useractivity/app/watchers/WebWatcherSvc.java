package com.gratex.perconik.useractivity.app.watchers;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import com.gratex.perconik.useractivity.app.dto.web.WebBookmarkEventDto;
import com.gratex.perconik.useractivity.app.dto.web.WebCopyEventDto;
import com.gratex.perconik.useractivity.app.dto.web.WebNavigateEventDto;
import com.gratex.perconik.useractivity.app.dto.web.WebSaveEventDto;
import com.gratex.perconik.useractivity.app.dto.web.WebTabEventDto;

@Path("/web")
public class WebWatcherSvc {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/navigate")
	public void postNavigateEvent(WebNavigateEventDto dto) {
		WebWatcher.getInstance().postNavigateEvent(dto);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/copy")
	public void postCopyEvent(WebCopyEventDto dto) {
		WebWatcher.getInstance().postCopyEvent(dto);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/save")
	public void postSaveEvent(WebSaveEventDto dto) {
		WebWatcher.getInstance().postSaveEvent(dto);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/bookmar")
	public void postBookmarkEvent(WebBookmarkEventDto dto) {
		WebWatcher.getInstance().postBookmarkEvent(dto);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/tab/{eventType:(switchto|open|close)}")
	public void postTabEvent(WebTabEventDto dto, @PathParam("eventType") String eventType) {
		WebWatcher.getInstance().postTabEvent(dto, eventType);
	}
}
