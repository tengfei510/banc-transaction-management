package com.example.transaction.service;

import com.example.transaction.exception.TransactionNotFoundException;
import com.example.transaction.model.Transaction;
import com.example.transaction.repository.TransactionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @CacheEvict(value = "transactions", allEntries = true)
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Cacheable(value = "transactions")
    public List<Transaction> getAllTransactions(int page, int size) {
        List<Transaction> allTransactions = transactionRepository.findAll();
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allTransactions.size());
        if (startIndex >= allTransactions.size()) {
            return new ArrayList<>();
        }
        return allTransactions.subList(startIndex, endIndex);
    }

    @Cacheable(value = "transactions", key = "#id")
    public Transaction getTransactionById(String id) {
        Transaction transaction = transactionRepository.findById(id);
        if (transaction == null) {
            throw new TransactionNotFoundException("Transaction not found with id: " + id);
        }
        return transaction;
    }

    @CacheEvict(value = "transactions", key = "#id")
    public void deleteTransaction(String id) {
        transactionRepository.deleteById(id);
    }

    @CacheEvict(value = "transactions", key = "#transaction.id")
    public Transaction updateTransaction(Transaction transaction) {
        return transactionRepository.update(transaction);
    }
}    