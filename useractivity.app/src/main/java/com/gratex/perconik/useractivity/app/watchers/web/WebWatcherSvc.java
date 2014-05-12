package com.gratex.perconik.useractivity.app.watchers.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import com.gratex.perconik.useractivity.app.watchers.ide.dto.IdeCodeElementEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.WebBookmarkEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.WebCopyEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.WebNavigateEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.WebSaveEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.WebTabEventRequest;
import com.ua.meta.Describe;

@Path("/web")
public class WebWatcherSvc {
	Describe describe = new Describe();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/navigate")
	public void postNavigateEvent(WebNavigateEventRequest req) {
		System.out.println("====navigate====");
		System.out.println(describe.describe(req));
		System.out.println("----");
	}	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/copy")
	public void postCopyEvent(WebCopyEventRequest req) {
		System.out.println("===copy=====");
		System.out.println(describe.describe(req));
		System.out.println("----");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/save")
	public void postSaveEvent(WebSaveEventRequest req) {
		System.out.println("===save=====");
		System.out.println(describe.describe(req));
		System.out.println("----");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/bookmar")
	public void postBookmarkEvent(WebBookmarkEventRequest req) {
		System.out.println("====bookmark====");
		System.out.println(describe.describe(req));
		System.out.println("----");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/tab/{eventType:(switchto|open|close)}")
	public void postTabEvent(WebTabEventRequest req, @PathParam("eventType") String eventType) {
		System.out.format("====tab(%s)====%n", eventType);
		System.out.println(describe.describe(req));
		System.out.println("----");
	}
}
