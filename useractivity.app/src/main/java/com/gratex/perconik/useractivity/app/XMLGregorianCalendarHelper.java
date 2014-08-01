package com.gratex.perconik.useractivity.app;

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarHelper {
  private static final DatatypeFactory datatypeFactory;

  static {
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static XMLGregorianCalendar createUtcNow() {
    GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    return datatypeFactory.newXMLGregorianCalendar(calendar);
  }

  public static XMLGregorianCalendar createUtc(long utcMilliseconds) {
    GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    calendar.setTimeInMillis(utcMilliseconds);
    return datatypeFactory.newXMLGregorianCalendar(calendar);
  }

  public static long getMilliseconds(XMLGregorianCalendar xmlCalendar) {
    return xmlCalendar.toGregorianCalendar().getTimeInMillis();
  }

  public static String toLocalString(XMLGregorianCalendar xmlCalendar) {
    return DateFormat.getInstance().format(xmlCalendar.toGregorianCalendar().getTime());
  }

  public static TimeType getTimeType(XMLGregorianCalendar xmlCalendar) {
    int timeZone = xmlCalendar.getTimezone();

    if (timeZone == 0) {
      return TimeType.UTC;
    }

    if (timeZone == DatatypeConstants.FIELD_UNDEFINED) {
      return TimeType.UNSPECIFIED;
    } else {
      return TimeType.LOCAL;
    }
  }

  public static XMLGregorianCalendar toUtc(XMLGregorianCalendar xmlCalendar) {
    TimeType timeType = getTimeType(xmlCalendar);

    if (timeType == TimeType.UTC) {
      return xmlCalendar;
    }

    if (timeType == TimeType.LOCAL) {
      return xmlCalendar.normalize();
    }

    //UNSPECIFIED
    GregorianCalendar calendar = new GregorianCalendar(); //to get local time zone offset
    xmlCalendar.setTimezone(calendar.getTimeZone().getOffset(calendar.getTimeInMillis()) / 60000); //convert to local
    return xmlCalendar.normalize(); //to UTC
  }

  public static XMLGregorianCalendar fromString(String xmlFormat) {
    return datatypeFactory.newXMLGregorianCalendar(xmlFormat.trim());
  }
}
