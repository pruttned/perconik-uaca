package com.gratex.perconik.useractivity.app.watchers.ide;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import com.gratex.perconik.useractivity.app.watchers.ide.dto.*;
import com.ua.meta.Describe;

@Path("/ide")
public class IdeWatcherSvc {
	
	Describe describe = new Describe();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/checkin")
	public void postCheckinEvent(IdeCheckinEventRequest req) {
		System.out.println("====checkin====");
		System.out.println(describe.describe(req));
		//printBaseProperties(req);
		//System.out.format("ChangesetIdInRcs = %s%n", req.getChangesetIdInRcs());
		//printRcsServer(req.getRcsServer());
		
		System.out.println("===============");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/find")
	public void postFindEvent(IdeFindEventRequest req) {
		System.out.println("====find====");
		System.out.println(describe.describe(req));
		System.out.println("===============");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/code/{eventType:(selectionchanged|paste|copy|cut)}")
	public void postCodeEvent(IdeCodeEventRequest req, @PathParam("eventType") String eventType) {
		System.out.format("====code(%s)====%n", eventType);
		System.out.println(describe.describe(req));
		System.out.println("===============");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/codeelement/{eventType:(visiblestart|visibleend|editstart|editend)}")
	public void postCodeElementEvent(IdeCodeElementEventRequest req, @PathParam("eventType") String eventType) {
		System.out.format("====codeElement(%s)====%n", eventType);
		System.out.println(describe.describe(req));
		System.out.println("===============");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/document/{eventType:(switchto|add|open|close|remove|rename|save)}")
	public void postDocumentEvent(IdeDocumentEventRequest req, @PathParam("eventType") String eventType) {
		System.out.format("====document(%s)====%n", eventType);
		System.out.println(describe.describe(req));
		System.out.println("===============");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/project/{eventType:(switchto|add|remove|rename|open|close|refresh)}")
	public void postProjectEvent(IdeProjectEventRequest req, @PathParam("eventType") String eventType) {
		System.out.format("====project(%s)====%n", eventType);
		System.out.println(describe.describe(req));
		System.out.println("===============");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/idestatechange")
	public void postIdeStateChangeEvent(IdeStateChangeEventRequest req) {
		System.out.println("====idestatechange====");
		System.out.println(describe.describe(req));
		System.out.println("===============");
	}
	
	/*
	private void printBaseProperties(BaseIdeEventRequest req){
		System.out.format("SessionId = %s%n", req.getSessionId());
		System.out.format("AppName = %s%n", req.getAppName());
		System.out.format("AppVersion = %s%n", req.getAppVersion());
		System.out.format("ProjectName = %s%n", req.getProjectName());
		System.out.format("SolutionName = %s%n", req.getSolutionName());
	}
	
	private void printRcsServer(RcsServer srv){
		System.out.println("RcsServer = ");
		System.out.format("\tTypeUri = %s%n", srv.getTypeUri());
		System.out.format("\tUrl = %s%n", srv.getUrl());
	}
	
	private void printIdeDocument(RcsServer srv){
		System.out.println("RcsServer = ");
		System.out.format("\tTypeUri = %s%n", srv.getTypeUri());
		System.out.format("\tUrl = %s%n", srv.getUrl());
	}
	*/
	
}
