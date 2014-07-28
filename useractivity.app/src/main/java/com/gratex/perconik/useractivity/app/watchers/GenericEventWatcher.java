package com.gratex.perconik.useractivity.app.watchers;

import java.io.IOException;
import java.util.UUID;

import com.gratex.perconik.useractivity.app.AppTracer;
import com.gratex.perconik.useractivity.app.EventCache;
import com.gratex.perconik.useractivity.app.SerializedEventReader;
import com.gratex.perconik.useractivity.app.SerializedEventWriter;
import com.gratex.perconik.useractivity.app.Settings;
import com.gratex.perconik.useractivity.app.XMLGregorianCalendarHelper;

public class GenericEventWatcher implements IWatcher {
	// http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	private static class LazyHolder {
		private static final GenericEventWatcher INSTANCE = new GenericEventWatcher();
	}

	private boolean isRunning = false;
	private EventCache eventCache;
		
	private GenericEventWatcher() {
	}
	
	public static GenericEventWatcher getInstance() {
		return LazyHolder.INSTANCE;
	}

	@Override
	public String getDisplayName() {
		return "Generic";
	}
	
	@Override
	public void start(EventCache eventCache) {
		this.eventCache = eventCache;
		isRunning = true;
	}

	@Override
	public void stop() {
		isRunning = false;
	}
	
	public void postGenericEvent(String serializedEvent) {		
		addEventIfRunning(serializedEvent);
	}
		
	private void addEventIfRunning(String serializedEvent) {
		if(isRunning) {
			try {
				SerializedEventReader reader = new SerializedEventReader(serializedEvent);
				SerializedEventWriter writer = new SerializedEventWriter(serializedEvent);
				
				if(!reader.hasEventTypeUri()) {
					AppTracer.getInstance().writeError(String.format("EventTypeUri is not set.\n\nEvent:\n\n%s", serializedEvent));				
				}
				else {
					if(reader.hasTimestamp()) {
						writer.setTimestamp(XMLGregorianCalendarHelper.toUtc(reader.getTimestamp())); //ensure UTC
					}
					else {
						writer.setTimestamp(XMLGregorianCalendarHelper.createUtcNow());
					}
					
					if(!reader.hasEventId()) {
						writer.setEventId(UUID.randomUUID().toString());
					}
					
					if(!reader.hasUser()) {
						writer.setUser(Settings.getInstance().getUserName());
					}
					
					if(!reader.hasWorkstation()) {
						writer.setWorkstation(Settings.getInstance().getWorkstationName());
					}
					
					serializedEvent = writer.getData();
					reader = new SerializedEventReader(serializedEvent);
					eventCache.addEventOrTrace(reader.getEventId(), reader.getTimestamp(), serializedEvent);
				}
			} catch (IOException ex) {
				AppTracer.getInstance().writeError(String.format("Cannot read/write the serialized event.\n\nSerialized event:\n\n%s", serializedEvent), ex);
			}
		}
	}
}