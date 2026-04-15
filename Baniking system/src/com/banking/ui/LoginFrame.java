package com.banking.ui;

import com.banking.models.User;
import com.banking.service.BankingService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private BankingService service;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public LoginFrame() {
        service = BankingService.getInstance();
        initComponents();
    }

    private void initComponents() {
        setTitle("Online Banking Management System");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("ONLINE BANKING SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Login As:"), gbc);

        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"CUSTOMER", "EMPLOYEE", "ADMIN"});
        formPanel.add(roleCombo, gbc);

        // Login Button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 153, 76));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> login());
        formPanel.add(loginButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("Default Credentials - admin/admin, Emp123/EmpPwd123", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        footerLabel.setForeground(Color.GRAY);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);

        // Enter key binding
        getRootPane().setDefaultButton(loginButton);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = service.login(username, password, role);

        if (user != null) {
            this.dispose();

            switch (role) {
                case "ADMIN":
                    new AdminFrame().setVisible(true);
                    break;
                case "EMPLOYEE":
                    new EmployeeFrame().setVisible(true);
                    break;
                case "CUSTOMER":
                    new CustomerFrame().setVisible(true);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid credentials or wrong role selected!",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}