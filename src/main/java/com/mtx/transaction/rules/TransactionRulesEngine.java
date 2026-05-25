package com.mtx.transaction.rules;

import com.mtx.transaction.dto.TransactionRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionRulesEngine {

    // Rule 1: Amount threshold
    private static final double AMOUNT_THRESHOLD = 10000.0;

    // Rule 2: Blocked categories
    private static final Set<String> BLOCKED_CATEGORIES = new HashSet<>(Arrays.asList(
            "Gambling",
            "Crypto",
            "Weapons"
    ));

    // Rule 3: Allowed countries
    private static final Set<String> ALLOWED_COUNTRIES = new HashSet<>(Arrays.asList(
            "US",
            "CA",
            "GB",
            "AU"
    ));

    /**
     * Validates transaction amount, category, and country against configured rules.
     * Returns a list of violation reasons if any rule is breached.
     */
    public List<String> validateTransaction(TransactionRequest request) {
        List<String> violations = new ArrayList<>();

        // Rule 1: Check amount threshold
        if (request.getAmount() != null && request.getAmount() > AMOUNT_THRESHOLD) {
            violations.add(String.format("Amount exceeds limit of %.2f", AMOUNT_THRESHOLD));
        }

        // Rule 2: Check blocked categories
        if (request.getCategory() != null && BLOCKED_CATEGORIES.contains(request.getCategory())) {
            violations.add(String.format("Category '%s' is blocked", request.getCategory()));
        }

        // Rule 3: Check allowed countries
        if (request.getCountry() != null && !ALLOWED_COUNTRIES.contains(request.getCountry())) {
            violations.add(String.format("Country '%s' is not in allowed list", request.getCountry()));
        }

        return violations;
    }

    /**
     * Returns the list of all configured rules for reference.
     */
    public List<RuleDetail> getRules() {
        return Arrays.asList(
                new RuleDetail(
                        "Amount Threshold",
                        String.format("Flag if transaction amount exceeds %.2f", AMOUNT_THRESHOLD)
                ),
                new RuleDetail(
                        "Blocked Categories",
                        String.format("Flag if category is in blocked list: %s", String.join(", ", BLOCKED_CATEGORIES))
                ),
                new RuleDetail(
                        "Allowed Countries",
                        String.format("Flag if country is not in allowed list: %s", String.join(", ", ALLOWED_COUNTRIES))
                )
        );
    }

    /**
     * Inner class to represent a rule detail.
     */
    public static class RuleDetail {
        private String name;
        private String description;

        public RuleDetail(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
