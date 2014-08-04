package com.gratex.perconik.useractivity.app;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class AppTracerDialog extends JDialog {
  private static final long serialVersionUID = 4010888456482642956L;

  private final ArrayList<MessageSeverity> disabledSeverities = new ArrayList<>();

  private JTable rowsTable;
  private ArrayList<AppTracerRow> displayedRows;

  public AppTracerDialog(JFrame parent) {
    super(parent, true);

    this.setTitle("Log");
    this.setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
    this.setSize(800, 500);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.addControls();
    this.setLocationRelativeTo(null);

    this.refresh();
  }

  private void addControls() {
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    this.add(topPanel);

    this.addRowsTable(topPanel);
    this.addFilterControls(topPanel);
    this.addMaxRowCountControls(topPanel);
    this.addButtons(topPanel);
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
        return this.getValueAt(0, columnIndex).getClass();
      }
    });
    this.rowsTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          AppTracerDialog.this.openSelectedRowDetail();
        }
      }
    });
    panel.add(new JScrollPane(this.rowsTable));
  }

  void openSelectedRowDetail() {
    int selectedRowIndex = this.rowsTable.getSelectedRow();
    if (selectedRowIndex != -1) {
      new AppTracerRowDetailDialog(this, this.displayedRows.get(selectedRowIndex)).setVisible(true);
    }
  }

  private void addMaxRowCountControls(JPanel panel) {
    JPanel maxRowCountPanel = new JPanel();
    maxRowCountPanel.setLayout(new BoxLayout(maxRowCountPanel, BoxLayout.X_AXIS));
    panel.add(maxRowCountPanel);

    //title
    JLabel titleLabel = new JLabel("Max row count:");
    maxRowCountPanel.add(titleLabel);
    maxRowCountPanel.add(Box.createRigidArea(new Dimension(2, 0)));

    //value
    final JLabel valueLabel = new JLabel(String.valueOf(Settings.getInstance().getMaxRowCountInLog()));
    maxRowCountPanel.add(valueLabel);
    maxRowCountPanel.add(Box.createRigidArea(new Dimension(2, 0)));

    //change button
    JButton changeButton = new JButton("Change...");
    changeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        String result = JOptionPane.showInputDialog(AppTracerDialog.this, "Enter new 'max row count':");
        if (!ValidationHelper.isStringNullOrWhitespace(result)) {
          try {
            int newValue = Math.max(Integer.parseInt(result), 0);
            Settings.getInstance().setMaxRowCountInLog(newValue);
            AppTracerDialog.this.refresh();
            valueLabel.setText(String.valueOf(newValue));
          } catch (Throwable ex) {
            MessageBox.showError(AppTracerDialog.this, "The value must be a number.", "Not a number");
          }
        }
      }
    });
    maxRowCountPanel.add(changeButton);

    //bottom margin
    panel.add(Box.createRigidArea(new Dimension(0, 15)));
  }

  private void addFilterControls(JPanel panel) {
    JPanel filterPanel = new JPanel();
    filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
    panel.add(filterPanel);

    this.addFilterButton("Info", MessageSeverity.INFO, filterPanel);
    this.addFilterButton("Event Commit", MessageSeverity.INFO_EVENT_COMMIT, filterPanel);
    this.addFilterButton("Warning", MessageSeverity.WARNING, filterPanel);
    this.addFilterButton("Error", MessageSeverity.ERROR, filterPanel);
  }

  private void addFilterButton(String title, final MessageSeverity severity, JPanel panel) {
    JCheckBox button = new JCheckBox();
    button.setSelected(true);
    panel.add(button);

    JLabel titleLabel = new JLabel(title, getSeverityIcon(severity), SwingConstants.CENTER);
    panel.add(titleLabel);

    panel.add(Box.createRigidArea(new Dimension(15, 0)));

    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        AppTracerDialog.this.setIsSeverityEnabled(severity, ((JCheckBox) e.getSource()).isSelected());
      }
    });
  }

  void setIsSeverityEnabled(MessageSeverity severity, boolean isEnabled) {
    if (isEnabled) {
      this.disabledSeverities.remove(severity);
    } else {
      this.disabledSeverities.add(severity);
    }
    this.refresh();
  }

  private void addButtons(JPanel panel) {
    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
    panel.add(buttonsPanel);

    //'refresh' button
    addButton(buttonsPanel, "Refresh", "Reload the log", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        AppTracerDialog.this.refresh();
      }
    });

    //'delete all' button
    addButton(buttonsPanel, "Delete All", "Clear the log", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        AppTracer.getInstance().clear();
        AppTracerDialog.this.refresh();
      }
    });

    //'close' button
    addButton(buttonsPanel, "Close", "Close the dialog", false, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        AppTracerDialog.this.setVisible(false);
      }
    });
  }

  private static void addButton(JPanel panel, String text, String toolTipText, boolean addMargin, ActionListener actionListener) {
    JButton button = new JButton(text);
    button.setToolTipText(toolTipText);
    button.setAlignmentY(CENTER_ALIGNMENT);
    button.setAlignmentX(CENTER_ALIGNMENT);
    button.addActionListener(actionListener);

    panel.add(button);
    if (addMargin) {
      panel.add(Box.createRigidArea(new Dimension(5, 0)));
    }
  }

  void refresh() {
    this.displayedRows = new ArrayList<>();
    for (AppTracerRow row: AppTracer.getInstance().getRows()) {
      if (!this.disabledSeverities.contains(row.getSeverity())) {
        this.displayedRows.add(row);
      }
    }

    this.setRowsTableData();
  }

  private void setRowsTableData() {
    //set data
    Object[][] rows = new Object[this.displayedRows.size()][3];
    for (int i = 0; i < this.displayedRows.size(); i ++) {
      AppTracerRow appTracerRow = this.displayedRows.get(i);
      rows[i] = new Object[] {getSeverityIcon(appTracerRow.getSeverity()), DateFormat.getInstance().format(appTracerRow.getTime()), appTracerRow.getMessage()};
    }
    ((DefaultTableModel) this.rowsTable.getModel()).setDataVector(rows, new String[] {"", "Time", "Message"});

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

  private static ImageIcon getSeverityIcon(MessageSeverity severity) {
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
