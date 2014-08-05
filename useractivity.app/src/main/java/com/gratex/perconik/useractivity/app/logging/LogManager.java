package com.gratex.perconik.useractivity.app.logging;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import com.gratex.perconik.useractivity.app.Settings;
import com.gratex.perconik.useractivity.app.ValidationHelper;
import com.gratex.perconik.useractivity.app.XmlGregorianCalendarHelper;

import org.h2.jdbcx.JdbcConnectionPool;

public class LogManager {
  private static class LazyHolder {
    private static final LogManager INSTANCE = new LogManager();
  }
  
  private static final int CLEANUP_TIMER_INTERVAL = 3 * 60 * 60 * 1000; //3 hours - cleanup is performed at this interval
  private static final int CLEANUP_TIME_WINDOW = 3 * 24 * 60 * 60 * 1000; //3 days - records older than this are subject to cleanup 
  private JdbcConnectionPool connectionPool;
  private Timer cleanupTimer;
  
  private LogManager() { }
  
  public static LogManager getInstance() {
    return LazyHolder.INSTANCE;
  }
  
  public void open() throws ClassNotFoundException, SQLException {
    try {
      Class.forName("org.h2.Driver");
      String dbUri = "jdbc:h2:" + Paths.get(Settings.getInstance().getUserFolder(), "Log");
      this.connectionPool = JdbcConnectionPool.create(dbUri, "", "");

      this.executeCreationScript();
      
      this.cleanupTimer = new Timer(true);
      this.startCleanupTimer();
    } catch (SQLException ex) {
      this.close();
      throw ex;
    }
  }
  
  public void close() {
    this.stopCleanupTimer();
    
    if (this.connectionPool != null) {
      this.connectionPool.dispose();
      this.connectionPool = null;
    }
  }
  
  public Connection openConnection() throws SQLException {
    return this.connectionPool.getConnection();
  }

  public void closeConnectionSilent(Connection connection) {
    try {
      closeConnection(connection);
    } catch (SQLException ex) {
      this.reportLoggingError(ex);
    }
  }
  
  public void closeConnection(Connection connection) throws SQLException {
    ValidationHelper.checkArgNotNull(connection, "connection");
    connection.close();
  }
  
  public void reportLoggingError(Throwable ex) {
    //TODO: store these exceptions (or just the last exception) in a list - this can be shown in the log dialog
  }
  
  private void executeCreationScript() throws SQLException {
    StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS RECORDS(");
    builder.append("ID IDENTITY PRIMARY KEY NOT NULL,");
    builder.append("TIMESTAMP BIGINT NOT NULL,"); //milliseconds
    builder.append("SEVERITY INT NOT NULL,");
    builder.append("MESSAGE VARCHAR NOT NULL)");

    Connection connection = this.openConnection();
    try {
      Logger.executeUpdate(connection, builder.toString());
    }
    finally {
      this.closeConnection(connection);
    }
  }
  
  private void startCleanupTimer() {
    this.cleanupTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        LogManager.this.cleanup();
      }
    }, LogManager.CLEANUP_TIMER_INTERVAL);
  }
  
  private void stopCleanupTimer() {
    if(this.cleanupTimer != null) {
      this.cleanupTimer.cancel();
    }
  }
  
  private void cleanup() {
    Connection connection = null;
    try {
      connection = this.openConnection();
      Logger.executeUpdate(connection, "DELETE FROM RECORDS WHERE TIMESTAMP < ?", XmlGregorianCalendarHelper.getMilliseconds(XmlGregorianCalendarHelper.createUtcNow()) - LogManager.CLEANUP_TIME_WINDOW);
    } catch (SQLException ex) {
      Logger.writeErrorSilentStatic("Log cleanup failed.", ex);
    } finally {
      this.closeConnectionSilent(connection);
      this.startCleanupTimer();
    }
  }
}
