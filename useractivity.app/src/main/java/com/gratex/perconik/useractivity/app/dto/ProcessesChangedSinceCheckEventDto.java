package com.gratex.perconik.useractivity.app.dto;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

public class ProcessesChangedSinceCheckEventDto extends SystemEventDto {
  private List<ProcessDto> startedProcesses;
  private List<ProcessDto> killedProcesses;

  public List<ProcessDto> getStartedProcesses() {
    if (this.startedProcesses != null) {
      return this.startedProcesses;
    }
    return new ArrayList<>();
  }

  public void setStartedProcesses(List<ProcessDto> startedProcesses) {
    this.startedProcesses = startedProcesses;
  }

  public List<ProcessDto> getKilledProcesses() {
    if (this.killedProcesses != null) {
      return this.killedProcesses;
    }
    return new ArrayList<>();
  }

  public void setKilledProcesses(List<ProcessDto> killedProcesses) {
    this.killedProcesses = killedProcesses;
  }

  @Override
  protected UriBuilder getDefaultEventTypeUri() {
    return super.getDefaultEventTypeUri().path("processeschangedsincechceck");
  }
}
