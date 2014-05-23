package com.gratex.perconik.useractivity.app;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class AppTracerDialog extends JDialog {
	private static final long serialVersionUID = 4010888456482642956L;
	private JTable rowsTable;
	private ArrayList<AppTracerRow> displayedRows;
	private ArrayList<MessageSeverity> disabledSeverities = new ArrayList<>();
	
	public AppTracerDialog(JFrame parent) {
		super(parent, true);
		
		setTitle("Log");
		setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
		setSize(800, 500);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addControls();
		setLocationRelativeTo(null);
		
		refresh();
	}
	
	private void addControls() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		add(topPanel);
		
		addRowsTable(topPanel);
		addFilterControls(topPanel);
		addButtons(topPanel);
	}
	
	private void addRowsTable(JPanel panel) {
		this.rowsTable = new JTable(new DefaultTableModel() {
			private static final long serialVersionUID = 1557961781722615098L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			@Override
			public Class<?> getColumnClass(int columnIndex) { 
				return getValueAt(0, columnIndex).getClass();
			}
		});
		this.rowsTable.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					openSelectedRowDetail();
				}
			}
		});
		panel.add(new JScrollPane(this.rowsTable));
	}
	
	private void openSelectedRowDetail() {
		int selectedRowIndex = this.rowsTable.getSelectedRow();
		if(selectedRowIndex != -1) {
			new AppTracerRowDetailDialog(this, this.displayedRows.get(selectedRowIndex)).setVisible(true);
		}
	}
	
	private void addFilterControls(JPanel panel) {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
		panel.add(filterPanel);
		
		addFilterButton("Info", MessageSeverity.INFO, filterPanel);
		addFilterButton("Event Commit", MessageSeverity.INFO_EVENT_COMMIT, filterPanel);
		addFilterButton("Warning", MessageSeverity.WARNING, filterPanel);
		addFilterButton("Error", MessageSeverity.ERROR, filterPanel);
	}
	
	private void addFilterButton(String title, final MessageSeverity severity, JPanel panel) {
		JCheckBox button = new JCheckBox();
		button.setSelected(true);
		panel.add(button);
		
		JLabel titleLabel = new JLabel(title, getSeverityIcon(severity), JLabel.CENTER);
		panel.add(titleLabel);
		
		panel.add(Box.createRigidArea(new Dimension(15, 0)));
		
		button.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {				
				AppTracerDialog.this.setIsSeverityEnabled(severity, ((JCheckBox)e.getSource()).isSelected());
			}
		});
	}

	private void setIsSeverityEnabled(MessageSeverity severity, boolean isEnabled) {
		if(isEnabled) {
			disabledSeverities.remove(severity);	
		}
		else {
			disabledSeverities.add(severity);
		}
		refresh();
	}
	
	private void addButtons(JPanel panel) {
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		panel.add(buttonsPanel);
		
		//'refresh' button
		addButton(buttonsPanel, "Refresh", "Reload the log", true, new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		
		//'delete all' button
		addButton(buttonsPanel, "Delete All", "Clear the log", true, new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				AppTracer.getInstance().clear();
				refresh();
			}
		});
		
		//'close' button
		addButton(buttonsPanel, "Close", "Close the dialog", false, new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				AppTracerDialog.this.setVisible(false);
			}
		});
	}
	
	private void addButton(JPanel panel, String text, String toolTipText, boolean addMargin, ActionListener actionListener) {		
		JButton button = new JButton(text);
		button.setToolTipText(toolTipText);
		button.setAlignmentY(CENTER_ALIGNMENT);
		button.setAlignmentX(CENTER_ALIGNMENT);
		button.addActionListener(actionListener);
		
		panel.add(button);
		if(addMargin) {
			panel.add(Box.createRigidArea(new Dimension(5, 0)));
		}
	}
	
	private void refresh() {
		displayedRows = new ArrayList<AppTracerRow>();		
		for (AppTracerRow row : AppTracer.getInstance().getRows()) {
			if(!disabledSeverities.contains(row.getSeverity())) {
				displayedRows.add(row);
			}
		}
		
		setRowsTableData();
	}
	
	private void setRowsTableData() {
		//set data
		Object[][] rows = new Object[this.displayedRows.size()][3];
		for(int i = 0; i < this.displayedRows.size(); i++) {
			AppTracerRow appTracerRow = this.displayedRows.get(i);
			rows[i] = new Object[] { getSeverityIcon(appTracerRow.getSeverity()),
									 SimpleDateFormat.getInstance().format(appTracerRow.getTime()), 
									 appTracerRow.getMessage() };
		}
		((DefaultTableModel)this.rowsTable.getModel()).setDataVector(rows, new String[] { "", "Time", "Message"});
		
		//resize columns
		TableColumn severityColumn = this.rowsTable.getColumnModel().getColumn(0);
		severityColumn.setPreferredWidth(16);
		severityColumn.setMinWidth(0);
		severityColumn.setMaxWidth(16);
		
		TableColumn timeColumn = this.rowsTable.getColumnModel().getColumn(1);
		timeColumn.setPreferredWidth(150);
		timeColumn.setMinWidth(0);
		timeColumn.setMaxWidth(1000);
	}

	private ImageIcon getSeverityIcon(MessageSeverity severity) {
		switch (severity) {
			case WARNING:
				return ResourcesHelper.getWarningSeverityIcon16();				
				
			case ERROR:
				return ResourcesHelper.getErrorSeverityIcon16();
				
			case INFO_EVENT_COMMIT:
				return ResourcesHelper.getInfoEventCommitSeverityIcon16();
				
			case INFO:
			default:
				return ResourcesHelper.getInfoSeverityIcon16();
		}
	}
}