package com.example.banktransaction.controller;

import com.example.transaction.controller.TransactionController;
import com.example.transaction.exception.DuplicateTransactionException;
import com.example.transaction.exception.TransactionNotFoundException;
import com.example.transaction.model.Transaction;
import com.example.transaction.model.Transaction.TransactionType;
import com.example.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest(classes = BankTransactionApplication.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    public void testCreateTransaction() throws Exception {
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, 1000.0);
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"DEPOSIT\", \"amount\": 1000.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(1000.0));
    }

    @Test
    public void testCreateDuplicateTransaction() throws Exception {
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, 1000.0);
        when(transactionService.createTransaction(any(Transaction.class))).thenThrow(new DuplicateTransactionException("Duplicate transaction detected"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"DEPOSIT\", \"amount\": 1000.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllTransactions() throws Exception {
        Transaction transaction1 = new Transaction(TransactionType.DEPOSIT, 1000.0);
        Transaction transaction2 = new Transaction(TransactionType.WITHDRAWAL, 500.0);
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionService.getAllTransactions(anyInt(), anyInt())).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"));
    }

    @Test
    public void testGetTransactionById() throws Exception {
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, 1000.0);
        when(transactionService.getTransactionById(anyString())).thenReturn(transaction);

        mockMvc.perform(get("/api/transactions/{id}", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(1000.0));
    }

    @Test
    public void testGetTransactionByIdNotFound() throws Exception {
        when(transactionService.getTransactionById(anyString())).thenThrow(new TransactionNotFoundException("Transaction not found with id: 123"));

        mockMvc.perform(get("/api/transactions/{id}", "123"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        doNothing().when(transactionService).deleteTransaction(anyString());

        mockMvc.perform(delete("/api/transactions/{id}", "123"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteTransactionNotFound() throws Exception {
        doThrow(new TransactionNotFoundException("Transaction not found with id: 123")).when(transactionService).deleteTransaction(anyString());

        mockMvc.perform(delete("/api/transactions/{id}", "123"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateTransaction() throws Exception {
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, 1000.0);
        when(transactionService.updateTransaction(any(Transaction.class))).thenReturn(transaction);

        mockMvc.perform(put("/api/transactions/{id}", transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"DEPOSIT\", \"amount\": 1000.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(1000.0));
    }

    @Test
    public void testUpdateTransactionNotFound() throws Exception {
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, 1000.0);
        when(transactionService.updateTransaction(any(Transaction.class))).thenThrow(new TransactionNotFoundException("Transaction not found with id: " + transaction.getId()));

        mockMvc.perform(put("/api/transactions/{id}", transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"DEPOSIT\", \"amount\": 1000.0}"))
                .andExpect(status().isNotFound());
    }
}    