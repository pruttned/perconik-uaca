package com.gratex.perconik.useractivity.app.logging;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.gratex.perconik.useractivity.app.ValidationHelper;
import com.gratex.perconik.useractivity.app.XmlGregorianCalendarHelper;

/**
 * Represents a data reader for Log query result sets. Reads LogRecords from a result set.
 */
public class LogReader {
  private final static int PAGE_SIZE = 3;
  
  private Statement statement;
  private ResultSet resultSet;
  private ArrayList<LogRecord> currentPage;
  private boolean hasNextPage;
  private boolean hasPreviousPage;
  private boolean wasLastOperationNextPage = true; //false - the last performed operation was previousPage()
  
  public LogReader(Statement statement, ResultSet resultSet) throws SQLException {
    ValidationHelper.checkArgNotNull(statement, "statement");
    ValidationHelper.checkArgNotNull(resultSet, "resultSet");

    this.statement = statement;
    this.resultSet = resultSet;
    this.updateHasNextPreviousPage();
  }
  
  public ArrayList<LogRecord> getCurrentPage() {
    return currentPage;
  }
  
  public boolean hasNextPage() {
    return this.hasNextPage;
  }
  
  public boolean hasPreviousPage() {
    return this.hasPreviousPage;
  }
  
  public ArrayList<LogRecord> nextPage() throws SQLException, IllegalStateException {
    if(!this.hasNextPage()) {
      throw new IllegalStateException("The cursor is already at the last page.");
    }
    
    if(!wasLastOperationNextPage) {
      this.resultSet.relative(this.currentPage.size() - 1); //skipping the current page - moving the cursor before the page being read
    }
    
    ArrayList<LogRecord> page = new ArrayList<LogRecord>();
    for(int i = 0; i < LogReader.PAGE_SIZE && this.resultSet.next(); i++) {
      page.add(readLogRecord());
    }
    
    this.currentPage = page;
    this.wasLastOperationNextPage = true;
    this.updateHasNextPreviousPage();
    
    return this.currentPage;
  }

  public ArrayList<LogRecord> previousPage() throws SQLException, IllegalStateException {
    if(!this.hasPreviousPage()) {
      throw new IllegalStateException("The cursor is already at or before the first page.");
    }
    
    if(wasLastOperationNextPage) {
      int delta = -this.currentPage.size();
      if(!this.resultSet.isAfterLast()) { //if PAGE_SIZE was reached
        delta++;        
      }
      this.resultSet.relative(delta); //skipping the current page - moving the cursor after the page being read
    }
    
    ArrayList<LogRecord> page = new ArrayList<LogRecord>();
    for(int i = 0; i < LogReader.PAGE_SIZE && this.resultSet.previous(); i++) {
      page.add(readLogRecord());
    }
    
    this.currentPage = page;
    this.wasLastOperationNextPage = false;
    this.updateHasNextPreviousPage();
    
    return this.currentPage;
  }
  
  public void closeOrTrace() {
    try {
      this.close();
    } catch (SQLException ex) {
      Logger.writeErrorSilentStatic("Failed to close an LogReader.", ex);
    }
  }

  public void close() throws SQLException {
    this.statement.close(); //also closes resultSet
  }
  
  private LogRecord readLogRecord() throws SQLException {
    return new LogRecord(XmlGregorianCalendarHelper.createUtc(this.resultSet.getLong("TIMESTAMP")), this.resultSet.getInt("SEVERITY"), this.resultSet.getString("MESSAGE"));
  }
  
  private void updateHasNextPreviousPage() throws SQLException {
    this.hasNextPage = !this.resultSet.isLast() && !this.resultSet.isAfterLast();
    this.hasPreviousPage = !this.resultSet.isFirst() && !this.resultSet.isBeforeFirst(); //TODO: VRATI TRUE, AK SOM NA PRVEJ STRANE !!!
  }
}