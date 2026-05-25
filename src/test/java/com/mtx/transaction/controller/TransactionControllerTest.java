package com.mtx.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtx.transaction.dto.TransactionRequest;
import com.mtx.transaction.dto.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test 1: Valid request that is NOT flagged
     * Transaction with amount < 10000, allowed category, and allowed country should not be flagged.
     */
    @Test
    public void testValidTransactionNotFlagged() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest(5000.0, "Electronics", "US");

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/transactions/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        TransactionResponse transactionResponse = objectMapper.readValue(response, TransactionResponse.class);

        assertFalse(transactionResponse.isFlagged(), "Transaction should not be flagged");
        assertTrue(transactionResponse.getReasons().isEmpty(), "Reasons list should be empty for valid transaction");
    }

    /**
     * Test 2: Valid request that IS flagged (amount exceeds limit)
     * Transaction with amount > 10000 should be flagged.
     */
    @Test
    public void testTransactionFlaggedByAmountLimit() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest(15000.0, "Electronics", "US");

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/transactions/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        TransactionResponse transactionResponse = objectMapper.readValue(response, TransactionResponse.class);

        assertTrue(transactionResponse.isFlagged(), "Transaction should be flagged for amount > 10000");
        assertFalse(transactionResponse.getReasons().isEmpty(), "Reasons should not be empty");
        assertTrue(transactionResponse.getReasons().stream()
                .anyMatch(r -> r.contains("exceeds limit")), "Should contain amount limit violation");
    }

    /**
     * Test 3: Transaction flagged by blocked category
     * Transaction with blocked category (Gambling) should be flagged.
     */
    @Test
    public void testTransactionFlaggedByBlockedCategory() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest(5000.0, "Gambling", "US");

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/transactions/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        TransactionResponse transactionResponse = objectMapper.readValue(response, TransactionResponse.class);

        assertTrue(transactionResponse.isFlagged(), "Transaction should be flagged for blocked category");
        assertTrue(transactionResponse.getReasons().stream()
                .anyMatch(r -> r.contains("blocked")), "Should contain category violation");
    }

    /**
     * Test 4: Transaction flagged by disallowed country
     * Transaction from non-allowed country should be flagged.
     */
    @Test
    public void testTransactionFlaggedByDisallowedCountry() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest(5000.0, "Electronics", "CN");

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/transactions/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        TransactionResponse transactionResponse = objectMapper.readValue(response, TransactionResponse.class);

        assertTrue(transactionResponse.isFlagged(), "Transaction should be flagged for disallowed country");
        assertTrue(transactionResponse.getReasons().stream()
                .anyMatch(r -> r.contains("not in allowed list")), "Should contain country violation");
    }

    /**
     * Test 5: Multiple rule violations
     * Transaction that violates multiple rules should list all reasons.
     */
    @Test
    public void testMultipleRuleViolations() throws Exception {
        // Arrange - amount exceeds limit AND category is blocked
        TransactionRequest request = new TransactionRequest(15000.0, "Crypto", "CN");

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/transactions/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        TransactionResponse transactionResponse = objectMapper.readValue(response, TransactionResponse.class);

        assertTrue(transactionResponse.isFlagged(), "Transaction should be flagged");
        assertEquals(3, transactionResponse.getReasons().size(), "Should have 3 violations (amount, category, country)");
    }

    /**
     * Test 6: Invalid request - missing amount (400 Bad Request)
     */
    @Test
    public void testInvalidRequestMissingAmount() throws Exception {
        // Arrange
        String invalidJson = "{\"category\": \"Electronics\", \"country\": \"US\"}";

        // Act & Assert
        mockMvc.perform(post("/api/transactions/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Test 7: Invalid request - negative amount (400 Bad Request)
     */
    @Test
    public void testInvalidRequestNegativeAmount() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest(-1000.0, "Electronics", "US");

        // Act & Assert
        mockMvc.perform(post("/api/transactions/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Test 8: Invalid request - blank category (400 Bad Request)
     */
    @Test
    public void testInvalidRequestBlankCategory() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest(5000.0, "", "US");

        // Act & Assert
        mockMvc.perform(post("/api/transactions/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Test 9: GET /api/rules endpoint
     */
    @Test
    public void testGetRulesEndpoint() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/rules")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("rules"), "Response should contain rules");
        assertTrue(response.contains("Amount Threshold"), "Rules should include Amount Threshold");
        assertTrue(response.contains("Blocked Categories"), "Rules should include Blocked Categories");
        assertTrue(response.contains("Allowed Countries"), "Rules should include Allowed Countries");
    }
}
