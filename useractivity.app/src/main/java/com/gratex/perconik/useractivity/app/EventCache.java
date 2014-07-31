package com.gratex.perconik.useractivity.app;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.h2.jdbcx.JdbcConnectionPool;

import com.gratex.perconik.useractivity.app.dto.CachedEvent;
import com.gratex.perconik.useractivity.app.dto.EventDto;

/**
 * Represents the local cache of events - local DB.
 */
public class EventCache {
  private JdbcConnectionPool connectionPool;
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
    this.connectionPool = JdbcConnectionPool.create(dbUri, "", "");

    this.executeCreationScript();
  }

  /**
   * Closes all connections.
   * Thread safe.
   */
  public void close() {
    if (this.connectionPool != null) {
      this.connectionPool.dispose();
    }
  }

  /**
   * Adds the specified event into the cache. If an error occurs, the error is traced and false is returned - no exception is thrown.
   * Thread safe.
   */
  public boolean addEventOrTrace(EventDto event) {
    try {
      this.addEvent(event);
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
      this.addEvent(eventId, timestamp, dataWithUtcTimestamp);
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
    this.addEvent(event.getEventId(), event.getTimestamp(), this.eventSerializer.serialize(event));
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

    this.executeUpdate("INSERT INTO EVENTS (EVENTID, TIMESTAMP, DATA) VALUES (?, ?, ?)", eventId, XMLGregorianCalendarHelper.getMilliseconds(timestamp), dataWithUtcTimestamp);
  }

  /**
   * Removes all events with specified 'IDs' from the cache.
   * Thread safe.
   * @throws SQLException
   */
  public void removeEvents(ArrayList<Integer> ids) throws SQLException {
    ValidationHelper.checkArgNotNull(ids, "ids");
    for (Integer id: ids) { //TODO: execute separate SQL with 'IN' clause
      this.removeEvent(id);
    }
  }

  /**
   * Removes the event with the specified 'ID' from the cache.
   * Thread safe.
   * @throws SQLException
   */
  public void removeEvent(int id) throws SQLException {
    this.executeUpdate("DELETE FROM EVENTS WHERE ID=?", id);
  }

  /**
   * Removes all events from the cache.
   * Thread safe.
   * @throws SQLException
   */
  public void removeAllEvents() throws SQLException {
    this.executeUpdate("DELETE FROM EVENTS");
  }

  /**
   * Gets the event with the specified ID from the event cache. If it is not found, an exception is thrown.
   * Thread safe.
   * @throws SQLException
   */
  public CachedEvent getEvent(int id) throws SQLException {
    EventCacheReader reader = this.executeQuery(String.format("SELECT TOP 1 ID, EVENTID, TIMESTAMP, DATA FROM EVENTS WHERE ID=%s", id));

    try {
      if (reader.next()) {
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
    return this.executeQuery(String.format("SELECT ID, EVENTID, TIMESTAMP, DATA FROM EVENTS WHERE TIMESTAMP <= %s ORDER BY TIMESTAMP ASC", lastEventTimeToCommit));
  }

  /**
   * Gets all events from the cache.
   * Thread safe.
   * @throws SQLException
   */
  public EventCacheReader getEvents() throws SQLException {
    return this.executeQuery("SELECT ID, EVENTID, TIMESTAMP, DATA FROM EVENTS ORDER BY TIMESTAMP DESC");
  }

  /**
   * Updates the user name in all events in the event cache to the current user name in the settings.
   * Thread safe.
   * @throws SQLException
   */
  public void updateUserNameInAllEvents() throws SQLException {
    String userNameRegexp = "\"user\":\"[^\"]*\"";
    String newUserName = String.format("\"user\":\"%s\"", Settings.getInstance().getUserName());

    this.executeUpdate(String.format("UPDATE EVENTS SET DATA=REGEXP_REPLACE(DATA, '%s', '%s')", userNameRegexp, newUserName));
  }

  private void executeCreationScript() throws SQLException {
    StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS EVENTS(");
    builder.append("ID IDENTITY PRIMARY KEY NOT NULL,");
    builder.append("EVENTID VARCHAR NOT NULL,");
    builder.append("TIMESTAMP BIGINT NOT NULL,"); //milliseconds
    builder.append("DATA VARCHAR NOT NULL)");

    this.executeUpdate(builder.toString());
  }

  private void executeUpdate(String sql, Object ... params) throws SQLException {
    Connection connection = this.connectionPool.getConnection();

    PreparedStatement statement = connection.prepareStatement(sql);
    for (int i = 0; i < params.length; i ++) {
      statement.setObject(i + 1, params[i]);
    }
    statement.executeUpdate();

    connection.close();
  }

  private EventCacheReader executeQuery(String sql) throws SQLException {
    Connection connection = this.connectionPool.getConnection();
    ResultSet resultSet = connection.createStatement().executeQuery(sql);
    return new EventCacheReader(connection, resultSet);
  }
}
