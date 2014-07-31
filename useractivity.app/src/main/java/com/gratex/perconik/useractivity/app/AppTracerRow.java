package com.gratex.perconik.useractivity.app;

import java.util.Date;

public class AppTracerRow {
  private String message;
  private MessageSeverity severity;
  private Date time;

  public AppTracerRow(String message, MessageSeverity severity, Date time) {
    this.message = message;
    this.severity = severity;
    this.time = time;
  }

  public String getMessage() {
    return this.message;
  }

  public MessageSeverity getSeverity() {
    return this.severity;
  }

  public Date getTime() {
    return this.time;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append(this.time);
    builder.append("; ");
    builder.append(this.severity);
    builder.append("; ");
    builder.append(this.message);

    return builder.toString();
  }
}
