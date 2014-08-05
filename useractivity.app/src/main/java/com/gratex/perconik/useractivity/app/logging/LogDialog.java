package com.gratex.perconik.useractivity.app.logging;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.gratex.perconik.useractivity.app.MessageBox;
import com.gratex.perconik.useractivity.app.ResourcesHelper;
import com.gratex.perconik.useractivity.app.XmlGregorianCalendarHelper;

public final class LogDialog extends JDialog {
  private static final long serialVersionUID = 4010888456482642956L;
  
  private Logger logger;
  private LogReader logReader;
  
  private JTable recordsTable;
  private JButton nextPageButton;
  private JButton prevPageButton;

  //private boolean[] severityEnableStates; //VSTUP DO LOGGER.GETEVENTS - BUDE SA POTOM PAGEOVAT LEN PO TYCH RECORDOCH
  
  public LogDialog(JFrame parent) throws SQLException {
    super(parent, true);

    this.logger = new Logger();
    
    /*severityEnableStates = new boolean[4];
    severityEnableStates[Logger.INFO_SEVERITY] = true;
    severityEnableStates[Logger.WARNING_SEVERITY] = true;
    severityEnableStates[Logger.ERROR_SEVERITY] = true;
    severityEnableStates[Logger.INFO_EVENT_COMMIT_SEVERITY] = false;*/
    
    this.setTitle("Log");
    this.setIconImage(ResourcesHelper.getUserActivityIcon16().getImage());
    this.setSize(800, 500);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    
    this.addControls();
    this.setLocationRelativeTo(null);

    this.refresh();
  }
  
  public void closeOrTrace() {
    this.closeLogReaderOrTrace();
    this.logger.closeSilent();
  }
  
  private void closeLogReaderOrTrace() {
    if(this.logReader != null) {
      this.logReader.closeOrTrace();
      this.logReader = null;
    }
  }
  
  private void addControls() {
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    this.add(topPanel);

    this.addRecordsTable(topPanel);
    this.addPageControls(topPanel);
    this.addFilterAndRefreshControls(topPanel);    
    this.addMiscButtons(topPanel);
  }
  
