package com.gratex.perconik.useractivity.app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gratex.perconik.useractivity.app.dto.CachedEvent;

/**
 * Represents a data reader for EventCache query result sets. Reads CachedEvents from a result set.
 */
public class EventCacheReader {
  private Statement statement;
  private ResultSet resultSet;
  private CachedEvent current;

  public EventCacheReader(Statement statement, ResultSet resultSet) {
    ValidationHelper.checkArgNotNull(statement, "statement");
    ValidationHelper.checkArgNotNull(resultSet, "resultSet");

    this.statement = statement;
    this.resultSet = resultSet;
  }

  /**
   * Reads the next CachedEvent from the result set.
   * Thread safe.
   * @throws SQLException
   */
  public boolean next() throws SQLException {
    if (this.resultSet.next()) {
      this.current = new CachedEvent(this.resultSet.getInt("ID"), this.resultSet.getString("EVENTID"), XmlGregorianCalendarHelper.createUtc(this.resultSet.getLong("TIMESTAMP")), this.resultSet.getString("DATA"));
      return true;
    }
    this.current = null;
    return false;
  }

  public CachedEvent getCurrent() {
    return this.current;
  }

  public void closeOrTrace() {
    try {
      this.close();
    } catch (SQLException ex) {
      AppTracer.getInstance().writeError("Failed to close an EventCacheReader.", ex);
    }
  }

  public void close() throws SQLException {
    this.statement.close(); //also closes resultSet
  }
}
