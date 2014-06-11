package com.gratex.perconik.useractivity.app;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Writes into serialized event string (e.g. CahcedEvent.getData())
 */
public class SerializedEventWriter {
	private static final ObjectMapper mapper = new ObjectMapper(); //thread safe
	private ObjectNode dataTree;
	
	public SerializedEventWriter(String data) throws IOException {
		ValidationHelper.checkArgNotNull(data, "data");
		dataTree = (ObjectNode)mapper.readTree(data);
	}
	
	public void setWasCommitForcedByUser(boolean wasCommitForcedByUser) {
		dataTree.put("wasCommitForcedByUser", wasCommitForcedByUser);
	}
	
	public String getData() throws JsonProcessingException {
		return mapper.writeValueAsString(dataTree);
	}
}