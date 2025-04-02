package com.example.banktransaction.integration;

import com.example.transaction.model.Transaction;
import com.example.transaction.model.Transaction.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateAndGetTransaction() {
        // 创建交易
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, 1000.0);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transaction> request = new HttpEntity<>(transaction, headers);

        ResponseEntity<Transaction> response = restTemplate.postForEntity(
                createURLWithPort("/api/transactions"), request, Transaction.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TransactionType.DEPOSIT, response.getBody().getType());
        assertEquals(1000.0, response.getBody().getAmount(), 0.001);

        // 获取交易列表
        ResponseEntity<Transaction[]> listResponse = restTemplate.getForEntity(
                createURLWithPort("/api/transactions?page=0&size=10"), Transaction[].class);

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<Transaction> transactions = Arrays.asList(listResponse.getBody());
        assertTrue(transactions.stream().anyMatch(t -> t.getType() == TransactionType.DEPOSIT && t.getAmount() == 1000.0));
    }

    @Test
    public void testDeleteTransaction() {
        // 创建交易
        Transaction transaction = new Transaction(TransactionType.WITHDRAWAL, 500.0);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transaction> request = new HttpEntity<>(transaction, headers);

        ResponseEntity<Transaction> createResponse = restTemplate.postForEntity(
                createURLWithPort("/api/transactions"), request, Transaction.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        String transactionId = createResponse.getBody().getId();

        // 删除交易
        restTemplate.delete(createURLWithPort("/api/transactions/" + transactionId));

        // 验证交易已删除
        ResponseEntity<Transaction> getResponse = restTemplate.getForEntity(
                createURLWithPort("/api/transactions/" + transactionId), Transaction.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    public void testUpdateTransaction() {
        // 创建交易
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, 1000.0);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transaction> request = new HttpEntity<>(transaction, headers);

        ResponseEntity<Transaction> createResponse = restTemplate.postForEntity(
                createURLWithPort("/api/transactions"), request, Transaction.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        String transactionId = createResponse.getBody().getId();

        // 更新交易
        Transaction updatedTransaction = new Transaction(TransactionType.WITHDRAWAL, 500.0);
        updatedTransaction.setId(transactionId);
        HttpEntity<Transaction> updateRequest = new HttpEntity<>(updatedTransaction, headers);

        ResponseEntity<Transaction> updateResponse = restTemplate.exchange(
                createURLWithPort("/api/transactions/" + transactionId),
                HttpMethod.PUT, updateRequest, Transaction.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals(TransactionType.WITHDRAWAL, updateResponse.getBody().getType());
        assertEquals(500.0, updateResponse.getBody().getAmount(), 0.001);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}    