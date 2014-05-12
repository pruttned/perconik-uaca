package com.gratex.perconik.useractivity.app.watchers.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class TestSvc {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public MyObj getEmployee() {
		MyObj obj = new MyObj();
		obj.setName("John");
		return obj;
	}
	
	@POST
	@Consumes("text/plain")
	public void postTest(String value){
		System.out.println(value);
	}
}
