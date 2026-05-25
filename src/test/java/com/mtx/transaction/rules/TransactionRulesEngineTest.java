package com.mtx.transaction.rules;

import com.mtx.transaction.dto.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionRulesEngineTest {

    private TransactionRulesEngine rulesEngine;

    @BeforeEach
    public void setUp() {
        rulesEngine = new TransactionRulesEngine();
    }

    /**
     * Test: Transaction passes all rules
     */
    @Test
    public void testTransactionPassesAllRules() {
        TransactionRequest request = new TransactionRequest(5000.0, "Electronics", "US");
        List<String> violations = rulesEngine.validateTransaction(request);
        assertTrue(violations.isEmpty(), "Valid transaction should have no violations");
    }

    /**
     * Test: Amount rule violation
     */
    @Test
    public void testAmountRuleViolation() {
        TransactionRequest request = new TransactionRequest(15000.0, "Electronics", "US");
        List<String> violations = rulesEngine.validateTransaction(request);
        assertFalse(violations.isEmpty(), "Should have violations");
        assertTrue(violations.stream().anyMatch(r -> r.contains("exceeds limit")), 
                "Should contain amount limit violation");
    }

    /**
     * Test: Category rule violation
     */
    @Test
    public void testCategoryRuleViolation() {
        TransactionRequest request = new TransactionRequest(5000.0, "Crypto", "US");
        List<String> violations = rulesEngine.validateTransaction(request);
        assertFalse(violations.isEmpty(), "Should have violations");
        assertTrue(violations.stream().anyMatch(r -> r.contains("blocked")), 
                "Should contain category violation");
    }

    /**
     * Test: Country rule violation
     */
    @Test
    public void testCountryRuleViolation() {
        TransactionRequest request = new TransactionRequest(5000.0, "Electronics", "JP");
        List<String> violations = rulesEngine.validateTransaction(request);
        assertFalse(violations.isEmpty(), "Should have violations");
        assertTrue(violations.stream().anyMatch(r -> r.contains("not in allowed list")), 
                "Should contain country violation");
    }

    /**
     * Test: Multiple violations
     */
    @Test
    public void testMultipleViolations() {
        TransactionRequest request = new TransactionRequest(20000.0, "Weapons", "RU");
        List<String> violations = rulesEngine.validateTransaction(request);
        assertEquals(3, violations.size(), "Should have 3 violations");
    }

    /**
     * Test: Get rules returns all three rules
     */
    @Test
    public void testGetRules() {
        List<TransactionRulesEngine.RuleDetail> rules = rulesEngine.getRules();
        assertEquals(3, rules.size(), "Should have 3 rules");
        assertTrue(rules.stream().anyMatch(r -> r.getName().equals("Amount Threshold")));
        assertTrue(rules.stream().anyMatch(r -> r.getName().equals("Blocked Categories")));
        assertTrue(rules.stream().anyMatch(r -> r.getName().equals("Allowed Countries")));
    }
}
