package com.banking.ui;

import com.banking.models.Customer;
import com.banking.service.BankingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeFrame extends JFrame {
    private BankingService service;

    public EmployeeFrame() {
        service = BankingService.getInstance();
        initComponents();
    }

    private void initComponents() {
        setTitle("Employee Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("EMPLOYEE DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 153));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton registerBtn = new JButton("Register Customer");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.addActionListener(e -> registerCustomer());
        buttonPanel.add(registerBtn);

        JButton editBtn = new JButton("Edit Customer");
        editBtn.setFont(new Font("Arial", Font.BOLD, 14));
        editBtn.addActionListener(e -> editCustomer());
        buttonPanel.add(editBtn);

        JButton openAccBtn = new JButton("Open Account");
        openAccBtn.setFont(new Font("Arial", Font.BOLD, 14));
        openAccBtn.addActionListener(e -> openAccount());
        buttonPanel.add(openAccBtn);

        JButton searchBtn = new JButton("Search Customers");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 14));
        searchBtn.addActionListener(e -> searchCustomers());
        buttonPanel.add(searchBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        buttonPanel.add(logoutBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void registerCustomer() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField countryField = new JTextField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);
        panel.add(new JLabel("Country:"));
        panel.add(countryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register Customer",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String customerId = service.registerCustomer(
                    usernameField.getText(),
                    new String(passwordField.getPassword()),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    cityField.getText(),
                    countryField.getText()
            );

            JOptionPane.showMessageDialog(this, "Customer registered successfully!\nCustomer ID: " + customerId);
        }
    }

    private void editCustomer() {
        String customerId = JOptionPane.showInputDialog(this, "Enter Customer ID:");
        if (customerId == null || customerId.trim().isEmpty()) return;

        Customer customer = service.getCustomerById(customerId);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));

        JTextField firstNameField = new JTextField(customer.getFirstName());
        JTextField lastNameField = new JTextField(customer.getLastName());
        JTextField emailField = new JTextField(customer.getEmail());
        JTextField phoneField = new JTextField(customer.getPhone());
        JTextField cityField = new JTextField(customer.getCity());
        JTextField countryField = new JTextField(customer.getCountry());
        JCheckBox verifiedCheck = new JCheckBox("", customer.isVerified());

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);
        panel.add(new JLabel("Country:"));
        panel.add(countryField);
        panel.add(new JLabel("Verified:"));
        panel.add(verifiedCheck);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Customer",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            service.editCustomer(customerId, firstNameField.getText(), lastNameField.getText(),
                    emailField.getText(), phoneField.getText(), cityField.getText(),
                    countryField.getText(), verifiedCheck.isSelected());
            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
        }
    }

    private void openAccount() {
        String customerId = JOptionPane.showInputDialog(this, "Enter Customer ID:");
        if (customerId == null || customerId.trim().isEmpty()) return;

        String[] types = {"SAVINGS", "CURRENT", "FD"};
        String accountType = (String) JOptionPane.showInputDialog(this, "Select Account Type:",
                "Open Account", JOptionPane.QUESTION_MESSAGE,
                null, types, types[0]);

        if (accountType != null) {
            int accountNumber = service.openAccount(customerId, accountType);
            JOptionPane.showMessageDialog(this, "Account created successfully!\nAccount Number: " + accountNumber);
        }
    }

    private void searchCustomers() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField cityField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"All", "Verified", "Unverified"});

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Search Customers",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String status = statusCombo.getSelectedItem().equals("Verified") ? "V" :
                    statusCombo.getSelectedItem().equals("Unverified") ? "U" : null;

            List<Customer> customers = service.searchCustomers(nameField.getText(),
                    cityField.getText(), status);

            showSearchResults(customers);
        }
    }

    private void showSearchResults(List<Customer> customers) {
        JFrame frame = new JFrame("Search Results");
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(this);

        String[] columns = {"Customer ID", "Name", "Email", "Phone", "City", "Country", "Verified"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Customer c : customers) {
            model.addRow(new Object[]{
                    c.getCustomerId(),
                    c.getFirstName() + " " + c.getLastName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getCity(),
                    c.getCountry(),
                    c.isVerified() ? "Yes" : "No"
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }

    private void logout() {
        service.logout();
        this.dispose();
        new LoginFrame().setVisible(true);
    }
}