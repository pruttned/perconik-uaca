package com.gratex.perconik.useractivity.app;

import java.io.IOException;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Writes/Read into serialized event string (e.g. CahcedEvent.getData())
 */
public class EventDocument {
	private static final ObjectMapper mapper = new ObjectMapper(); //thread safe
	private static final ObjectMapper mapperFormated = new ObjectMapper(); //thread safe
	private ObjectNode dataTree;

	static {
		mapperFormated.configure(SerializationFeature.INDENT_OUTPUT, true);
	  }
	
	/**
	 * Loads EventDocument from the json svc requesta and also ensures/fix required properties
	 * @return
	 * @throws IOException 
	 */
	public static EventDocument LoadFromRequest(String jsonStr) throws IOException{
		EventDocument doc = new EventDocument(jsonStr);
		doc.fixAfterReceive();
		return doc;
	}

	public EventDocument(String jsonStr) throws IOException {
		if(ValidationHelper.isStringNullOrWhitespace(jsonStr)){
			dataTree = JsonNodeFactory.instance.objectNode();
		}
		dataTree = (ObjectNode)mapper.readTree(jsonStr);
	}
	
	public String toJsonString() throws JsonProcessingException {
		return mapper.writeValueAsString(dataTree);
	}
	
	public String toFormatedJsonString() throws JsonProcessingException {
		return mapperFormated.writeValueAsString(dataTree);
	}

	public boolean hasEventTypeUri() {
		return dataTree.hasNonNull("eventTypeUri");
	}
	/**
	 * 
	 * @return event type uir or null
	 */
	public String getEventTypeUri() {
		return tryGetNodeAsString("eventTypeUri");
	}
	
	public void setEventTypeUri(String uri){
		dataTree.put("eventTypeUri", uri);
	}
	
	/**
	 * For code/paste
	 * @return event text field or null
	 */
	public String getText() {
		return tryGetNodeAsString("text");
	}

	/**
	 * For code/paste
	 */
	public void setWebUrl(String url){
		dataTree.put("webUrl", url);
	}

	/**
	 * For web/copy
	 * @return event text field or null
	 */
	public String getUrl() {
		return tryGetNodeAsString("url");
	}
	
	/**
	 * For web/copy
	 * @return event text field or null
	 */
	public String getContent() {
		return tryGetNodeAsString("content");
	}
	
	public void setWasCommitForcedByUser(boolean wasCommitForcedByUser) {
		dataTree.put("wasCommitForcedByUser", wasCommitForcedByUser);
	}
	
	public boolean hasTimestamp() {
		return dataTree.hasNonNull("timestamp");
	}
	
	/**
	 * 
	 * @return timestamp or null
	 */
	public XMLGregorianCalendar getTimestamp() {
		JsonNode node = dataTree.findValue("timestamp");
		if(node == null){
			return null;
		}
		
		try	{
			return XMLGregorianCalendarHelper.fromString(node.asText());
		}catch(IllegalArgumentException ex){
			throw new IllegalArgumentException("Invalid timestamp format");
		}
	}
	
	public void setTimestamp(XMLGregorianCalendar timestamp) {
		dataTree.put("timestamp", timestamp.toString());
	}
	
	public boolean hasEventId() {
		return dataTree.hasNonNull("eventId");
	}
	
	/**
	 * 
	 * @return eventId or null
	 */
	public String getEventId() {
		return tryGetNodeAsString("eventId");
	}

	public void setEventId(String eventId) {
		dataTree.put("eventId", eventId);
	}
	
	public boolean hasUser() {
		return dataTree.hasNonNull("user");
	}
	
	public void setUser(String user) {
		dataTree.put("user", user);
	}
	
	public boolean hasWorkstation() {
		return dataTree.hasNonNull("workstation");
	}
	
	public void setWorkstation(String workstation) {
		dataTree.put("workstation", workstation);
	}
	
	private void fixAfterReceive(){
		//ensure timestamp + ToUtc
		XMLGregorianCalendar timestamp = getTimestamp();
		if(timestamp != null){
			setTimestamp(XMLGregorianCalendarHelper.toUtc(timestamp)); //ensure UTC
		}
		else{
			setTimestamp(XMLGregorianCalendarHelper.createUtcNow());
		}
		
		//eventId
		if(!hasEventId()) {
			setEventId(UUID.randomUUID().toString());
		}
		
		//user
		setUser(Settings.getInstance().getUserName());
		
		//workstation
		setWorkstation(Settings.getInstance().getWorkstationName());
	}
	
	private String tryGetNodeAsString(String nodeName){
		JsonNode node = dataTree.findValue(nodeName);
		if(node == null){
			return null;
		}
		return node.asText();
	}

}