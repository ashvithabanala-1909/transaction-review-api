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
public class TransactionController {

    @Autowired
    private TransactionRulesEngine rulesEngine;

    @Autowired
    private ValidationService validationService;

    @PostMapping("/api/transactions/review")
    public ResponseEntity reviewTransaction(@RequestBody TransactionRequest request) {
        try {
            validationService.validateInput(request);
            List violations = rulesEngine.validateTransaction(request);
            boolean isFlagged = !violations.isEmpty();
            TransactionResponse response = new TransactionResponse(isFlagged, violations);
            return ResponseEntity.ok(response);
        } catch (ValidationService.ValidationException e) {
            Map errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/api/rules")
    public ResponseEntity getRules() {
        List rules = rulesEngine.getRules();
        Map response = new HashMap<>();
        response.put("rules", rules);
        response.put("count", rules.size());
        return ResponseEntity.ok(response);
    }
}
