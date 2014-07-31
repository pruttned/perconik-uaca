package com.gratex.perconik.useractivity.app.watchers;

import com.gratex.perconik.useractivity.app.ValidationHelper;

public class WebWatcherState {
  // http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
  private static class LazyHolder {
    private static final WebWatcherState INSTANCE = new WebWatcherState();
  }

  private String lastCopyText;
  private String lastCopyUrl;

  private WebWatcherState() {}

  public static WebWatcherState getInstance() {
    return LazyHolder.INSTANCE;
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
