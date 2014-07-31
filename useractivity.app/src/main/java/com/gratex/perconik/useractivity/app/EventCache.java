package com.gratex.perconik.useractivity.app;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gratex.perconik.useractivity.app.dto.CachedEvent;
import com.gratex.perconik.useractivity.app.dto.EventDto;


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
		
		convertEventIdToVarchar(); //TODO: remove in version 2.0.8
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
	 * Adds the specified event into the cache. If an error occurs, the error is traced and false is returned - no exception is thrown.
	 * Thread safe.
	 */
	public boolean addEventOrTrace(String eventId, XMLGregorianCalendar timestamp, String dataWithUtcTimestamp) {
		try {
			addEvent(eventId, timestamp, dataWithUtcTimestamp);
			return true;
		} catch (Exception ex) {
			AppTracer.getInstance().writeError(String.format("Failed to add the event with ID '%s' to the event cache (local DB).", eventId), ex);
			return false;
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
		
		event.setTimestamp(XMLGregorianCalendarHelper.toUtc(event.getTimestamp())); //ensure UTC
		addEvent(event.getEventId(), event.getTimestamp(), this.eventSerializer.serialize(event));
	}
	
	/**
	 * Adds the specified event into the cache.
	 * Thread safe.
	 * @throws SQLException 
	 * @throws JsonProcessingException 
	 */
	public void addEvent(String eventId, XMLGregorianCalendar timestamp, String dataWithUtcTimestamp) throws SQLException, JsonProcessingException {
		ValidationHelper.checkStringArgNotNullOrWhitespace(eventId, "eventId");
		ValidationHelper.checkArgNotNull(timestamp, "timestamp");
		ValidationHelper.checkStringArgNotNullOrWhitespace(dataWithUtcTimestamp, "dataWithUtcTimestamp");
		
		executeThreadSafeUpdate("INSERT INTO EVENTS (EVENTID, TIMESTAMP, DATA) VALUES (?, ?, ?)", eventId, 
																							 	  XMLGregorianCalendarHelper.getMilliseconds(timestamp), 
																							 	  dataWithUtcTimestamp);
	}
	
	/**
	 * Removes all events with specified 'IDs' from the cache.
	 * Thread safe.
	 * @throws SQLException
	 */
	public void removeEvents(ArrayList<Integer> ids) throws SQLException {
		ValidationHelper.checkArgNotNull(ids, "ids");
		for (Integer id : ids) {
			removeEvent(id);
		}
	}
	
	/**
	 * Removes the event with the specified 'ID' from the cache.
	 * Thread safe.
	 * @throws SQLException 
	 */
	public void removeEvent(int id) throws SQLException {
		executeThreadSafeUpdate("DELETE FROM EVENTS WHERE ID=?", id);
	}
	
	/**
	 * Removes all events from the cache.
	 * Thread safe.
	 * @throws SQLException
	 */
	public void removeAllEvents() throws SQLException {
		executeThreadSafeUpdate("DELETE FROM EVENTS");
	}
	
	/**
	 * Gets the event with the specified ID from the event cache. If it is not found, an exception is thrown.
	 * Thread safe.
	 * @throws SQLException
	 */
	public CachedEvent getEvent(int id) throws SQLException {
		ResultSet resultSet = executeThreadSafeQuery(String.format("SELECT TOP 1 ID, EVENTID, TIMESTAMP, DATA FROM EVENTS WHERE ID=%s", id));
		EventCacheReader reader = new EventCacheReader(connection, resultSet);
		
		try {
			if(reader.next()) {
				return reader.getCurrent();
			}
		} finally {
			reader.close();
		}
		
		throw new SQLException(String.format("The event with ID '%s' was not found in the event cache.", id));
	}
	
	/**
	 * Gets events from the cache that are old enough to be committed to the server.
	 * Thread safe.
	 * @throws SQLException 
	 */
	public EventCacheReader getEventsToCommit() throws SQLException {
		long lastEventTimeToCommit = new Date().getTime() - Settings.getInstance().getEventAgeToCommit();
		ResultSet resultSet = executeThreadSafeQuery(String.format("SELECT ID, EVENTID, TIMESTAMP, DATA FROM EVENTS WHERE TIMESTAMP <= %s ORDER BY TIMESTAMP ASC", lastEventTimeToCommit));
		return new EventCacheReader(connection, resultSet);
	}

	/**
	 * Gets all events from the cache.
	 * Thread safe.
	 * @throws SQLException 
	 */
	public EventCacheReader getEvents() throws SQLException {
		ResultSet resultSet = executeThreadSafeQuery("SELECT ID, EVENTID, TIMESTAMP, DATA FROM EVENTS ORDER BY TIMESTAMP DESC");
		return new EventCacheReader(connection, resultSet);
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
	
	private void executeCreationScript() throws SQLException {
		StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS EVENTS(");
		builder.append("ID IDENTITY PRIMARY KEY NOT NULL,");
		builder.append("EVENTID VARCHAR NOT NULL,");
		builder.append("TIMESTAMP BIGINT NOT NULL,"); //milliseconds 
		builder.append("DATA VARCHAR NOT NULL)");
		this.connection.createStatement().executeUpdate(builder.toString());
	}
	
	private void convertEventIdToVarchar() throws SQLException {
		String sql = "ALTER TABLE EVENTS ALTER COLUMN EVENTID TYPE VARCHAR";		
		this.connection.createStatement().executeUpdate(sql);
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