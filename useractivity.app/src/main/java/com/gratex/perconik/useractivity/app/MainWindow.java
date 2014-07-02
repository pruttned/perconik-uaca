package com.gratex.perconik.useractivity.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 6907023637057102017L;
	private App app;
	private EventCache eventCache;	
	private JButton pauseButton;
	private JLabel pauseStatusLabel;
	
	public MainWindow(App app, EventCache eventCache) {
		this.app = app;
		this.eventCache = eventCache;
		
		setTitle("User Activity");
		setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
		setMinimumSize(new Dimension(220, 0));
		setResizable(false);
		addControls();
		pack();
		setLocationRelativeTo(null);
	}

	public void updateIsCollectingAndCommittingControls() {
		updatePauseStatusLabel();
		updatePauseButton();
	}
	
	private void addControls() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		add(topPanel);

		//title
		JLabel titleLabel = new JLabel("User Activity");
		titleLabel.setAlignmentX(CENTER_ALIGNMENT);
		titleLabel.setFont(titleLabel.getFont().deriveFont(25.0f));
		topPanel.add(titleLabel);		
	
		//version
		JLabel versionLabel = new JLabel(String.format("Version: %s", Settings.getInstance().getVersion()));
		versionLabel.setAlignmentX(CENTER_ALIGNMENT);		
		topPanel.add(versionLabel);
		topPanel.add(Box.createRigidArea(new Dimension(0,  10)));
		
		//'pause status' label
		pauseStatusLabel = new JLabel();
		pauseStatusLabel.setAlignmentX(CENTER_ALIGNMENT);		
		topPanel.add(pauseStatusLabel);
		topPanel.add(Box.createRigidArea(new Dimension(0,  10)));
		updatePauseStatusLabel();
		
		//'pause' button
		pauseButton = addButton(topPanel, "", "", true, new ActionListener() { //texts are set by 'updatePauseButton()'
			public void actionPerformed(ActionEvent arg0) {
				app.toggleCollectingAndCommitting();
			}
		});
		updatePauseButton();
		
		//'eventCache' button
		addButton(topPanel, "Event Cache", "Show events that have not yet been sent to the server", true, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new EventCacheDialog(MainWindow.this, MainWindow.this.eventCache, MainWindow.this.app.getEventCommitJob()).setVisible(true);
			}
		});
		
		//'settings' button
		addButton(topPanel, "Settings", "Show the settings dialog", true, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SettingsDialog dialog = new SettingsDialog(MainWindow.this);
				dialog.setVisible(true);
				if(dialog.areChangesApplied()) {
					MainWindow.this.app.getEventCommitJob().restartIfRunning();
					MainWindow.this.app.getUserActivityServiceProxy().setSvcUrl(Settings.getInstance().getSvcUrl());
					
					if(dialog.isUserNameChanged()) {
						boolean updateUserName = MessageBox.showYesNoQuestion(MainWindow.this, "The user name has been changed. Update the user name for all existing events in the event cache?", 
								"Update user name in event cache?");
						
						if(updateUserName) {
							try {
								MainWindow.this.eventCache.updateUserNameInAllEvents();
								MessageBox.showOkInfo(MainWindow.this, "Existing events updated!", "Done!");
							} catch (SQLException ex) {
								MessageBox.showError(MainWindow.this, "Failed to update events.", ex, "Failed!");
							}
						}
					}
				}
			}
		});		

		//'log' button
		addButton(topPanel, "Log", "Show the log", true, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new AppTracerDialog(MainWindow.this).setVisible(true);
			}
		});
		
		//'exit' button
		addButton(topPanel, "Shut Down", "Shut the User Activity down - nothing will be collected or sent to the server", true, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
	}
	
	private JButton addButton(JPanel panel, String text, String toolTipText, boolean addBottomMargin, ActionListener actionListener) {
		JButton button = new JButton(text);
		button.setToolTipText(toolTipText);
		button.setAlignmentX(CENTER_ALIGNMENT);
		button.setMinimumSize(new Dimension(120, 0));
		button.setMaximumSize(new Dimension(120, 500));		
		button.addActionListener(actionListener);
		
		panel.add(button);		
		if(addBottomMargin) {
			panel.add(Box.createRigidArea(new Dimension(0, 5)));
		}
		
		return button;
	}
	
	private void updatePauseButton() {
		if(this.app.isCollectingAndCommitting()) {
			pauseButton.setText("Disable");
			pauseButton.setToolTipText("Temporarily disable the User Activity - nothing will be collected or sent to the server");			
		} else {
			pauseButton.setText("Enable");
			pauseButton.setToolTipText("Enable the User Activity");
		}
	}
	
	private void updatePauseStatusLabel() {
		if(this.app.isCollectingAndCommitting()) {
			pauseStatusLabel.setText("Enabled...");
			pauseStatusLabel.setForeground(new Color(0, 180, 0));
		} else {
			pauseStatusLabel.setText("Disabled!");
			pauseStatusLabel.setForeground(Color.RED);
		}
	}
}