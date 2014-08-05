package com.gratex.perconik.useractivity.app.logging;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.gratex.perconik.useractivity.app.ResourcesHelper;

public final class LogRecordDialog extends JDialog {
  private static final long serialVersionUID = 6367895829127550046L;

  public LogRecordDialog(JDialog parent, LogRecord logRecord) {
    super(parent, true);

    this.setTitle("Log Record Detail");
    this.setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
    this.setSize(800, 500);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.addControls(logRecord);
    this.setLocationRelativeTo(null);
  }

  private void addControls(LogRecord LogRecord) {
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    this.add(topPanel);

    //message text box
    JTextArea messageTextBox = new JTextArea(LogRecord.getMessage());
    messageTextBox.setEditable(false);
    topPanel.add(new JScrollPane(messageTextBox));

    //close button
    JButton closeButton = new JButton("Close");
    closeButton.setToolTipText("Close the dialog");
    closeButton.setAlignmentX(CENTER_ALIGNMENT);
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        LogRecordDialog.this.setVisible(false);
      }
    });
    topPanel.add(closeButton);
  }
}
