package com.gratex.perconik.useractivity.app.watchers;

import com.gratex.perconik.useractivity.app.EventCache;

public interface Watcher {
  /**
   * Gets the name of this watcher that is to be shown to the user.
   */
  public String getDisplayName();

  /**
   * The watcher starts to collect events.
   */
  public void start(EventCache eventCache);

  /**
   * The watcher stops and releases all resources.
   * This method is thread safe.
   */
  public void stop();
}
