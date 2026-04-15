package com.banking.ui;

import com.banking.models.AuditLog;
import com.banking.service.BankingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminFrame extends JFrame {
    private BankingService service;
    private JTable auditTable;
    private DefaultTableModel tableModel;

    public AdminFrame() {
        service = BankingService.getInstance();
        initComponents();
        loadAuditLogs();
    }

    private void initComponents() {
        setTitle("Admin Panel");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("ADMIN - AUDIT LOGS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(204, 0, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Log ID", "Timestamp", "Action", "Table", "Record ID", "User ID", "Modified By"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        auditTable = new JTable(tableModel);
        auditTable.setRowHeight(25);
        auditTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(auditTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAuditLogs());
        bottomPanel.add(refreshButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        bottomPanel.add(logoutButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadAuditLogs() {
        tableModel.setRowCount(0);
        List<AuditLog> logs = service.getAuditLogs();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        for (AuditLog log : logs) {
            tableModel.addRow(new Object[]{
                    log.getLogId(),
                    log.getTimestamp().format(formatter),
                    log.getAction(),
                    log.getTableName(),
                    log.getRecordId(),
                    log.getUserId() != null ? log.getUserId() : "-",
                    log.getModifiedBy()
            });
        }
    }

    private void logout() {
        service.logout();
        this.dispose();
        new LoginFrame().setVisible(true);
    }
}