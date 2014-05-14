package com.gratex.perconik.useractivity.app.dto.ide;

import javax.ws.rs.core.UriBuilder;

public class IdeDocumentEventDto extends IdeEventDto {
	/**
	 * Document that has been subject of this event
	 */
	private IdeDocumentDto document;

	/**
	 * @return the {@link #document}
	 */
	public IdeDocumentDto getDocument() {
		return document;
	}
	/**
	 * @param {@link #document}
	 */
	public void setDocument(IdeDocumentDto document) {
		this.document = document;
	}
	
	public void setEventType(String eventType) {
		setEventTypeUri(UriBuilder.fromPath(getEventTypeUri()).path(eventType).build().toString());
	}
	
	@Override
	protected UriBuilder getDefaultEventTypeUri() {
		return super.getDefaultEventTypeUri().path("document");
	}
}