package com.gratex.perconik.useractivity.app;

import java.sql.Connection;
import java.util.Timer;
import java.util.TimerTask;

import com.gratex.perconik.useractivity.app.dto.CachedEvent;

/**
 * Represents a timer job that commits events stored in EventCache to the server in time intervals.
 */
public class EventCommitJob {
  private EventCommitJobState state = EventCommitJobState.STOPPED;
  private Object stateSyncObj = new Object();
  private boolean isCommitAborted = false;
  private Timer timer;
  private EventCache eventCache;
  private UserActivityServiceProxy userActivityServiceProxy;

  public EventCommitJob(EventCache eventCache, UserActivityServiceProxy userActivityServiceProxy) {
    ValidationHelper.checkArgNotNull(eventCache, "eventCache");
    ValidationHelper.checkArgNotNull(userActivityServiceProxy, "userActivityServiceProxy");

    this.eventCache = eventCache;
    this.userActivityServiceProxy = userActivityServiceProxy;
  }

  public EventCommitJobState getState() {
    synchronized (this.stateSyncObj) {
      return this.state;
    }
  }

  public void start() {
    synchronized (this.stateSyncObj) {
      if (this.getState() == EventCommitJobState.STOPPED) {
        this.setState(EventCommitJobState.WAITING);
        this.isCommitAborted = false;
        this.startTimer();
      }
    }
  }

  public void stop() {
    this.isCommitAborted = true; //notifies the committing thread that it is to stop committing as soon as possible
    synchronized (this.stateSyncObj) {
      this.setState(EventCommitJobState.STOPPED);
      this.stopTimer();
    }
  }

  public void restartIfRunning() {
    synchronized (this.stateSyncObj) {
      if (this.getState() != EventCommitJobState.STOPPED) {
        this.stop();
        this.start();
      }
    }
  }

  /**
   * Forces commit - commits events now. This is completely independent from this job`s timer or its work.
   * Thread safe.
   * @param commitAllEvents true - commit all events in EventCache; false - commit only events that are old enough to be committed
   */
  public void commitEventsNow(boolean commitAllEvents) {
    EventCacheReader eventsToCommitReader = null;
    Connection connection = null;
    try {
      connection = this.eventCache.openConnection();
      eventsToCommitReader = commitAllEvents ? this.eventCache.getEvents(connection) : this.eventCache.getEventsToCommit(connection);

      while (eventsToCommitReader.next()) {
        CachedEvent cachedEvent = eventsToCommitReader.getCurrent();
        try {
          EventDocument doc = new EventDocument(cachedEvent.getData());
          doc.setWasCommitForcedByUser(true);
          cachedEvent.setData(doc.toJsonString());

          this.userActivityServiceProxy.commitEvent(cachedEvent);
          this.eventCache.removeEvent(connection, cachedEvent.getId()); //'commitEvent' is an idempotent operation so it is safe to fail during remove
        } catch (Exception ex) {
          AppTracer.getInstance().writeError(String.format("There was an error while committing the event with ID '%s'.", cachedEvent.getEventId()), ex);
        }
      }
    } catch (Exception ex) {
      AppTracer.getInstance().writeError("There was an error while committing events.", ex);
    } finally {
      if (eventsToCommitReader != null) {
        eventsToCommitReader.closeOrTrace();
      }
      if(connection != null ){
        this.eventCache.closeConnectionOrTrace(connection);
      }
    }
  }

  private void setState(EventCommitJobState state) {
    this.state = state;
    AppTracer.getInstance().writeInfo("EventCommitJob state changed to '" + state + "'.");
  }

  private void startTimer() {
    if (this.timer == null) {
      this.timer = new Timer(true);
    }

    this.timer.schedule(new TimerTask() {
      @Override
      public void run() {
        EventCommitJob.this.commitEvents();
      }
    }, Settings.getInstance().getEventCommitInterval());
  }

  private void stopTimer() {
    if (this.timer != null) {
      this.timer.cancel();
      this.timer = null; //after canceling, the timer cannot be used again - it will be recreated in 'startTimer()'
    }
  }

  private void commitEvents() {
    synchronized (this.stateSyncObj) {
      EventCacheReader eventsToCommitReader = null;
      Connection connection = null;
      try {
        if (this.getState() != EventCommitJobState.STOPPED) {
          this.setState(EventCommitJobState.COMMITTING);
          connection = this.eventCache.openConnection();

          eventsToCommitReader = this.eventCache.getEventsToCommit(connection);
          while (eventsToCommitReader.next()) {
            if (this.isCommitAborted) {
              break;
            }

            CachedEvent cachedEvent = eventsToCommitReader.getCurrent();
            try {
              this.userActivityServiceProxy.commitEvent(cachedEvent);
              this.eventCache.removeEvent(connection, cachedEvent.getId()); //'commitEvent' is an idempotent operation so it is safe to fail during remove
            } catch (Exception ex) {
              AppTracer.getInstance().writeError(String.format("There was an error while committing the event with ID '%s'.", cachedEvent.getEventId()), ex);
            }
          }
        }
      } catch (Exception ex) {
        AppTracer.getInstance().writeError("There was an error while committing events.", ex);
      } finally {
        if (eventsToCommitReader != null) {
          eventsToCommitReader.closeOrTrace();
        }

        if (!this.isCommitAborted) {
          this.setState(EventCommitJobState.WAITING);
          this.startTimer();
        }

        if(connection != null ){
          this.eventCache.closeConnectionOrTrace(connection);
        }

      }
    }
  }
}
