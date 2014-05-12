package com.gratex.perconik.useractivity.app.watchers.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.gratex.perconik.useractivity.app.watchers.web.dto.PostBookmarkEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.PostCopyEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.PostNavigateEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.PostSaveEventRequest;
import com.gratex.perconik.useractivity.app.watchers.web.dto.PostTabEventRequest;

@Path("/web")
public class WebWatcherSvc {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/navigateevent")
	public void postNavigateEvent(PostNavigateEventRequest req) {
		System.out.println("====navigateevent====");
		System.out.println(req.getSessionId());
		System.out.println(req.getAppName());
		System.out.println(req.getAppVersion());
		System.out.println(req.getTabId());
		System.out.println(req.getUrl());
		System.out.println(req.getTransitionType());
		
		//ak bude getTransitionType ,tak sa musi vytvorit root navigation..t.j. iba ./navigation miesto ./navigation/bylink
		
		System.out.println("----");
	}	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/copyevent")
	public void postCopyEvent(PostCopyEventRequest req) {
		System.out.println("===copyevent=====");
		System.out.println(req.getSessionId());
		System.out.println(req.getAppName());
		System.out.println(req.getAppVersion());
		System.out.println(req.getTabId());
		System.out.println(req.getUrl());
		System.out.println(req.getContent());
		System.out.println("----");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/saveevent")
	public void postSaveEvent(PostSaveEventRequest req) {
		System.out.println("===saveevent=====");
		System.out.println(req.getSessionId());
		System.out.println(req.getAppName());
		System.out.println(req.getAppVersion());
		System.out.println(req.getUrl());
		System.out.println(req.getName());
		System.out.println("----");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/bookmarkevent")
	public void postBookmarkEvent(PostBookmarkEventRequest req) {
		System.out.println("====bookmarkevent====");
		System.out.println(req.getSessionId());
		System.out.println(req.getAppName());
		System.out.println(req.getAppVersion());
		System.out.println(req.getUrl());
		System.out.println(req.getName());
		System.out.println("----");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/tabevent")
	public void postTabEvent(PostTabEventRequest req) {
		System.out.println("===tabevent=====");
		System.out.println(req.getSessionId());
		System.out.println(req.getAppName());
		System.out.println(req.getAppVersion());
		System.out.println(req.getTabId());
		System.out.println(req.getUrl());
		System.out.println(req.getOperation());
		System.out.println("----");
	}
}
