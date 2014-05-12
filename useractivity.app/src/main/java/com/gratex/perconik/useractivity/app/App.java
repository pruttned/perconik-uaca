package com.gratex.perconik.useractivity.app;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.gratex.perconik.useractivity.app.dto.MonitoringStartedEventDto;
import com.gratex.perconik.useractivity.app.watchers.WatcherManager;

public class App {	
	private EventCache eventCache;
	private UserActivityServiceProxy userActivityServiceProxy;
	private EventCommitJob eventCommitJob;
	private WatcherManager watcherManager;
	private MainWindow mainWindow;
	private boolean isCollectingAndCommitting = false;
	
	public static void main(String[] args) {
		new App().onRun();
	}
	
	public void startCollectingAndCommitting() {
		if(!isCollectingAndCommitting) {
			addMonitoringStartedEvent();
			
			this.eventCommitJob.start();
			this.watcherManager.startWatchers();
		
			this.isCollectingAndCommitting = true;
		}
	}
	
	public void stopCollectingAndCommitting() {
		if(this.watcherManager != null) {
			this.watcherManager.stopWatchers();
		}
		
		if(this.eventCommitJob != null) {
			this.eventCommitJob.stop();
		}
		
		this.isCollectingAndCommitting = false;
	}
	
	public boolean isCollectingAndCommitting() {
		return this.isCollectingAndCommitting;
	}
	
	public EventCommitJob getEventCommitJob() {
		return this.eventCommitJob;
	}
	
	private void onRun() {
		hookToShutdown();
		initCore();
		initGui();
	}
	
	private void hookToShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				onExitAsync();
			}
		});
	}
	
	private void initCore() {
		try {
			this.eventCache = new EventCache();
			this.eventCache.initialize(new EventSerializer());
			
			this.userActivityServiceProxy = new UserActivityServiceProxy();
			this.eventCommitJob = new EventCommitJob(this.eventCache, this.userActivityServiceProxy);
			this.watcherManager = new WatcherManager(this.eventCache);
			
			startCollectingAndCommitting();
			
		} catch (Throwable ex) {
			exitOnInitError(null, ex);
		}
	}
	
	private void initGui() {
		if(SystemTray.isSupported()) {
			try {
				this.mainWindow = new MainWindow(this, this.eventCache);
				
				PopupMenu menu = new PopupMenu();
				TrayIcon trayIcon = new TrayIcon(ResourcesHelper.getUserActivityIcon16().getImage(), "User Activity - PerConIK", menu);
				trayIcon.addActionListener(new ActionListener() {					
					public void actionPerformed(ActionEvent e) {
						mainWindow.setVisible(true);
					}
				});
				SystemTray.getSystemTray().add(trayIcon);
				
				//'open' menu item				
				MenuItem openMenuItem = new MenuItem("Open");
				openMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						mainWindow.setVisible(true);
					}
				});
				menu.add(openMenuItem);
				
				//'exit' menu item
				MenuItem exitMenuItem = new MenuItem("Exit");
				exitMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						System.exit(0);
					}
				});
				menu.add(exitMenuItem);
			} catch (AWTException ex) {
				exitOnInitError("failed to insert the application into the system tray", ex);
			}
		} else {
			exitOnInitError("the system tray is not supported by this OS", null);
		}
	}
	
	private void exitOnInitError(String message, Throwable exception) {
		if(message == null) {
			message = "";
		}
		MessageBox.showError(null, String.format("Failed to run the UserActivity application%s%s.", message != "" ? " - " : "", message), exception, "UserActivity failed to run");
		System.exit(1);
	}
	
	private void addMonitoringStartedEvent() {
		eventCache.addEventOrTrace(new MonitoringStartedEventDto());
	}
	
	private void onExitAsync() {		
		try {
			stopCollectingAndCommitting();
			
			if (this.eventCache != null) {
				this.eventCache.close();
			}
		} catch (Throwable ex) {
			//nothing - just exit
		}
	}
}