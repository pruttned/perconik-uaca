package com.gratex.perconik.useractivity.app;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.gratex.perconik.useractivity.app.dto.CachedEvent;

public final class EventCacheDialog extends JDialog {

  private static final long serialVersionUID = 3565081061317049889L;
  private static final int PAGE_SIZE = 3;
  
  final EventCache eventCache;
  final EventCommitJob eventCommitJob;
  JTable eventsTable;
  ArrayList<CachedEventViewModel> displayedEvents; //events currently displayed to the user
  int pageIndex;
  int lastPageIndex;
  private JLabel pageLabel;
  private JButton prevPageButton;
  private JButton nextPageButton;

  public EventCacheDialog(JFrame parent, EventCache eventCache, EventCommitJob eventCommitJob) {
    super(parent, true);

    this.eventCache = eventCache;
    this.eventCommitJob = eventCommitJob;

    this.setTitle("Event Cache");
    this.setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
    this.setSize(900, 500);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.addControls();
    this.setLocationRelativeTo(null);

    this.refresh();
  }

  private final static class CachedEventViewModel {
    private final EventCache eventCache;

    final int id;
    final String eventId;
    final String timestamp;

    String eventTypeShortUri;
    String formattedData;

    public CachedEventViewModel(CachedEvent cachedEvent, EventCache eventCache) {
      this.eventCache = eventCache;
      this.id = cachedEvent.getId();
      this.eventId = cachedEvent.getEventId();
      this.timestamp = XmlGregorianCalendarHelper.toLocalString(cachedEvent.getTimestamp());

      try {
        EventDocument doc = new EventDocument(cachedEvent.getData());
        this.eventTypeShortUri = TypeUriHelper.getEventTypeShortUri(doc.getEventTypeUri());
        //formattedData = doc.toFormatedJsonString();
      } catch (IOException ex) {
        AppTracer.getInstance().writeError(String.format("Failed to deserialize the event with ID '%s'.", cachedEvent.getEventId()), ex);
        this.eventTypeShortUri = "<ERROR - see log for details>";
        //formattedData = "<ERROR - see log for details>";
      }
    }

    public String getFormattedData() throws JsonProcessingException, IOException, SQLException {
      if (this.formattedData == null) {
        Connection connection = this.eventCache.openConnection();

        try {
          this.formattedData = new EventDocument(this.eventCache.getEvent(connection, this.id).getData()).toFormatedJsonString();
        } finally {
          this.eventCache.closeConnectionOrTrace(connection);
        }
      }

      return this.formattedData;
    }
  }

  private void addControls() {
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    this.add(topPanel);

    this.addEventsTable(topPanel);
    this.addPageControls(topPanel);
    this.addButtons(topPanel);
  }

