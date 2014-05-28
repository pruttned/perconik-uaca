package com.gratex.perconik.useractivity.app;

import java.util.ArrayList;
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
			if(getState() == EventCommitJobState.STOPPED) {				
				setState(EventCommitJobState.WAITING);
				this.isCommitAborted = false;
				startTimer();
			}
		}
	}
	
	public void stop() {
		this.isCommitAborted = true; //notifies the committing thread that it is to stop committing as soon as possible
		synchronized (this.stateSyncObj) {
			setState(EventCommitJobState.STOPPED);
			stopTimer();
		}
	}
	
	public void restartIfRunning() {
		synchronized (this.stateSyncObj) {
			if(getState() != EventCommitJobState.STOPPED) {
				stop();
				start();
			}
		}
	}
	
	/**
	 * Forces commit - commits events now. This is completely independent from this job`s timer or its work.
	 * Thread safe. 
	 * @param commitAllEvents true - commit all events in EventCache; false - commit only events that are old enough to be committed
	 */
	public void commitEventsNow(boolean commitAllEvents) {
		try {
			ArrayList<CachedEvent> eventsToCommit = commitAllEvents ? eventCache.getEvents() : eventCache.getEventsToCommit();
			
			for (CachedEvent cachedEvent : eventsToCommit) {
				try {
					SerializedEventWriter writer = new SerializedEventWriter(cachedEvent.getData());
					writer.setWasCommitForcedByUser(true);
					cachedEvent.setData(writer.getData());
					
					userActivityServiceProxy.commitEvent(cachedEvent);
					eventCache.removeEvent(cachedEvent.getEventId()); //'commitEvent' is an idempotent operation so it is safe to fail during remove
				}
				catch (Exception ex) {
					AppTracer.getInstance().writeError(String.format("There was an error while committing the event with ID '%s'.", cachedEvent.getEventId()), ex);
				}
			}
		}
		catch (Exception ex) {
			AppTracer.getInstance().writeError("There was an error while committing events.", ex);
		}
	}
	
	private void setState(EventCommitJobState state) {
		this.state = state;
		AppTracer.getInstance().writeInfo("EventCommitJob state changed to '" + state + "'.");
	}
	
	private void startTimer() {
		if(timer == null) {
			timer = new Timer(true);
		}
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				commitEvents();
			}
		}, Settings.getInstance().getEventCommitInterval());
	}
	
	private void stopTimer() {		
		if(this.timer != null) {
			this.timer.cancel();
			this.timer = null; //after canceling, the timer cannot be used again - it will be recreated in 'startTimer()'
		}
	}
	
	private void commitEvents() {
		synchronized (this.stateSyncObj) {
			try {
				if(getState() != EventCommitJobState.STOPPED) {
					setState(EventCommitJobState.COMMITTING);
					ArrayList<CachedEvent> eventsToCommit = this.eventCache.getEventsToCommit();
					
					for (CachedEvent cachedEvent : eventsToCommit) {
						if(this.isCommitAborted) {
							break;
						}
						
						try {
							this.userActivityServiceProxy.commitEvent(cachedEvent);
							this.eventCache.removeEvent(cachedEvent.getEventId()); //'commitEvent' is an idempotent operation so it is safe to fail during remove
						}
						catch (Exception ex) {
							AppTracer.getInstance().writeError(String.format("There was an error while committing the event with ID '%s'.", cachedEvent.getEventId()), ex);
						}
					}
				}
			}
			catch (Exception ex) {
				AppTracer.getInstance().writeError("There was an error while committing events.", ex);
			}
			finally {
				if(!this.isCommitAborted) {
					setState(EventCommitJobState.WAITING);
					startTimer();
				}				
			}
		}
	}
}