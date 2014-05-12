package com.gratex.perconik.useractivity.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AppTracerRowDetailDialog extends JDialog {
	private static final long serialVersionUID = 6367895829127550046L;

	public AppTracerRowDetailDialog(JDialog parent, AppTracerRow appTracerRow) {
		super(parent, true);
		
		setTitle("Log Row Detail");
		setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
		setSize(800, 500);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addControls(appTracerRow);
		setLocationRelativeTo(null);
	}
	
	private void addControls(AppTracerRow appTracerRow) {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		add(topPanel);
		
		//message text box
		JTextArea messageTextBox = new JTextArea(appTracerRow.getMessage());
		messageTextBox.setEditable(false);
		topPanel.add(new JScrollPane(messageTextBox));
		
		//close button
		JButton closeButton = new JButton("Close");
		closeButton.setToolTipText("Close the dialog");
		closeButton.setAlignmentX(CENTER_ALIGNMENT);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AppTracerRowDetailDialog.this.setVisible(false);
			}
		});
		topPanel.add(closeButton);
	}
}
