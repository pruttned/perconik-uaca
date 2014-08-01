package com.gratex.perconik.useractivity.app;

import java.util.Date;

public final class AppTracerRow {
  private final String message;
  private final MessageSeverity severity;
  private final Date time;

  public AppTracerRow(String message, MessageSeverity severity, Date time) {
    this.message = message;
    this.severity = severity;
    this.time = new Date(time.getTime());
  }

  public String getMessage() {
    return this.message;
  }

  public MessageSeverity getSeverity() {
    return this.severity;
  }

  public Date getTime() {
    return new Date(this.time.getTime());
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
