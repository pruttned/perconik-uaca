package com.gratex.perconik.useractivity.app;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

public class AppTracer {
  private static final AppTracer INSTANCE = new AppTracer();
  private ArrayList<AppTracerRow> rows = new ArrayList<>();
  private Object syncObj = new Object();

  private AppTracer() {}

  public static AppTracer getInstance() {
    return INSTANCE;
  }

  public AppTracerRow[] getRows() {
    synchronized (this.syncObj) {
      return this.rows.toArray(new AppTracerRow[this.rows.size()]);
    }
  }

  public void writeError(String message, Throwable exception) {
    this.writeError(String.format("%s%nError:%s", message, getExceptionFullText(exception)));
  }

  public void writeError(Throwable exception) {
    this.writeError(getExceptionFullText(exception));
  }

  public void writeError(String message) {
    this.write(message, MessageSeverity.ERROR);
  }

  public void writeWarning(String message) {
    this.write(message, MessageSeverity.WARNING);
  }

  public void writeInfo(String message) {
    this.write(message, MessageSeverity.INFO);
  }

  public void write(String message, MessageSeverity severity) {
    synchronized (this.syncObj) {
      this.rows.add(new AppTracerRow(message, severity, new Date()));

      if (this.rows.size() > Settings.getInstance().getMaxRowCountInLog()) {
        this.rows.remove(0);
      }
    }
  }

  public void clear() {
    synchronized (this.syncObj) {
      this.rows.clear();
    }
  }

  public static String getExceptionFullText(Throwable exception) {
    if (exception == null) {
      return "";
    }

    StringWriter stringWriter = new StringWriter();
    exception.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }

  public void ensureMaxRowCount() {
    if (this.rows.size() > Settings.getInstance().getMaxRowCountInLog()) {
      int newStartIndex = this.rows.size() - Settings.getInstance().getMaxRowCountInLog();
      this.rows = new ArrayList<>(this.rows.subList(newStartIndex, this.rows.size()));
    }
  }
}
