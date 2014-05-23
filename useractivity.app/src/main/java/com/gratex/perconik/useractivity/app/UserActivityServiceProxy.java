package com.gratex.perconik.useractivity.app;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import com.gratex.perconik.useractivity.app.dto.CachedEvent;

public class UserActivityServiceProxy {
	Client client = ClientBuilder.newClient();
	WebTarget baseSvcUrl;

	public UserActivityServiceProxy() {
		baseSvcUrl = client.target(Settings.getInstance().getSvcUrl());
	}

	public void setSvcUrl(String url){
		baseSvcUrl = client.target(url);
	}
	
	public void commitEvent(CachedEvent cachedEvent) throws SvcException {
		ValidationHelper.checkArgNotNull(cachedEvent, "cachedEvent");

		AppTracer.getInstance().write(String.format("Committing event '%s'... \n\nData:\n\n%s", cachedEvent.getEventId(), getEventFormattedData(cachedEvent)), MessageSeverity.INFO_EVENT_COMMIT);

		WebTarget fullTarget = baseSvcUrl.path(cachedEvent.getEventId()
				.toString());
		Response response = fullTarget.request().put(
				Entity.json(cachedEvent.getData()));
		try {
			StatusType status = response.getStatusInfo();
			if (status.getFamily() == Family.CLIENT_ERROR
					|| status.getFamily() == Family.SERVER_ERROR) {
				StringBuilder strb = new StringBuilder();
				strb.append("Request to Uaca failed. Details: PUT ")
						.append(fullTarget.getUri().toString())
						.append(" returned ").append(status.getStatusCode())
						.append(" ").append(status.getReasonPhrase());
				if (response.getLength() > 0) {
					String content = response.readEntity(String.class);
					strb.append("Content: ").append(content);
				}
				throw new SvcException(strb.toString());
			}
		} finally {
			response.close();
		}
	}
	
	private String getEventFormattedData(CachedEvent cachedEvent) {
		try {
			return new SerializedEventReader(cachedEvent.getData()).getFormattedData();
		} catch (IOException ex) {
			AppTracer.getInstance().writeError(String.format("Failed to deserialize the event with ID '%s'", cachedEvent.getEventId()), ex);
			return "<ERROR - see log for details>";
		}
	}
}
