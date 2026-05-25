package com.mtx.transaction.controller;

import com.mtx.transaction.dto.TransactionRequest;
import com.mtx.transaction.dto.TransactionResponse;
import com.mtx.transaction.rules.TransactionRulesEngine;
import com.mtx.transaction.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionRulesEngine rulesEngine;

    @Autowired
    private ValidationService validationService;

    /**
     * POST /api/transactions/review
     * Reviews a transaction against business rules and returns whether it should be flagged.
     *
     * @param request TransactionRequest with amount, category, and country
     * @return TransactionResponse with flagged status and list of violation reasons
     */
    @PostMapping("/review")
    public ResponseEntity<?> reviewTransaction(@RequestBody TransactionRequest request) {
        try {
            // Validate input
            validationService.validateInput(request);

            // Check against business rules
            List<String> violations = rulesEngine.validateTransaction(request);

            // Build response
            boolean isFlagged = !violations.isEmpty();
            TransactionResponse response = new TransactionResponse(isFlagged, violations);

            return ResponseEntity.ok(response);

        } catch (ValidationService.ValidationException e) {
            // Return 400 Bad Request for validation errors
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * GET /api/rules
     * Returns the list of all transaction review rules.
     *
     * @return List of rule details
     */
    @GetMapping("/rules")
    public ResponseEntity<?> getRules() {
        List<TransactionRulesEngine.RuleDetail> rules = rulesEngine.getRules();
        Map<String, Object> response = new HashMap<>();
        response.put("rules", rules);
        response.put("count", rules.size());
        return ResponseEntity.ok(response);
    }
}
