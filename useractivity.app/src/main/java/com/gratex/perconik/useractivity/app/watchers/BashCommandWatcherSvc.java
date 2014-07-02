package com.gratex.perconik.useractivity.app.watchers;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.gratex.perconik.useractivity.app.dto.BashCommandEventDto;

@Path("/bash")
public class BashCommandWatcherSvc {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/command")
	public void postBashCommandEvent(BashCommandEventDto dto) {
		BashCommandWatcher.getInstance().postBashCommandEvent(dto);
	}
}
