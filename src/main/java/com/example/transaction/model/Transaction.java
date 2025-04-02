package com.example.transaction.model;

import java.io.Serializable;
import java.util.UUID;


public class Transaction  implements Serializable {
    private String id;
    private TransactionType type;
    private double amount;

    public Transaction(TransactionType type, double amount) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public static enum TransactionType {
        DEPOSIT, WITHDRAWAL
    }

}    