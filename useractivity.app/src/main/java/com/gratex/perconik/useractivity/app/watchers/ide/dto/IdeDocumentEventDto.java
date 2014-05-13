package com.gratex.perconik.useractivity.app.watchers.ide.dto;

public class IdeDocumentEventDto extends BaseIdeEventDto {
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
}