package com.gratex.perconik.useractivity.app.watchers;

import java.util.Timer;
import java.util.TimerTask;

import com.gratex.perconik.useractivity.app.EventCache;

/**
 * Base class for watchers that execute their work in time intervals.
 */
public abstract class TimerWatcherBase implements IWatcher {
	private EventCache eventCache;
	private Timer timer;
	private boolean isStopped = true;
	
	public abstract String getDisplayName();

	/**
	 * NOT thread safe.
	 */
	public void start(EventCache eventCache) {
		if(isStopped) {
			this.eventCache = eventCache;
			isStopped = false;
			
			if(ticksAtStart()) {
				onTick();
			}
			startTimer();
		}
	}

	/**
	 * Thread safe.
	 */
	public void stop() {
		isStopped = true;
		timer.cancel();
		timer = null; //after canceling, the timer cannot be used again - it will be recreated in 'startTimer()'
	}
	
	protected EventCache getEventCache() {
		return eventCache;
	}
	
	protected long getTimerInterval() {
		return 1000 * 60 * 5;
	}
	
	/**
	 * Determines whether to wait the time interval to perform the first tick or whether to perform it immediately after 'start()' is called.  
	 */
	protected boolean ticksAtStart() {
		return true;
	}
	
	protected abstract void onTick();
	
	private void startTimer() {
		if(timer == null) {
			timer = new Timer(true);
		}
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				onTick();
				if(!isStopped) {
					startTimer();
				}
			}
			
		}, getTimerInterval());
	}
}