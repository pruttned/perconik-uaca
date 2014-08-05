package com.gratex.perconik.useractivity.app.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gratex.perconik.useractivity.app.XmlGregorianCalendarHelper;

public class Logger {
  public static final int INFO_SEVERITY = 0;
  public static final int WARNING_SEVERITY = 1;
  public static final int ERROR_SEVERITY = 2;
  public static final int INFO_EVENT_COMMIT_SEVERITY = 3;
  
  private Connection connection;
  
  public Logger() throws SQLException{
    this.connection = LogManager.getInstance().openConnection();
  }
  
  public void closeSilent() {
    try {
      this.close();
    } catch (SQLException ex) {
      LogManager.getInstance().reportLoggingError(ex);
    }
  }
  
  public void close() throws SQLException {
    this.connection.close();
  }
  
  public LogReader getRecords() throws SQLException {
    return this.executeQuery("SELECT TIMESTAMP, SEVERITY, MESSAGE FROM RECORDS ORDER BY TIMESTAMP DESC");
  }
  
  public void clearRecords() throws SQLException {
    this.executeUpdate("DELETE FROM RECORDS");
  }
  
  public void writeErrorSilent(String message, Throwable exception) {
    this.writeSilent(Logger.getErrorMessage(message, exception), Logger.ERROR_SEVERITY);
  }
  
  public void writeErrorSilent(Throwable exception) {
    this.writeSilent(Logger.getErrorMessage(null, exception), Logger.ERROR_SEVERITY);
  }

  public void writeErrorSilent(String message) {
    this.writeSilent(Logger.getErrorMessage(message, null), Logger.ERROR_SEVERITY);
  }

  public void writeWarningSilent(String message) {
    this.writeSilent(message, Logger.WARNING_SEVERITY);
  }

  public void writeInfoSilent(String message) {
    this.writeSilent(message, Logger.INFO_SEVERITY);
  }
  
  public void writeSilent(String message, int severity) {
    this.executeUpdateSilent("INSERT INTO RECORDS (TIMESTAMP, SEVERITY, MESSAGE) VALUES (?, ?, ?)", XmlGregorianCalendarHelper.getMilliseconds(XmlGregorianCalendarHelper.createUtcNow()), severity, message);
  }
  
  public static void writeErrorSilentStatic(String message, Throwable exception) {
    Logger.writeSilentStatic(Logger.getErrorMessage(message, exception), Logger.ERROR_SEVERITY);
  }

  public static void writeErrorSilentStatic(Throwable exception) {
    Logger.writeSilentStatic(Logger.getErrorMessage(null, exception), Logger.ERROR_SEVERITY);
  }
  
  public static void writeErrorSilentStatic(String message) {
    Logger.writeSilentStatic(Logger.getErrorMessage(message, null), Logger.ERROR_SEVERITY);
  }
  
  public static void writeWarningSilentStatic(String message) {
    Logger.writeSilentStatic(message, Logger.WARNING_SEVERITY);
  }
  
  public static void writeInfoSilentStatic(String message) {
    Logger.writeSilentStatic(message, Logger.INFO_SEVERITY);
  }
  
  public static void writeSilentStatic(String message, int severity) {
    Logger logger = null;
    try {
      logger = new Logger();
      logger.writeSilent(message, severity);
    } catch (Throwable ex) {
      LogManager.getInstance().reportLoggingError(ex);
    } finally {
      if(logger != null) {
        logger.closeSilent();
      }
    }
  }
  
  public static String getExceptionFullText(Throwable exception) {
    if (exception == null) {
      return "";
    }

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    exception.printStackTrace(printWriter);
    String exceptionFullText = stringWriter.toString();

    printWriter.close();
    try {
      stringWriter.close();
    } catch (IOException e) {
      // 'close' call has no effect
    }

    return exceptionFullText;
  }
  
  static void executeUpdateSilent(Connection connection, String sql, Object ... params) {
    try {
      Logger.executeUpdate(connection, sql, params);
    } catch (SQLException ex) {
      LogManager.getInstance().reportLoggingError(ex);
    }
  }  
  
  static void executeUpdate(Connection connection, String sql, Object ... params) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(sql);

    try {
      for (int i = 0; i < params.length; i ++) {
        statement.setObject(i + 1, params[i]);
      }
      statement.executeUpdate();
    }
    finally {
      statement.close();
    }
  }
  
  static LogReader executeQuery(Connection connection, String sql) throws SQLException {
    Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //the result set can be read in both orders
    try {
      ResultSet resultSet = statement.executeQuery(sql);
      return new LogReader(statement, resultSet);
    } catch(SQLException ex) {
      statement.close();
      throw ex;
    }
  }
 
  private void executeUpdateSilent(String sql, Object ... params) {
    Logger.executeUpdateSilent(connection, sql, params);
  }
  
  private void executeUpdate(String sql, Object ... params) throws SQLException {
    Logger.executeUpdate(connection, sql, params);
  }
  
  private LogReader executeQuery(String sql) throws SQLException {
    return Logger.executeQuery(connection, sql);
  }
  
  private static String getErrorMessage(String message, Throwable exception) {
    if(message != null && exception != null) {
      return String.format("%s%n%nError:%s", message, Logger.getExceptionFullText(exception));
    }
    if(exception != null) {
      return Logger.getExceptionFullText(exception);
    }
    return message;
  }
}