  private void addEventsTable(JPanel parent) {
    this.eventsTable = new JTable(new DefaultTableModel() {
      private static final long serialVersionUID = -4700723820298918429L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    });
    this.eventsTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          EventCacheDialog.this.openSelectedEventRowDetail();
        }
      }
    });
    parent.add(new JScrollPane(this.eventsTable));
  }

  void refresh() {
    this.pageIndex = 0;
    this.loadPage();
  }

  void goToNextPage() {
    this.pageIndex++;
    this.loadPage();
  }
  
  void goToPrevPage() {
    this.pageIndex--;
    this.loadPage();
  }
  
  private void loadPage() {
    Connection connection = null;
    EventCacheReader eventsReader = null;
    
    try {
      connection = this.eventCache.openConnection();
      eventsReader = this.eventCache.getEvents(connection, this.pageIndex, EventCacheDialog.PAGE_SIZE);
      this.lastPageIndex = (int)Math.ceil((double)this.eventCache.getEventCount(connection) / (double)EventCacheDialog.PAGE_SIZE) - 1;
      this.displayedEvents = this.createViewModels(eventsReader);
      this.setEventsTableData();
      this.updatePageControls();
      
    } catch (SQLException ex) {
      MessageBox.showError(this, "Failed to retrieve events from the cache.", ex, "Failed retrieve events.");
    } finally {
      if (eventsReader != null) {
        eventsReader.closeOrTrace();
      }
      if (connection != null) {
        this.eventCache.closeConnectionOrTrace(connection);
      }
    }
  }
  
  private void updatePageControls() {
    this.prevPageButton.setEnabled(this.pageIndex > 0);
    this.nextPageButton.setEnabled(this.pageIndex < this.lastPageIndex);
    this.pageLabel.setText(String.format("%s / %s", this.pageIndex + 1, this.lastPageIndex + 1));
  }
  
  private ArrayList<CachedEventViewModel> createViewModels(EventCacheReader cachedEventsReader) throws SQLException {
    ArrayList<CachedEventViewModel> viewModels = new ArrayList<>();
    while (cachedEventsReader.next()) {
      viewModels.add(new CachedEventViewModel(cachedEventsReader.getCurrent(), this.eventCache));
    }
    return viewModels;
  }

  private void setEventsTableData() {
    //set data
    Object[][] rows = new Object[this.displayedEvents.size()][3];
    for (int i = 0; i < this.displayedEvents.size(); i ++) {
      CachedEventViewModel viewModel = this.displayedEvents.get(i);
      rows[i] = new Object[] {viewModel.timestamp, viewModel.eventTypeShortUri, viewModel.eventId};
    }
    ((DefaultTableModel) this.eventsTable.getModel()).setDataVector(rows, new String[] {"Time", "Type", "ID"});

    //resize columns
    TableColumn timeColumn = this.eventsTable.getColumnModel().getColumn(0);
    timeColumn.setPreferredWidth(150);
    timeColumn.setMinWidth(0);
    timeColumn.setMaxWidth(1000);
  }

  void openSelectedEventRowDetail() {
    int selectedRowIndex = this.eventsTable.getSelectedRow();
    if (selectedRowIndex != -1) {
      try {
        new CachedEventDetailDialog(this, this.displayedEvents.get(selectedRowIndex).getFormattedData()).setVisible(true);
      } catch (IOException | SQLException ex) {
        MessageBox.showError(this, "Failed to retrieve the event detail.", ex, "Event detail failed");
      }
    }
  }

  private void addPageControls(JPanel panel) {
    JPanel controlsPanel = new JPanel();
    controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
    panel.add(controlsPanel);
    
    //'prev page' button
    this.prevPageButton = addButton(controlsPanel, "<< Previous Page", "Moves to the previous page", false, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        EventCacheDialog.this.goToPrevPage();
      }
    });
    this.prevPageButton.setEnabled(false);
    
    //'current page / last page' label
    this.pageLabel = new JLabel("0/0");
    controlsPanel.add(this.pageLabel);
    controlsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
    
    //'next page' button
    this.nextPageButton = addButton(controlsPanel, "Next Page >>", "Moves to the next page", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        EventCacheDialog.this.goToNextPage();
      }
    });
    this.nextPageButton.setEnabled(false);
  }
  
  private void addButtons(JPanel panel) {
    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
    panel.add(buttonsPanel);

    //'refresh' button
    addButton(buttonsPanel, "Refresh", "Reload events from the cache", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        EventCacheDialog.this.refresh();
      }
    });

    //'delete selection' button
    addButton(buttonsPanel, "Delete Selection", "Remove the selected events from the cache", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          ArrayList<Integer> selectedEventIds = new ArrayList<>();
          for (int eventIndex: EventCacheDialog.this.eventsTable.getSelectedRows()) {
            selectedEventIds.add(EventCacheDialog.this.displayedEvents.get(eventIndex).id);
          }
          EventCache eventCache = EventCacheDialog.this.eventCache;
          Connection connection = eventCache.openConnection();
          try{
            eventCache.removeEvents(connection, selectedEventIds);
          }finally{
            eventCache.closeConnectionOrTrace(connection);
          }
          EventCacheDialog.this.refresh();
        } catch (SQLException ex) {
          MessageBox.showError(EventCacheDialog.this, "Failed to delete all of the selected events.", ex, "Delete selection failed");
        }
      }
    });

    //'delete all' button
    addButton(buttonsPanel, "Delete All", "Remove all events from the cache", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          EventCache eventCache = EventCacheDialog.this.eventCache;
          Connection connection = eventCache.openConnection();
          try{
            eventCache.removeAllEvents(connection);
          }finally{
            eventCache.closeConnectionOrTrace(connection);
          }

          EventCacheDialog.this.refresh();
        } catch (SQLException ex) {
          MessageBox.showError(EventCacheDialog.this, "Failed to delete all events.", ex, "Delete all failed");
        }
      }
    });

    //'send now' button
    addButton(buttonsPanel, "Send Now", "Send all events, that are old enough, to the server now", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() throws Exception {
            EventCacheDialog.this.eventCommitJob.commitEventsNow(false);
            return null;
          }

          @Override
          protected void done() {
            EventCacheDialog.this.refresh();
          }
        }.execute();
      }
    });

    //'send now - force all' button
    addButton(buttonsPanel, "Send Now - Force All", "Send all events, no matter how old they are, to the server now", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() throws Exception {
            EventCacheDialog.this.eventCommitJob.commitEventsNow(true);
            return null;
          }

          @Override
          protected void done() {
            EventCacheDialog.this.refresh();
          }
        }.execute();
      }
    });

    //'close' button
    addButton(buttonsPanel, "Close", "Close the dialog", false, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        EventCacheDialog.this.setVisible(false);
      }
    });
  }

  private static JButton addButton(JPanel panel, String text, String toolTipText, boolean addMargin, ActionListener actionListener) {
    JButton button = new JButton(text);
    button.setToolTipText(toolTipText);
    button.setAlignmentY(CENTER_ALIGNMENT);
    button.setAlignmentX(CENTER_ALIGNMENT);
    //button.setMinimumSize(new Dimension(75, 0));
    //button.setMaximumSize(new Dimension(75, 500));
    button.addActionListener(actionListener);

    panel.add(button);
    if (addMargin) {
      panel.add(Box.createRigidArea(new Dimension(5, 0)));
    }
    
    return button;
  }
}
