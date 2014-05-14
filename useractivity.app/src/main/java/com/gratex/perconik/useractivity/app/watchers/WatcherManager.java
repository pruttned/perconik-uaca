package com.gratex.perconik.useractivity.app.watchers;

import java.util.ArrayList;

import com.gratex.perconik.useractivity.app.AppTracer;
import com.gratex.perconik.useractivity.app.EventCache;
import com.gratex.perconik.useractivity.app.Settings;
import com.gratex.perconik.useractivity.app.watchers.ide.IdeWatcherSvc;

/**
 * Manages all watcher instances.
 */
public class WatcherManager {
	private EventCache eventCache;
	private ArrayList<IWatcher> watchers;
	
	public WatcherManager(EventCache eventCache) {
		this.eventCache = eventCache;
		
		watchers = new ArrayList<IWatcher>();
		
		try {
			watchers.add(new ProcessWatcher());
			watchers.add(WebWatcher.getInstance());
			watchers.add(IdeWatcher.getInstance());
		} catch(Exception ex) {
			AppTracer.getInstance().writeError("Failed to create an instance of a watcher.", ex);
		}
				
		try {
			WatcherServer watcherServer = new WatcherServer(Settings.getInstance().getLocalSvcPort());  
			watcherServer.setServiceClasses(IdeWatcherSvc.class, WebWatcherSvc.class);
			watcherServer.start(); //TODO: stop at app shutdown
		} catch (Exception ex) {
			AppTracer.getInstance().writeError(String.format("Failed to start the '%s'.", WatcherServer.class.getName()), ex);
		}
	}
	
	public ArrayList<IWatcher> getWatchers() { 
		return this.watchers; 
	}
	
	public void startWatchers() {
		for (IWatcher watcher : this.watchers) {
			try {
				watcher.start(this.eventCache);
			} catch (Throwable ex) {
				AppTracer.getInstance().writeError(String.format("Failed to start the watcher '%s'.", watcher.getDisplayName()), ex);
			}
		}
	}
	
	public void stopWatchers() {
		for (IWatcher watcher : this.watchers) {
			try {
				watcher.stop();
			} catch (Throwable ex) {
				AppTracer.getInstance().writeError(String.format("Failed to stop the watcher '%s'.", watcher.getDisplayName()), ex);
			}
		}
	}
}