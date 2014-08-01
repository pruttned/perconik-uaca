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
  private boolean isUserNameChanged = false;

  public SettingsDialog(JFrame parent) {
    super(parent, true);

    this.setTitle("Settings");
    this.setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.addControls();
    this.pack();
    this.setSize(800, this.getSize().height);
    this.setLocationRelativeTo(null);
  }

  public boolean areChangesApplied() {
    return this.areChangesApplied;
  }

  public boolean isUserNameChanged() {
    return this.isUserNameChanged;
  }

  private void addControls() {
    this.setLayout(new GridBagLayout());

    //event commit interval
    JSpinner eventCommitIntervalSpinner = this.createSpinner(Settings.getInstance().getEventCommitInterval() / 60000); //milliseconds to minutes
    this.addField("Events are sent to the server every (minutes)", eventCommitIntervalSpinner);

    //event age to commit
    JSpinner eventAgeToCommitSpinner = this.createSpinner(Settings.getInstance().getEventAgeToCommit() / 60000); //milliseconds to minutes
    this.addField("Send only events older than (minutes)", eventAgeToCommitSpinner);

    //user name
    JTextField userNameTextBox = new JTextField(Settings.getInstance().getUserName());
    this.addField("User name (sent with each event)", userNameTextBox);

    //svc url
    JTextField svcUrlTextBox = new JTextField(Settings.getInstance().getSvcUrl());
    this.addField("Service URL (where events are sent)", svcUrlTextBox);

    //local svc port
    JSpinner localSvcPortSpinner = this.addLocalSvcPortField();

    //'OK' and 'cancel' buttons
    this.addCloseButtons(eventCommitIntervalSpinner, eventAgeToCommitSpinner, userNameTextBox, svcUrlTextBox, localSvcPortSpinner);
  }

  private JSpinner createSpinner(long value) {
    JSpinner spinner = new JSpinner();
    spinner.setEditor(new JSpinner.NumberEditor(spinner, "#"));
    SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
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
    this.add(new JLabel(title + ":"), titleConstraints);

    //control
    GridBagConstraints controlConstraints = new GridBagConstraints();
    controlConstraints.anchor = GridBagConstraints.LINE_START;
    controlConstraints.gridx = 1;
    controlConstraints.gridy = GridBagConstraints.RELATIVE;
    controlConstraints.weightx = 1;
    controlConstraints.fill = GridBagConstraints.HORIZONTAL;
    controlConstraints.insets = new Insets(2, 2, 0, 0);
    this.add(control, controlConstraints);
  }

  private JSpinner addLocalSvcPortField() {
    int topMargin = 30;

    //title
    GridBagConstraints titleConstraints = new GridBagConstraints();
    titleConstraints.anchor = GridBagConstraints.LINE_START;
    titleConstraints.gridx = 0;
    titleConstraints.gridy = GridBagConstraints.RELATIVE;
    titleConstraints.insets = new Insets(topMargin, 0, 0, 0);
    this.add(new JLabel("Local services port (for communication with IDE, web browser...):"), titleConstraints);

    //control
    JSpinner control = this.createSpinner(Settings.getInstance().getLocalSvcPort());
    GridBagConstraints controlConstraints = new GridBagConstraints();
    controlConstraints.anchor = GridBagConstraints.LINE_START;
    controlConstraints.gridx = 1;
    controlConstraints.gridy = GridBagConstraints.RELATIVE;
    controlConstraints.weightx = 1;
    controlConstraints.fill = GridBagConstraints.HORIZONTAL;
    controlConstraints.insets = new Insets(topMargin, 2, 0, 0);
    this.add(control, controlConstraints);

    //info
    JLabel infoLabel = new JLabel("<html>- Requires restart<br>- Must be set in each plug-in (web browser, IDE...) manually</html>");
    GridBagConstraints infoLabelConstraints = new GridBagConstraints();
    infoLabelConstraints.anchor = GridBagConstraints.LINE_START;
    infoLabelConstraints.gridx = 1;
    infoLabelConstraints.gridy = GridBagConstraints.RELATIVE;
    infoLabelConstraints.weightx = 1;
    infoLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
    infoLabelConstraints.insets = new Insets(0, 2, 0, 0);
    this.add(infoLabel, infoLabelConstraints);

    return control;
  }

  private void addCloseButtons(final JSpinner eventCommitIntervalSpinner, final JSpinner eventAgeToCommitSpinner, final JTextField userNameTextBox, final JTextField svcUrlTextBox, final JSpinner localSvcPortSpinner) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    GridBagConstraints panelConstraints = new GridBagConstraints();
    panelConstraints.gridwidth = 2;
    panelConstraints.anchor = GridBagConstraints.CENTER;
    panelConstraints.gridx = 0;
    panelConstraints.gridy = GridBagConstraints.RELATIVE;
    panelConstraints.weightx = 1;
    panelConstraints.insets = new Insets(5, 0, 0, 0);
    this.add(panel, panelConstraints);

    //'OK' button
    JButton okButton = new JButton("OK");
    okButton.setToolTipText("Accept changes");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (SettingsDialog.this.applyChanges(eventCommitIntervalSpinner, eventAgeToCommitSpinner, userNameTextBox, svcUrlTextBox, localSvcPortSpinner)) {
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

  boolean applyChanges(JSpinner eventCommitIntervalSpinner, JSpinner eventAgeToCommitSpinner, JTextField userNameTextBox, JTextField svcUrlTextBox, JSpinner localSvcPortSpinner) {
    if (ValidationHelper.isStringNullOrWhitespace(userNameTextBox.getText()) || ValidationHelper.isStringNullOrWhitespace(svcUrlTextBox.getText())) {
      MessageBox.showError(this, "Please fill all fields.", "Empty fields!");
      return false;
    }

    this.isUserNameChanged = !userNameTextBox.getText().equals(Settings.getInstance().getUserName());

    Settings.getInstance().setEventCommitInterval(this.getSpinnerValue(eventCommitIntervalSpinner) * 60000L);
    Settings.getInstance().setEventAgeToCommit(this.getSpinnerValue(eventAgeToCommitSpinner) * 60000L);
    Settings.getInstance().setUserName(userNameTextBox.getText());
    Settings.getInstance().setSvcUrl(svcUrlTextBox.getText());
    Settings.getInstance().setLocalSvcPort(this.getSpinnerValue(localSvcPortSpinner));

    this.areChangesApplied = true;
    return true;
  }

  private int getSpinnerValue(JSpinner spinner) {
    return ((SpinnerNumberModel) spinner.getModel()).getNumber().intValue();
  }
}
