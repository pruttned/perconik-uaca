package com.gratex.perconik.useractivity.app;

public class SvcException extends Exception {
  private static final long serialVersionUID = 0L;

  public SvcException(String message) {
    super(message);
  }

  public SvcException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
