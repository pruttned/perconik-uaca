package com.gratex.perconik.useractivity.app;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class SettingsDialog extends JDialog {
	private static final long serialVersionUID = 2908926995613740506L;
	private boolean areChangesApplied = false;
	
	public SettingsDialog(JFrame parent) {
		super(parent, true);
		
		setTitle("Settings");
		setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addControls();
		pack();
		setSize(500, getSize().height);
		setLocationRelativeTo(null);
	}
	
	public boolean areChangesApplied() {
		return this.areChangesApplied;
	}
	
	private void addControls() {
		setLayout(new GridBagLayout());
		
		//event commit interval
		JSpinner eventCommitIntervalSpinner = createSpinner(Settings.getInstance().getEventCommitInterval() / 60000); //milliseconds to minutes
		addField("Event Commit Interval (Minutes)", eventCommitIntervalSpinner);
		
		//event age to commit
		JSpinner eventAgeToCommitSpinner = createSpinner(Settings.getInstance().getEventAgeToCommit() / 60000); //milliseconds to minutes
		addField("Event Age To Commit (Minutes)", eventAgeToCommitSpinner);
		
		//user name
		JTextField userNameTextBox = new JTextField(Settings.getInstance().getUserName());
		addField("User Name", userNameTextBox);
		
		//'OK' and 'cancel' buttons
		addCloseButtons(eventCommitIntervalSpinner, eventAgeToCommitSpinner, userNameTextBox);
	}
	
	private JSpinner createSpinner(long value) {
		JSpinner spinner = new JSpinner();
		SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
		model.setMinimum(0);
		model.setValue(value);
		return spinner;
	}
	
	private void addField(String title, Component control) {
		//title
		GridBagConstraints titleConstraints = new GridBagConstraints();		
		titleConstraints.anchor = GridBagConstraints.LINE_START;
		titleConstraints.gridx = 0;
		titleConstraints.gridy = GridBagConstraints.RELATIVE;		
		titleConstraints.insets = new Insets(2, 0, 0, 0);
		add(new JLabel(title + ":"), titleConstraints);
		
		//control
		GridBagConstraints controlConstraints = new GridBagConstraints();
		controlConstraints.anchor = GridBagConstraints.LINE_START;
		controlConstraints.gridx = 1;
		controlConstraints.gridy = GridBagConstraints.RELATIVE;		
		controlConstraints.weightx = 1;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		controlConstraints.insets = new Insets(2, 2, 0, 0);		
		add(control, controlConstraints);
	}
	
	private void addCloseButtons(final JSpinner eventCommitIntervalSpinner, final JSpinner eventAgeToCommitSpinner, final JTextField userNameTextBox) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		GridBagConstraints panelConstraints = new GridBagConstraints();
		panelConstraints.gridwidth = 2;
		panelConstraints.anchor = GridBagConstraints.CENTER;
		panelConstraints.gridx = 0;
		panelConstraints.gridy = GridBagConstraints.RELATIVE;
		panelConstraints.weightx = 1;
		panelConstraints.insets = new Insets(5, 0, 0, 0);
		add(panel, panelConstraints);
					
		//'OK' button
		JButton okButton = new JButton("OK");
		okButton.setToolTipText("Accept changes");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(applyChanges(eventCommitIntervalSpinner, eventAgeToCommitSpinner, userNameTextBox)) {
					SettingsDialog.this.setVisible(false);
				}
			}
		});
		panel.add(okButton);
		
		//'cancel' button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setToolTipText("Cancel changes");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SettingsDialog.this.setVisible(false);
			}
		});
		panel.add(cancelButton);
	}
	
	private boolean applyChanges(JSpinner eventCommitIntervalSpinner, JSpinner eventAgeToCommitSpinner, JTextField userNameTextBox) {
		if(ValidationHelper.isStringNullOrWhitespace(userNameTextBox.getText())) {
			MessageBox.showError(this, "Please fill all fields.", "Empty fields!");
			return false;
		}
		
		Settings.getInstance().setEventCommitInterval(getSpinnerValue(eventCommitIntervalSpinner) * 60000);
		Settings.getInstance().setEventAgeToCommit(getSpinnerValue(eventAgeToCommitSpinner) * 60000);
		Settings.getInstance().setUserName(userNameTextBox.getText());
		
		this.areChangesApplied = true;
		return true;		
	}
	
	private int getSpinnerValue(JSpinner spinner) {
		return ((SpinnerNumberModel)spinner.getModel()).getNumber().intValue();
	}
}
