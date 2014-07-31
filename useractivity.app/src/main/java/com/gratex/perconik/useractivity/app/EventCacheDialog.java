package com.gratex.perconik.useractivity.app;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gratex.perconik.useractivity.app.dto.CachedEvent;

public class EventCacheDialog extends JDialog {
	
	private class CachedEventViewModel {
		private EventCache eventCache;
		private int id;
		private String eventId;
		private String timestamp;
		private String eventTypeShortUri;
		private String formattedData;		
		
		public CachedEventViewModel(CachedEvent cachedEvent, EventCache eventCache) {
			this.eventCache = eventCache;
			id = cachedEvent.getId();
			eventId = cachedEvent.getEventId();
			timestamp = XMLGregorianCalendarHelper.toLocalString(cachedEvent.getTimestamp());
			
			try {
				EventDocument doc = new EventDocument(cachedEvent.getData());
				eventTypeShortUri = TypeUriHelper.getEventTypeShortUri(doc.getEventTypeUri());
				//formattedData = doc.toFormatedJsonString();
			} catch (IOException ex) {
				AppTracer.getInstance().writeError(String.format("Failed to deserialize the event with ID '%s'.", cachedEvent.getEventId()), ex);
				eventTypeShortUri = "<ERROR - see log for details>";
				//formattedData = "<ERROR - see log for details>";
			}
		}
		
		public String getFormattedData() throws JsonProcessingException, IOException, SQLException {
			if(formattedData == null) {
				formattedData = new EventDocument(this.eventCache.getEvent(id).getData()).toFormatedJsonString();
			}
			return formattedData;
		}
	}
	
	private static final long serialVersionUID = 3565081061317049889L;
	private EventCache eventCache;
	private EventCommitJob eventCommitJob;
	private JTable eventsTable;
	private ArrayList<CachedEventViewModel> displayedEvents; //events currently displayed to the user
	
	public EventCacheDialog(JFrame parent, EventCache eventCache, EventCommitJob eventCommitJob) {
		super(parent, true);
		
		this.eventCache = eventCache;
		this.eventCommitJob = eventCommitJob;
		
		setTitle("Event Cache");
		setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
		setSize(900, 500);		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addControls();
		setLocationRelativeTo(null);
		
		refreshEvents();
	}
	
	private void addControls() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		add(topPanel);
		
		addEventsTable(topPanel);
		addButtons(topPanel);
	}
	
	private void addEventsTable(JPanel parent) {
		this.eventsTable = new JTable(new DefaultTableModel() {
			private static final long serialVersionUID = -4700723820298918429L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		this.eventsTable.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					openSelectedEventRowDetail();
				}
			}
		});
		parent.add(new JScrollPane(this.eventsTable));
	}
	
	private void refreshEvents() {
		try {
			EventCacheReader eventsReader = eventCache.getEvents();
			displayedEvents = createViewModels(eventsReader);
			eventsReader.close();
			
			setEventsTableData();
		} catch (SQLException ex) {
			MessageBox.showError(this, "Failed to retrieve events from the cache.", ex, "Failed retrieve events.");
		}
	}
	
	private ArrayList<CachedEventViewModel> createViewModels(EventCacheReader cachedEventsReader) throws SQLException {
		ArrayList<CachedEventViewModel> viewModels = new ArrayList<>();
		while(cachedEventsReader.next()) {
			viewModels.add(new CachedEventViewModel(cachedEventsReader.getCurrent(), eventCache));
		}
		return viewModels;
	}

	private void setEventsTableData() {
		//set data
		Object[][] rows = new Object[displayedEvents.size()][3];
		for(int i = 0; i < displayedEvents.size(); i++) {
			CachedEventViewModel viewModel = displayedEvents.get(i);
			rows[i] = new Object[] { viewModel.timestamp, viewModel.eventTypeShortUri, viewModel.eventId };
		}
		((DefaultTableModel)this.eventsTable.getModel()).setDataVector(rows, new String[] { "Time", "Type", "ID"});
		
		//resize columns
		TableColumn timeColumn = this.eventsTable.getColumnModel().getColumn(0);
		timeColumn.setPreferredWidth(150);
		timeColumn.setMinWidth(0);
		timeColumn.setMaxWidth(1000);
	}
	
	private void openSelectedEventRowDetail() {
		int selectedRowIndex = this.eventsTable.getSelectedRow();
		if(selectedRowIndex != -1) {
			try {
				new CachedEventDetailDialog(this, this.displayedEvents.get(selectedRowIndex).getFormattedData()).setVisible(true);
			} catch (IOException | SQLException ex) {
				MessageBox.showError(this, "Failed to retrieve the event detail.", ex, "Event detail failed");
			}
		}
	}
	
	private void addButtons(JPanel panel) {
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		panel.add(buttonsPanel);
		
		//'refresh' button
		addButton(buttonsPanel, "Refresh", "Reload events from the cache", true, new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				refreshEvents();
			}
		});
		
		//'delete selection' button
		addButton(buttonsPanel, "Delete Selection", "Remove the selected events from the cache", true, new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				try {
					ArrayList<Integer> selectedEventIds = new ArrayList<Integer>();
					for (int eventIndex : EventCacheDialog.this.eventsTable.getSelectedRows()) {
						selectedEventIds.add(EventCacheDialog.this.displayedEvents.get(eventIndex).id);
					}
					EventCacheDialog.this.eventCache.removeEvents(selectedEventIds);
					refreshEvents();
				} catch(SQLException ex) {
					MessageBox.showError(EventCacheDialog.this, "Failed to delete all of the selected events.", ex, "Delete selection failed");
				}
			}
		});
		
		//'delete all' button
		addButton(buttonsPanel, "Delete All", "Remove all events from the cache", true, new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				try {
					EventCacheDialog.this.eventCache.removeAllEvents();
					refreshEvents();
				} catch(SQLException ex) {
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
						eventCommitJob.commitEventsNow(false);
						return null;
					}
					@Override
					protected void done() {
						EventCacheDialog.this.refreshEvents();
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
						eventCommitJob.commitEventsNow(true);
						return null;
					}
					@Override
					protected void done() {
						EventCacheDialog.this.refreshEvents();
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
	
	private void addButton(JPanel panel, String text, String toolTipText, boolean addMargin, ActionListener actionListener) {		
		JButton button = new JButton(text);
		button.setToolTipText(toolTipText);
		button.setAlignmentY(CENTER_ALIGNMENT);
		button.setAlignmentX(CENTER_ALIGNMENT);
		//button.setMinimumSize(new Dimension(75, 0));
		//button.setMaximumSize(new Dimension(75, 500));
		button.addActionListener(actionListener);
		
		panel.add(button);
		if(addMargin) {
			panel.add(Box.createRigidArea(new Dimension(5, 0)));
		}
	}
}