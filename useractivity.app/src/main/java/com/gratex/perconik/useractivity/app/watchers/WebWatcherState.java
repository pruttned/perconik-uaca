package com.gratex.perconik.useractivity.app.watchers;

import com.gratex.perconik.useractivity.app.ValidationHelper;

public final class WebWatcherState {
  private static class LazyHolder {
    static final WebWatcherState instance = new WebWatcherState();
  }

  private String lastCopyText;
  private String lastCopyUrl;

  WebWatcherState() {}

  public static WebWatcherState getInstance() {
    return LazyHolder.instance;
  }

  public void setCopiedText(String url, String text) {
    if (!ValidationHelper.isStringNullOrWhitespace(text)) {
      this.lastCopyUrl = url;
      this.lastCopyText = text.trim(); //TODO: remove all whitespaces
    } else {
      this.lastCopyText = null;
      this.lastCopyUrl = null;
    }
  }

  public String getLastCopyText() {
    return this.lastCopyText;
  }

  public String getLastCopyUrl() {
    return this.lastCopyUrl;
  }
}
