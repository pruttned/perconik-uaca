package com.gratex.perconik.useractivity.app;

import java.io.IOException;

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Reads serialized event string (e.g. CahcedEvent.getData())
 */
public class SerializedEventReader {
	private static final ObjectMapper mapper = new ObjectMapper(); //thread safe
	private JsonNode dataTree;
	
	static {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	  }
	
	public SerializedEventReader(String data) throws IOException {
		ValidationHelper.checkArgNotNull(data, "data");
		dataTree = mapper.readTree(data);
	}
	
	public String getFormattedData() throws JsonProcessingException {
		return mapper.writeValueAsString(dataTree);
	}
	
	public String getEventTypeUri() {
		return dataTree.findValue("eventTypeUri").asText();
	}
	
	public boolean hasEventTypeUri() {
		return dataTree.hasNonNull("eventTypeUri");
	}
	
	public XMLGregorianCalendar getTimestamp() {
		String timestampString = dataTree.findValue("timestamp").asText();
		return XMLGregorianCalendarHelper.fromString(timestampString);
	}
	
	public boolean hasTimestamp() {
		return dataTree.hasNonNull("timestamp");
	}
	
	public String getEventId() {
		return dataTree.findValue("eventId").asText();
	}
	
	public boolean hasEventId() {
		return dataTree.hasNonNull("eventId");
	}
	
	public boolean hasUser() {
		return dataTree.hasNonNull("user");
	}
	
	public boolean hasWorkstation() {
		return dataTree.hasNonNull("workstation");
	}
}