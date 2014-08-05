package com.gratex.perconik.useractivity.app.logging;

import javax.xml.datatype.XMLGregorianCalendar;

import com.gratex.perconik.useractivity.app.XmlGregorianCalendarHelper;

public class LogRecord {
  private XMLGregorianCalendar timestamp;
  private int severity;
  private String message;

  public LogRecord(XMLGregorianCalendar timestamp, int severity, String message) {
    this.timestamp = timestamp;
    this.severity = severity;
    this.message = message;
  }

  public XMLGregorianCalendar getTimestamp() {
    return this.timestamp;
  }

  public int getSeverity() {
    return this.severity;
  }
  
  public String getMessage() {
    return this.message;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    return builder.append(XmlGregorianCalendarHelper.toLocalString(this.timestamp))
                  .append("; ")
                  .append(this.severity)
                  .append("; ")
                  .append(this.message)
                  .toString();
  }
}