  private void addRecordsTable(JPanel panel) {
    this.recordsTable = new JTable(new DefaultTableModel() {
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
    this.recordsTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          LogDialog.this.openSelectedRecordDetail();
        }
      }
    });
    panel.add(new JScrollPane(this.recordsTable));
  }
  
  private void openSelectedRecordDetail() {
    int selectedIndex = this.recordsTable.getSelectedRow();
    if (selectedIndex != -1) {
      new LogRecordDialog(this, this.logReader.getCurrentPage().get(selectedIndex)).setVisible(true);
    }
  }
  
  private void addFilterAndRefreshControls(JPanel panel) {
    JPanel filterPanel = new JPanel();
    filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
    panel.add(filterPanel);

    /*this.addSeverityCheckBox("Info", Logger.INFO_SEVERITY, filterPanel);
    this.addSeverityCheckBox("Event Commit", Logger.INFO_EVENT_COMMIT_SEVERITY, filterPanel);
    this.addSeverityCheckBox("Warning", Logger.WARNING_SEVERITY, filterPanel);
    this.addSeverityCheckBox("Error", Logger.ERROR_SEVERITY, filterPanel);*/

    //'refresh' button
    addButton(filterPanel, "Refresh", "Reload the log", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        LogDialog.this.refresh();
      }
    });
  }

  /*private void addSeverityCheckBox(String title, final int severity, JPanel panel) {
    JCheckBox checkBox = new JCheckBox();
    checkBox.setSelected(severityEnableStates[severity]);
    panel.add(checkBox);
    
    JLabel titleLabel = new JLabel(title, getSeverityIcon(severity), SwingConstants.CENTER);
    panel.add(titleLabel);

    panel.add(Box.createRigidArea(new Dimension(15, 0)));

    checkBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LogDialog.this.severityEnableStates[severity] = ((JCheckBox)e.getSource()).isSelected();
      }
    });
  }*/
  
  private void addPageControls(JPanel panel) {
    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
    panel.add(buttonsPanel);
    
    //'previous page' button
    this.prevPageButton = addButton(buttonsPanel, "<< Prev Page ", "Shows the previous page", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        LogDialog.this.goToPreviousPage();
      }
    });
    
    //'next page' button
    this.nextPageButton = addButton(buttonsPanel, "Next Page >>", "Shows the next page", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {        
        LogDialog.this.goToNextPage();
      }
    });
  }
  
  private void addMiscButtons(JPanel panel) {
    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
    panel.add(buttonsPanel);

    //'delete all' button
    addButton(buttonsPanel, "Delete All", "Clear the log", true, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        LogDialog.this.deleteAllRecordsAndRefresh();
      }
    });

    //'close' button
    addButton(buttonsPanel, "Close", "Close the dialog", false, new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        LogDialog.this.setVisible(false);
      }
    });
  }

  private JButton addButton(JPanel panel, String text, String toolTipText, boolean addMargin, ActionListener actionListener) {
    JButton button = new JButton(text);
    button.setToolTipText(toolTipText);
    button.setAlignmentY(CENTER_ALIGNMENT);
    button.setAlignmentX(CENTER_ALIGNMENT);
    button.addActionListener(actionListener);

    panel.add(button);
    if (addMargin) {
      panel.add(Box.createRigidArea(new Dimension(5, 0)));
    }
    
    return button;
  }
  
  private ImageIcon getSeverityIcon(int severity) {
    switch (severity) {
      case Logger.WARNING_SEVERITY:
        return ResourcesHelper.getWarningSeverityIcon16();

      case Logger.ERROR_SEVERITY:
        return ResourcesHelper.getErrorSeverityIcon16();

      case Logger.INFO_EVENT_COMMIT_SEVERITY:
        return ResourcesHelper.getInfoEventCommitSeverityIcon16();
      
      default: //INFO_SEVERITY
        return ResourcesHelper.getInfoSeverityIcon16();
    }
  }
  
  private void deleteAllRecordsAndRefresh() {
    try {
      this.logger.clearRecords();
      this.refresh();
    } catch (SQLException ex) {
      MessageBox.showError(this, "Failed to clear the log.", ex, "Failed");
    }
  }   
  
  private void refresh() {
    this.closeLogReaderOrTrace();
    
    try {
      this.logReader = this.logger.getRecords();
      this.goToNextPage();
    } catch (SQLException ex) {
      showReadErrorAndClear(ex);
    }
  }
  
  private void goToNextPage() {
    try {
      this.logReader.nextPage();
      this.updateRecordsTable();
      this.updatePageButtons();
    } catch (IllegalStateException | SQLException ex) {
      this.showReadErrorAndClear(ex);
    }
  }
  
  private void goToPreviousPage() {
    try {
      this.logReader.previousPage();
      this.updateRecordsTable();
      this.updatePageButtons();
    } catch (IllegalStateException | SQLException ex) {
      this.showReadErrorAndClear(ex);
    }
  }
  
  private void showReadErrorAndClear(Throwable ex) {
    this.closeLogReaderOrTrace();
    this.updateRecordsTable();
    this.updatePageButtons();
    MessageBox.showError(this, "Failed to read the log.", ex, "Failed");
  }
  
  private void updatePageButtons() {
    nextPageButton.setEnabled(this.logReader != null && this.logReader.hasNextPage());
    prevPageButton.setEnabled(this.logReader != null && this.logReader.hasPreviousPage());
  }

  private void updateRecordsTable() {
    if(this.logReader == null) {
      ((DefaultTableModel)this.recordsTable.getModel()).getDataVector().clear();
    } else {
      //set data
      ArrayList<LogRecord> currentPage = this.logReader.getCurrentPage();

      Object[][] rows = new Object[currentPage.size()][3];
      for (int i = 0; i < currentPage.size(); i ++) {
        LogRecord record = currentPage.get(i);
        rows[i] = new Object[] {getSeverityIcon(record.getSeverity()), XmlGregorianCalendarHelper.toLocalString(record.getTimestamp()), record.getMessage()};
      }
      ((DefaultTableModel)this.recordsTable.getModel()).setDataVector(rows, new String[] {"", "Time", "Message"});

      //resize columns
      TableColumn severityColumn = this.recordsTable.getColumnModel().getColumn(0);
      severityColumn.setPreferredWidth(16);
      severityColumn.setMinWidth(0);
      severityColumn.setMaxWidth(16);

      TableColumn timeColumn = this.recordsTable.getColumnModel().getColumn(1);
      timeColumn.setPreferredWidth(150);
      timeColumn.setMinWidth(0);
      timeColumn.setMaxWidth(1000);
    }
  }
}