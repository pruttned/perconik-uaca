package com.gratex.perconik.useractivity.app;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/uaca")
@SuppressWarnings("static-method")
public class AppSvc {
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/window")
  public void postWindow() {
    App.getInstance().showMainWindow();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/exit")
  public void postExit() {
    System.exit(0);
  }
}
