package com.gratex.perconik.useractivity.app.dto;

import javax.ws.rs.core.UriBuilder;
import com.gratex.perconik.useractivity.app.dto.ApplicationEventDto;

public class BashCommandEventDto extends ApplicationEventDto {
	private String commandName;
	private String commandParameters;
	
	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public String getCommandParameters() {
		return commandParameters;
	}

	public void setCommandParameters(String commandParameters) {
		this.commandParameters = commandParameters;
	}

	@Override	
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("bash/command");
	}
}