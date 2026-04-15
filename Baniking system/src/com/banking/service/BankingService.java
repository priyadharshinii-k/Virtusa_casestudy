package com.banking.service;

import com.banking.models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BankingService {
    private static BankingService instance;

    private List<User> users;
    private List<Customer> customers;
    private List<Account> accounts;
    private List<Transaction> transactions;
    private List<AuditLog> auditLogs;

    private User loggedInUser;
    private int customerSequence = 1;
    private int auditSequence = 1;

    private BankingService() {
        users = new ArrayList<>();
        customers = new ArrayList<>();
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
        auditLogs = new ArrayList<>();
        seedAdminAndEmployee();
    }

    public static BankingService getInstance() {
        if (instance == null) {
            instance = new BankingService();
        }
        return instance;
    }

    private void seedAdminAndEmployee() {
        users.add(new User(1, "admin", "admin", "ADMIN", true));
        users.add(new User(2, "Emp123", "EmpPwd123", "EMPLOYEE", true));
    }

    // Login
    public User login(String username, String password, String expectedRole) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password) &&
                    user.getRole().equals(expectedRole) &&
                    user.isActive()) {
                loggedInUser = user;
                logAudit(user.getUserId(), "LOGIN", "users", String.valueOf(user.getUserId()));
                return user;
            }
        }
        return null;
    }

    // Register Customer
    public String registerCustomer(String username, String password, String firstName,
                                   String lastName, String email, String phone,
                                   String city, String country) {
        String customerId = "INB" + String.format("%03d", customerSequence);

        User user = new User();
        user.setUserId(users.size() + 1);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("CUSTOMER");
        user.setActive(true);

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setUserId(user.getUserId());
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setCity(city);
        customer.setCountry(country);

        users.add(user);
        customers.add(customer);
        customerSequence++;

        logAudit(user.getUserId(), "INSERT", "customers", customerId);
        return customerId;
    }

    // Edit Customer
    public boolean editCustomer(String customerId, String firstName, String lastName,
                                String email, String phone, String city, String country,
                                Boolean isVerified) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                if (firstName != null && !firstName.isEmpty()) customer.setFirstName(firstName);
                if (lastName != null && !lastName.isEmpty()) customer.setLastName(lastName);
                if (email != null && !email.isEmpty()) customer.setEmail(email);
                if (phone != null && !phone.isEmpty()) customer.setPhone(phone);
                if (city != null && !city.isEmpty()) customer.setCity(city);
                if (country != null && !country.isEmpty()) customer.setCountry(country);
                if (isVerified != null) customer.setVerified(isVerified);

                logAudit(customer.getUserId(), "UPDATE", "customers", customer.getCustomerId());
                return true;
            }
        }
        return false;
    }

    // Open Account
    public int openAccount(String customerId, String accountType) {
        int accountNumber = new Random().nextInt(90000000) + 10000000;

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setCustomerId(customerId);
        account.setBalance(0);
        account.setAccountType(accountType);
        account.setFrozen(false);

        accounts.add(account);
        logAudit(null, "INSERT", "accounts", String.valueOf(accountNumber));
        return accountNumber;
    }

    // Search Customers
    public List<Customer> searchCustomers(String name, String city, String status) {
        List<Customer> results = new ArrayList<>();

        for (Customer customer : customers) {
            boolean match = true;

            if (name != null && !name.isEmpty()) {
                String fullName = (customer.getFirstName() + " " + customer.getLastName()).toLowerCase();
                if (!fullName.contains(name.toLowerCase())) {
                    match = false;
                }
            }

            if (city != null && !city.isEmpty()) {
                if (customer.getCity() == null ||
                        !customer.getCity().toLowerCase().contains(city.toLowerCase())) {
                    match = false;
                }
            }

            if (status != null && !status.isEmpty()) {
                if (status.equals("V") && !customer.isVerified()) {
                    match = false;
                } else if (status.equals("U") && customer.isVerified()) {
                    match = false;
                }
            }

            if (match) {
                results.add(customer);
            }
        }

        logAudit(null, "SEARCH", "customers", "FILTER");
        return results;
    }

    // View Accounts
    public List<Account> getAccountsByUserId(int userId) {
        String customerId = getCustomerIdByUserId(userId);
        List<Account> userAccounts = new ArrayList<>();

        for (Account account : accounts) {
            if (account.getCustomerId().equals(customerId)) {
                userAccounts.add(account);
            }
        }
        return userAccounts;
    }

    // Deposit
    public boolean deposit(int accountNumber, double amount) {
        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber) {
                if (account.isFrozen()) {
                    return false;
                }
                account.setBalance(account.getBalance() + amount);
                transactions.add(new Transaction(accountNumber, accountNumber, amount, "DEPOSIT"));
                logAudit(loggedInUser.getUserId(), "DEPOSIT", "transactions", String.valueOf(accountNumber));
                return true;
            }
        }
        return false;
    }

    // Transfer
    public boolean transfer(int fromAccount, int toAccount, double amount) {
        Account sender = null, receiver = null;

        for (Account account : accounts) {
            if (account.getAccountNumber() == fromAccount) sender = account;
            if (account.getAccountNumber() == toAccount) receiver = account;
        }

        if (sender == null || receiver == null || sender.getBalance() < amount || sender.isFrozen()) {
            return false;
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);
        transactions.add(new Transaction(fromAccount, toAccount, amount, "TRANSFER"));
        logAudit(loggedInUser.getUserId(), "TRANSFER", "transactions", fromAccount + "->" + toAccount);
        return true;
    }

    // Toggle Freeze
    public boolean toggleFreeze(int accountNumber) {
        String customerId = getCustomerIdByUserId(loggedInUser.getUserId());

        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber &&
                    account.getCustomerId().equals(customerId)) {
                account.setFrozen(!account.isFrozen());
                logAudit(loggedInUser.getUserId(), "UPDATE", "accounts", String.valueOf(accountNumber));
                return true;
            }
        }
        return false;
    }

    // View Transactions
    public List<Transaction> getTransactionsByUserId(int userId) {
        String customerId = getCustomerIdByUserId(userId);
        List<Integer> accountNumbers = new ArrayList<>();

        for (Account account : accounts) {
            if (account.getCustomerId().equals(customerId)) {
                accountNumbers.add(account.getAccountNumber());
            }
        }

        List<Transaction> userTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (accountNumbers.contains(transaction.getFromAccount()) ||
                    accountNumbers.contains(transaction.getToAccount())) {
                userTransactions.add(transaction);
            }
        }
        return userTransactions;
    }

    // View Audit Logs
    public List<AuditLog> getAuditLogs() {
        return new ArrayList<>(auditLogs);
    }

    // Helper Methods
    private void logAudit(Integer userId, String action, String tableName, String recordId) {
        AuditLog log = new AuditLog();
        log.setLogId(auditSequence++);
        log.setUserId(userId);
        log.setAction(action);
        log.setTableName(tableName);
        log.setRecordId(recordId);
        log.setModifiedBy(loggedInUser != null ? loggedInUser.getUserId() : 0);
        auditLogs.add(log);
    }

    private String getCustomerIdByUserId(int userId) {
        for (Customer customer : customers) {
            if (customer.getUserId() == userId) {
                return customer.getCustomerId();
            }
        }
        return null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void logout() {
        loggedInUser = null;
    }

    public Customer getCustomerById(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }
}