package com.banking.models;

import java.time.LocalDateTime;

public class Transaction {
    private int fromAccount;
    private int toAccount;
    private double amount;
    private String type;
    private LocalDateTime timestamp;

    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(int fromAccount, int toAccount, double amount, String type) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public int getFromAccount() { return fromAccount; }
    public void setFromAccount(int fromAccount) { this.fromAccount = fromAccount; }
    public int getToAccount() { return toAccount; }
    public void setToAccount(int toAccount) { this.toAccount = toAccount; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}