package com.gratex.perconik.useractivity.app;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gratex.perconik.useractivity.app.dto.EventDto;
import com.gratex.perconik.useractivity.app.dto.CachedEvent;


/**
 * Represents the local cache of events - local DB.
 */
public class EventCache {
	private Connection connection;
	private EventSerializer eventSerializer;
	
	/**
	 * Initializes this cache and ensures that the backing store exists.
	 * NOT thread safe.
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public void initialize(EventSerializer eventSerializer) throws ClassNotFoundException, SQLException {		
		ValidationHelper.checkArgNotNull(eventSerializer, "eventSerializer");
		
		this.eventSerializer = eventSerializer;
		
		Class.forName("org.h2.Driver");
		String dbUri = "jdbc:h2:" + Paths.get(Settings.getInstance().getUserFolder(), "EventCache");
		this.connection = DriverManager.getConnection(dbUri);
		executeCreationScript();
	}

	/**
	 * Closes the connection.
	 * Thread safe.
	 * @throws SQLException 
	 */
	public void close() throws SQLException {
		if (this.connection != null) {
			synchronized(this.connection) {
				if(!connection.isClosed()) {
					this.connection.close();
				}
			}
		}
	}
	
	/**
	 * Adds the specified event into the cache.
	 * Thread safe.
	 * @throws SQLException 
	 * @throws JsonProcessingException 
	 */
	public void addEvent(EventDto event) throws SQLException, JsonProcessingException {
		ValidationHelper.checkArgNotNull(event, "event");
		
		executeThreadSafeUpdate("INSERT INTO EVENTS (EVENTID, TIMESTAMP, DATA) VALUES (?, ?, ?)", event.getEventId(), 
																							 	  XMLGregorianCalendarHelper.getMilliseconds(event.getTimestamp()), 
																							 	  this.eventSerializer.serialize(event));
	}
	
	/**
	 * Adds the specified event into the cache. If an error occurs, the error is traced and false is returned - no exception is thrown.
	 * Thread safe. 
	 */
	public boolean addEventOrTrace(EventDto event) {
		try {
			addEvent(event);
			return true;
		} catch (Exception ex) {
			AppTracer.getInstance().writeError(String.format("Failed to add the event with ID '%s' to the event cache (local DB).", event.getEventId()), ex);
			return false;
		}
	}
	
	/**
	 * Removes the event with the specified 'event ID' from the cache.
	 * Thread safe.
	 * @throws SQLException 
	 */
	public void removeEvent(UUID eventId) throws SQLException {
		ValidationHelper.checkArgNotNull(eventId, "eventId");
		executeThreadSafeUpdate("DELETE FROM EVENTS WHERE EVENTID=?", eventId);
	}
	
	/**
	 * Removes the event with the specified 'event ID' from the cache. If an error occurs, the error is traced and false is returned - no exception is thrown.
	 * Thread safe.
	 */
	public boolean removeEventOrTrace(UUID eventId) {
		try {
			removeEvent(eventId);
			return true;
		} catch (Exception ex) {
			AppTracer.getInstance().writeError(String.format("Failed to remove the event with ID '%s' from the event cache (local DB).", eventId), ex);
			return false;
		}
	}
	
	/**
	 * Removes all events from the cache.
	 * @throws SQLException
	 */
	public void removeAllEvents() throws SQLException {
		executeThreadSafeUpdate("DELETE FROM EVENTS");
	}
	
	/**
	 * Removes all events with the specified 'event IDs' from the cache.
	 * @param eventIds
	 * @throws SQLException
	 */
	public void removeEvents(ArrayList<UUID> eventIds) throws SQLException {
		ValidationHelper.checkArgNotNull(eventIds, "eventIds");
		for (UUID eventId : eventIds) {
			removeEvent(eventId);
		}
	}
	
	/**
	 * Gets events from the cache that are old enough to be committed to the server.
	 * Thread safe.
	 * @throws SQLException 
	 */
	public ArrayList<CachedEvent> getEventsToCommit() throws SQLException {
		long lastEventTimeToCommit = new Date().getTime() - Settings.getInstance().getEventAgeToCommit();
		ResultSet result = executeThreadSafeQuery(String.format("SELECT EVENTID, TIMESTAMP, DATA FROM EVENTS WHERE TIMESTAMP <= %s ORDER BY TIMESTAMP ASC", lastEventTimeToCommit));
		return convertToArrayList(result);
	}

	/**
	 * Gets all events from the cache.
	 * Thread safe.
	 * @throws SQLException 
	 */
	public ArrayList<CachedEvent> getEvents() throws SQLException {
		ResultSet result = executeThreadSafeQuery("SELECT EVENTID, TIMESTAMP, DATA FROM EVENTS ORDER BY TIMESTAMP DESC");
		return convertToArrayList(result);
	}
	
	/**
	 * Updates the user name in all events in the event cache to the current user name in the settings.
	 * Thread safe.
	 * @throws SQLException
	 */
	public void updateUserNameInAllEvents() throws SQLException {
		String userNameRegexp = "\"user\":\"[^\"]*\"";
		String newUserName = String.format("\"user\":\"%s\"", Settings.getInstance().getUserName());
		
		executeThreadSafeUpdate(String.format("UPDATE EVENTS SET DATA=REGEXP_REPLACE(DATA, '%s', '%s')", userNameRegexp, newUserName));
	}
	
	private ArrayList<CachedEvent> convertToArrayList(ResultSet queryResult) throws SQLException {
		ArrayList<CachedEvent> cachedEvents = new ArrayList<CachedEvent>();
		while(queryResult.next()) {
			cachedEvents.add(new CachedEvent((UUID)queryResult.getObject("EVENTID"), 
											 XMLGregorianCalendarHelper.createUtc(queryResult.getLong("TIMESTAMP")), 
											 queryResult.getString("DATA")));
		}
		return cachedEvents;
	}
	
	private void executeCreationScript() throws SQLException {
		StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS EVENTS(");
		builder.append("ID IDENTITY PRIMARY KEY NOT NULL,");
		builder.append("EVENTID UUID NOT NULL,");
		builder.append("TIMESTAMP BIGINT NOT NULL,"); //milliseconds 
		builder.append("DATA VARCHAR NOT NULL)");
		this.connection.createStatement().executeUpdate(builder.toString());
	}
	
	private void executeThreadSafeUpdate(String sql, Object... params) throws SQLException {
		synchronized(this.connection) {
			if(connection.isClosed()) throw new IllegalStateException("The connection is closed."); //the connection could be closed while this thread was waiting
			
			PreparedStatement statement = this.connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				statement.setObject(i + 1, params[i]);
			}
			statement.executeUpdate();
		}
	}

	private ResultSet executeThreadSafeQuery(String sql) throws SQLException {
		synchronized(this.connection) {
			if(connection.isClosed()) throw new IllegalStateException("The connection is closed."); //the connection could be closed while this thread was waiting
			return this.connection.createStatement().executeQuery(sql);
		}
	}
}