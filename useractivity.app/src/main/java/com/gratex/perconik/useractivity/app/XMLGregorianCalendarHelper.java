package com.gratex.perconik.useractivity.app;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jersey.repackaged.com.google.common.base.Throwables;

public class XMLGregorianCalendarHelper {
	private static final DatatypeFactory datatypeFactory;
	
	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException ex) {
			throw Throwables.propagate(ex);
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
		return SimpleDateFormat.getInstance().format(xmlCalendar.toGregorianCalendar().getTime());
	}
}
