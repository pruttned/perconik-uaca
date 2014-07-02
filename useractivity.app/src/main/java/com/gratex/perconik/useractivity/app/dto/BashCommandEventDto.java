package com.gratex.perconik.useractivity.app.dto;

import javax.ws.rs.core.UriBuilder;
import com.gratex.perconik.useractivity.app.dto.ApplicationEventDto;

public class BashCommandEventDto extends ApplicationEventDto {
	private String commandLine;
	private int commandId;

	public String getCommandLine() {
		return commandLine;
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	@Override	
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("bash/command");
	}
}