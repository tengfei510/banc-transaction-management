package com.example.transaction.repository;

import com.example.transaction.exception.DuplicateTransactionException;
import com.example.transaction.exception.TransactionNotFoundException;
import com.example.transaction.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionRepository {
    private final Map<String, Transaction> transactions = new HashMap<>();

    public Transaction save(Transaction transaction) {
        if (transactions.values().stream().anyMatch(t -> t.getType() == transaction.getType() && t.getAmount() == transaction.getAmount())) {
            throw new DuplicateTransactionException("Duplicate transaction detected");
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    public Transaction findById(String id) {
        return transactions.get(id);
    }

    public void deleteById(String id) {
        if (!transactions.containsKey(id)) {
            throw new TransactionNotFoundException("Transaction not found with id: " + id);
        }
        transactions.remove(id);
    }

    public Transaction update(Transaction transaction) {
        if (!transactions.containsKey(transaction.getId())) {
            throw new TransactionNotFoundException("Transaction not found with id: " + transaction.getId());
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }
}    