package com.gratex.perconik.useractivity.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.gratex.perconik.useractivity.app.dto.CachedEvent;

public class CachedEventDetailDialog extends JDialog {
	private static final long serialVersionUID = -3333495706407138000L;
	
	public CachedEventDetailDialog(JDialog parent, CachedEvent cachedEvent) {
		super(parent, true);
		
		setTitle("Event Detail");
		setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
		setSize(800, 500);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addControls(cachedEvent);
		setLocationRelativeTo(null);
	}
	
	private void addControls(CachedEvent cachedEvent) {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		add(topPanel);
		
		//data text box
		JTextArea dataTextBox = new JTextArea(cachedEvent.getData());
		dataTextBox.setEditable(false);
		dataTextBox.setLineWrap(true);
		topPanel.add(dataTextBox);
		
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
