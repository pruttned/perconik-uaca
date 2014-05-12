package com.gratex.perconik.useractivity.app;

import javax.ws.rs.core.UriBuilder;

public class TypeUriBuilder {
	private static final String BASE_URI = "http://perconik.gratex.com/UserActivity";
	private static final String EVENT_BASE_URI = UriBuilder.fromPath(BASE_URI).path("Event").build().toString();
	
	/*
	 * http://.../Event/[relativeUri]
	 */
	public static String getEventUri(String relativeUri) {
		ValidationHelper.checkStringArgNotNullOrWhitespace(relativeUri, "relativeUri");
		return UriBuilder.fromPath(EVENT_BASE_URI).path(relativeUri).build().toString();
	}
}