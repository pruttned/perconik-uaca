package com.gratex.perconik.useractivity.app;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.gratex.perconik.useractivity.app.dto.MonitoringStartedEventDto;
import com.gratex.perconik.useractivity.app.watchers.WatcherManager;

public class App {
  private static final App INSTANCE = new App();
  private EventCache eventCache;
  private UserActivityServiceProxy userActivityServiceProxy;
  private EventCommitJob eventCommitJob;
  private WatcherManager watcherManager;
  private MainWindow mainWindow;
  private boolean isCollectingAndCommitting = false;
  private CheckboxMenuItem collectingAndCommittingMenuItem;

  private App() {}

  public static void main(String[] args) {
    getInstance().onRun();
  }

  public static App getInstance() {
    return INSTANCE;
  }

  public void toggleCollectingAndCommitting() {
    if (this.isCollectingAndCommitting()) {
      this.stopCollectingAndCommitting();
    } else {
      this.startCollectingAndCommitting();
    }
  }

  public void startCollectingAndCommitting() {
    if (!this.isCollectingAndCommitting) {
      this.addMonitoringStartedEvent();

      this.eventCommitJob.start();
      this.watcherManager.startWatchers();

      this.isCollectingAndCommitting = true;

      this.updateIsCollectingAndCommittingControls();
    }
  }

  public void stopCollectingAndCommitting() {
    if (this.watcherManager != null) {
      this.watcherManager.stopWatchers();
    }

    if (this.eventCommitJob != null) {
      this.eventCommitJob.stop();
    }

    this.isCollectingAndCommitting = false;

    this.updateIsCollectingAndCommittingControls();
  }

  public boolean isCollectingAndCommitting() {
    return this.isCollectingAndCommitting;
  }

  public boolean isCollecting() {
    return this.isCollectingAndCommitting;
  }

  public EventCommitJob getEventCommitJob() {
    return this.eventCommitJob;
  }

  public UserActivityServiceProxy getUserActivityServiceProxy() {
    return this.userActivityServiceProxy;
  }

  public EventCache getEventCache() {
    return this.eventCache;
  }

  public void showMainWindow() {
    this.mainWindow.setVisible(true);
  }

  private void onRun() {
    this.hookToShutdown();
    this.initCore();
    this.initGui();
  }

  private void hookToShutdown() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        App.this.onExitAsync();
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

      this.startCollectingAndCommitting();

    } catch (Throwable ex) {
      this.exitOnInitError(null, ex);
    }
  }

  private void initGui() {
    if (SystemTray.isSupported()) {
      try {
        this.mainWindow = new MainWindow(this, this.eventCache);

        PopupMenu menu = new PopupMenu();
        TrayIcon trayIcon = new TrayIcon(ResourcesHelper.getUserActivityIcon16().getImage(), "User Activity - PerConIK", menu);
        trayIcon.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            App.this.showMainWindow();
          }
        });
        SystemTray.getSystemTray().add(trayIcon);

        //'open' menu item
        MenuItem openMenuItem = new MenuItem("Open");
        openMenuItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            App.this.showMainWindow();
          }
        });
        menu.add(openMenuItem);

        //'enable/disable' menu item
        this.collectingAndCommittingMenuItem = new CheckboxMenuItem("Enabled");
        this.updateIsCollectingAndCommittingControls();
        this.collectingAndCommittingMenuItem.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent arg0) {
            App.this.toggleCollectingAndCommitting();
          }
        });
        menu.add(this.collectingAndCommittingMenuItem);

        //'exit' menu item
        MenuItem exitMenuItem = new MenuItem("Exit (Shut User Activity Down)");
        exitMenuItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            System.exit(0);
          }
        });
        menu.add(exitMenuItem);
      } catch (AWTException ex) {
        this.exitOnInitError("failed to insert the application into the system tray", ex);
      }
    } else {
      this.exitOnInitError("the system tray is not supported by this OS", null);
    }
  }

  private void exitOnInitError(String message, Throwable exception) {
    if (message == null) {
      message = "";
    }
    MessageBox.showError(null, String.format("Failed to run the UserActivity application%s%s.", message != "" ? " - " : "", message), exception, "UserActivity failed to run");
    System.exit(1);
  }

  private void addMonitoringStartedEvent() {
    this.eventCache.addEventOrTrace(new MonitoringStartedEventDto());
  }

  private void updateIsCollectingAndCommittingControls() {
    if (this.collectingAndCommittingMenuItem != null) {
      this.collectingAndCommittingMenuItem.setState(this.isCollectingAndCommitting);
    }

    if (this.mainWindow != null) {
      this.mainWindow.updateIsCollectingAndCommittingControls();
    }
  }

  void onExitAsync() {
    try {
      this.stopCollectingAndCommitting();

      if (this.watcherManager != null) {
        this.watcherManager.close();
      }

      if (this.eventCache != null) {
        this.eventCache.close();
      }

    } catch (Throwable ex) {
      //nothing - just exit
    }
  }
}
