package com.gratex.perconik.useractivity.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.gratex.perconik.useractivity.app.TypeUriBuilder;

public class ProcessesChangedSinceCheckEventDto extends EventDto {
	private List<ProcessDto> addedProcesses;
	private List<ProcessDto> killedProcesses;

	public ProcessesChangedSinceCheckEventDto() {
		setEventTypeUri(TypeUriBuilder.getEventUri("System/ProcessesChangedSinceChceck"));
	}

	public List<ProcessDto> getAddedProcesses() {
		if(this.addedProcesses != null) {
			return this.addedProcesses;
		}
		return new ArrayList<ProcessDto>();
	}

	public void setAddedProcesses(List<ProcessDto> addedProcesses) {
		this.addedProcesses = addedProcesses;
	}

	public List<ProcessDto> getKilledProcesses() {
		if(this.killedProcesses != null) {
			return this.killedProcesses;
		}
		return new ArrayList<ProcessDto>();
	}

	public void setKilledProcesses(List<ProcessDto> killedProcesses) {
		this.killedProcesses = killedProcesses;
	}
}
