package com.gratex.perconik.useractivity.app;

public class ValidationHelper {
	public static void checkArgNotNull(Object value, String name) {
		if(name == null) throw new IllegalArgumentException("'name' must not be null");
		if(value == null) throw new IllegalArgumentException(String.format("'%s' must not be null.", name));
	}

	public static void checkStringArgNotNullOrWhitespace(String value, String name) {
		checkArgNotNull(name, "name");
		if(isStringNullOrWhitespace(value)) throw new IllegalArgumentException(String.format("'%s' must not be empty or contain only whitespaces.", name));
	}
	
	public static boolean isStringNullOrWhitespace(String value) {
		return value == null || value.trim().isEmpty();
	}
}
