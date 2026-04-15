package com.banking.ui;

import com.banking.models.Account;
import com.banking.models.Transaction;
import com.banking.service.BankingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerFrame extends JFrame {
    private BankingService service;

    public CustomerFrame() {
        service = BankingService.getInstance();
        initComponents();
    }

    private void initComponents() {
        setTitle("Customer Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("CUSTOMER DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 128, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton viewAccBtn = new JButton("View Accounts");
        viewAccBtn.setFont(new Font("Arial", Font.BOLD, 14));
        viewAccBtn.addActionListener(e -> viewAccounts());
        buttonPanel.add(viewAccBtn);

        JButton depositBtn = new JButton("Deposit Amount");
        depositBtn.setFont(new Font("Arial", Font.BOLD, 14));
        depositBtn.addActionListener(e -> depositAmount());
        buttonPanel.add(depositBtn);

        JButton transferBtn = new JButton("Transfer Funds");
        transferBtn.setFont(new Font("Arial", Font.BOLD, 14));
        transferBtn.addActionListener(e -> transferFunds());
        buttonPanel.add(transferBtn);

        JButton viewTxBtn = new JButton("View Transactions");
        viewTxBtn.setFont(new Font("Arial", Font.BOLD, 14));
        viewTxBtn.addActionListener(e -> viewTransactions());
        buttonPanel.add(viewTxBtn);

        JButton freezeBtn = new JButton("Freeze / Unfreeze Account");
        freezeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        freezeBtn.addActionListener(e -> toggleFreeze());
        buttonPanel.add(freezeBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        buttonPanel.add(logoutBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void viewAccounts() {
        List<Account> accounts = service.getAccountsByUserId(service.getLoggedInUser().getUserId());

        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No accounts found!");
            return;
        }

        JFrame frame = new JFrame("My Accounts");
        frame.setSize(700, 300);
        frame.setLocationRelativeTo(this);

        String[] columns = {"Account Number", "Type", "Balance", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Account acc : accounts) {
            model.addRow(new Object[]{
                    acc.getAccountNumber(),
                    acc.getAccountType(),
                    String.format("%.2f", acc.getBalance()),
                    acc.isFrozen() ? "Frozen" : "Active"
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }

    private void depositAmount() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        JTextField accField = new JTextField();
        JTextField amountField = new JTextField();

        panel.add(new JLabel("Account Number:"));
        panel.add(accField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Deposit Amount",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int accountNumber = Integer.parseInt(accField.getText());
                double amount = Double.parseDouble(amountField.getText());

                if (service.deposit(accountNumber, amount)) {
                    JOptionPane.showMessageDialog(this, "Deposit successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Deposit failed! Account may be frozen.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void transferFunds() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField fromField = new JTextField();
        JTextField toField = new JTextField();
        JTextField amountField = new JTextField();

        panel.add(new JLabel("From Account:"));
        panel.add(fromField);
        panel.add(new JLabel("To Account:"));
        panel.add(toField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Transfer Funds",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int fromAccount = Integer.parseInt(fromField.getText());
                int toAccount = Integer.parseInt(toField.getText());
                double amount = Double.parseDouble(amountField.getText());

                if (service.transfer(fromAccount, toAccount, amount)) {
                    JOptionPane.showMessageDialog(this, "Transfer successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Transfer failed! Check balance and account status.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewTransactions() {
        List<Transaction> transactions = service.getTransactionsByUserId(service.getLoggedInUser().getUserId());

        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No transactions found!");
            return;
        }

        JFrame frame = new JFrame("My Transactions");
        frame.setSize(700, 400);
        frame.setLocationRelativeTo(this);

        String[] columns = {"Type", "From Account", "To Account", "Amount", "Timestamp"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        for (Transaction tx : transactions) {
            model.addRow(new Object[]{
                    tx.getType(),
                    tx.getFromAccount(),
                    tx.getToAccount(),
                    String.format("%.2f", tx.getAmount()),
                    tx.getTimestamp().format(formatter)
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }

    private void toggleFreeze() {
        String input = JOptionPane.showInputDialog(this, "Enter Account Number:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int accountNumber = Integer.parseInt(input);

            if (service.toggleFreeze(accountNumber)) {
                JOptionPane.showMessageDialog(this, "Account freeze status toggled successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Account not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid account number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        service.logout();
        this.dispose();
        new LoginFrame().setVisible(true);
    }
}