package com.gratex.perconik.useractivity.app;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

  public Connection openConnection() throws SQLException {
    return this.connectionPool.getConnection();
  }

  public void closeConnectionOrTrace(Connection connection){
    ValidationHelper.checkArgNotNull(connection, "connection");

    try {
      connection.close();
    } catch (Exception ex) {
      AppTracer.getInstance().writeError("Failed to close a connection.", ex);
    }
  }

  /**
   * Adds the specified event into the cache. If an error occurs, the error is traced and false is returned - no exception is thrown.
   * Thread safe.
   */
  public boolean addEventOrTrace(EventDto event) {
    Connection connection = null;
    try {
      connection = this.openConnection();
      return this.addEventOrTrace(connection, event);

    } catch (SQLException ex) {
      AppTracer.getInstance().writeError("Failed to open a connection.", ex);
    } finally {
      this.closeConnectionOrTrace(connection);
    }

    return false;
  }

  /**
   * Adds the specified event into the cache. If an error occurs, the error is traced and false is returned - no exception is thrown.
   * Thread safe.
   */
  public boolean addEventOrTrace(Connection connection, EventDto event) {
    try {
      this.addEvent(connection, event);
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
    Connection connection = null;
    try {
      connection = this.openConnection();
      return this.addEventOrTrace(connection, eventId, timestamp, dataWithUtcTimestamp);

    } catch (SQLException ex) {
      AppTracer.getInstance().writeError("Failed to open a connection.", ex);
    } finally {
      this.closeConnectionOrTrace(connection);
    }

    return false;
  }

  /**
   * Adds the specified event into the cache. If an error occurs, the error is traced and false is returned - no exception is thrown.
   * Thread safe.
   */
  public boolean addEventOrTrace(Connection connection, String eventId, XMLGregorianCalendar timestamp, String dataWithUtcTimestamp) {
    try {
      this.addEvent(connection, eventId, timestamp, dataWithUtcTimestamp);
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
    Connection connection = this.openConnection();
    try {
      this.addEvent(connection, event);
    } finally {
      this.closeConnectionOrTrace(connection);
    }
  }

  /**
   * Adds the specified event into the cache.
   * Thread safe.
   * @throws SQLException
   * @throws JsonProcessingException
   */
  public void addEvent(Connection connection, EventDto event) throws SQLException, JsonProcessingException {
    ValidationHelper.checkArgNotNull(event, "event");

    event.setTimestamp(XmlGregorianCalendarHelper.toUtc(event.getTimestamp())); //ensure UTC
    this.addEvent(connection, event.getEventId(), event.getTimestamp(), this.eventSerializer.serialize(event));
  }

  /**
   * Adds the specified event into the cache.
   * Thread safe.
   * @throws SQLException
   * @throws JsonProcessingException
   */
  public void addEvent(String eventId, XMLGregorianCalendar timestamp, String dataWithUtcTimestamp) throws SQLException, JsonProcessingException {
    Connection connection = this.openConnection();
    try {
      this.addEvent(connection, eventId, timestamp, dataWithUtcTimestamp);
    } finally {
      this.closeConnectionOrTrace(connection);
    }
  }

  /**
   * Adds the specified event into the cache.
   * Thread safe.
   * @throws SQLException
   * @throws JsonProcessingException
   */
  public void addEvent(Connection connection, String eventId, XMLGregorianCalendar timestamp, String dataWithUtcTimestamp) throws SQLException, JsonProcessingException {
    ValidationHelper.checkArgNotNull(connection, "connection");
    ValidationHelper.checkStringArgNotNullOrWhitespace(eventId, "eventId");
    ValidationHelper.checkArgNotNull(timestamp, "timestamp");
    ValidationHelper.checkStringArgNotNullOrWhitespace(dataWithUtcTimestamp, "dataWithUtcTimestamp");

    this.executeUpdate(connection, "INSERT INTO EVENTS (EVENTID, TIMESTAMP, DATA) VALUES (?, ?, ?)", eventId, XmlGregorianCalendarHelper.getMilliseconds(timestamp), dataWithUtcTimestamp);
  }

  /**
   * Removes all events with specified 'IDs' from the cache.
   * Thread safe.
   * @throws SQLException
   */
  public void removeEvents(Connection connection, ArrayList<Integer> ids) throws SQLException {
    ValidationHelper.checkArgNotNull(ids, "ids");
    for (Integer id: ids) { //TODO: execute separate SQL with 'IN' clause
      this.removeEvent(connection, id);
    }
  }

  /**
   * Removes the event with the specified 'ID' from the cache.
   * Thread safe.
   * @throws SQLException
   */
  public void removeEvent(Connection connection, int id) throws SQLException {
    this.executeUpdate(connection, "DELETE FROM EVENTS WHERE ID=?", id);
  }

  /**
   * Removes all events from the cache.
   * Thread safe.
   * @throws SQLException
   */
  public void removeAllEvents(Connection connection) throws SQLException {
    this.executeUpdate(connection, "DELETE FROM EVENTS");
  }

  /**
   * Gets the event with the specified ID from the event cache. If it is not found, an exception is thrown.
   * Thread safe.
   * @throws SQLException
   */
  public CachedEvent getEvent(Connection connection, int id) throws SQLException {
    EventCacheReader reader = this.executeQuery(connection, String.format("SELECT TOP 1 ID, EVENTID, TIMESTAMP, DATA FROM EVENTS WHERE ID=%s", id));

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
  public EventCacheReader getEventsToCommit(Connection connection) throws SQLException {
    long lastEventTimeToCommit = new Date().getTime() - Settings.getInstance().getEventAgeToCommit();
    return this.executeQuery(connection, String.format("SELECT ID, EVENTID, TIMESTAMP, DATA FROM EVENTS WHERE TIMESTAMP <= %s ORDER BY TIMESTAMP ASC", lastEventTimeToCommit));
  }

  /**
   * Gets all events from the cache.
   * Thread safe.
   * @throws SQLException
   */
  public EventCacheReader getEvents(Connection connection) throws SQLException {
    return this.executeQuery(connection, "SELECT ID, EVENTID, TIMESTAMP, DATA FROM EVENTS ORDER BY TIMESTAMP DESC");
  }

  /**
   * Updates the user name in all events in the event cache to the current user name in the settings.
   * Thread safe.
   * @throws SQLException
   */
  public void updateUserNameInAllEvents(Connection connection) throws SQLException {
    String userNameRegexp = "\"user\":\"[^\"]*\"";
    String newUserName = String.format("\"user\":\"%s\"", Settings.getInstance().getUserName());

    this.executeUpdate(connection, String.format("UPDATE EVENTS SET DATA=REGEXP_REPLACE(DATA, '%s', '%s')", userNameRegexp, newUserName));
  }

  private void executeCreationScript() throws SQLException {
    StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS EVENTS(");
    builder.append("ID IDENTITY PRIMARY KEY NOT NULL,");
    builder.append("EVENTID VARCHAR NOT NULL,");
    builder.append("TIMESTAMP BIGINT NOT NULL,"); //milliseconds
    builder.append("DATA VARCHAR NOT NULL)");

    Connection connection = this.openConnection();
    try {
      this.executeUpdate(connection, builder.toString());
    }
    finally {
      this.closeConnectionOrTrace(connection);
    }
  }

  private void executeUpdate(Connection connection, String sql, Object ... params) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 0; i < params.length; i ++) {
        statement.setObject(i + 1, params[i]);
      }
      statement.executeUpdate();
    }
  }

  private EventCacheReader executeQuery(Connection connection, String sql) throws SQLException {
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);
    return new EventCacheReader(statement, resultSet);
  }
}
