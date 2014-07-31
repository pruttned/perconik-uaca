package com.gratex.perconik.useractivity.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CachedEventDetailDialog extends JDialog {
  private static final long serialVersionUID = -3333495706407138000L;

  public CachedEventDetailDialog(JDialog parent, String eventData) {
    super(parent, true);

    this.setTitle("Event Detail");
    this.setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
    this.setSize(800, 500);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.addControls(eventData);
    this.setLocationRelativeTo(null);
  }

  private void addControls(String eventData) {
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    this.add(topPanel);

    //data text box
    JTextArea dataTextBox = new JTextArea(eventData);
    dataTextBox.setEditable(false);
    dataTextBox.setLineWrap(true);
    topPanel.add(new JScrollPane(dataTextBox));

    //close button
    JButton closeButton = new JButton("Close");
    closeButton.setToolTipText("Close the dialog");
    closeButton.setAlignmentX(CENTER_ALIGNMENT);
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        CachedEventDetailDialog.this.setVisible(false);
      }
    });
    topPanel.add(closeButton);
  }
}
