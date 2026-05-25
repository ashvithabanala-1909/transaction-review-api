package com.mtx.transaction.validation;

import com.mtx.transaction.dto.TransactionRequest;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    /**
     * Validates the input TransactionRequest for required fields and valid data types.
     * Throws ValidationException if validation fails.
     */
    public void validateInput(TransactionRequest request) throws ValidationException {
        
        if (request == null) {
            throw new ValidationException("Request body cannot be null");
        }

        if (request.getAmount() == null) {
            throw new ValidationException("Field 'amount' is required");
        }

        if (request.getAmount() < 0) {
            throw new ValidationException("Field 'amount' cannot be negative");
        }

        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new ValidationException("Field 'category' is required and cannot be blank");
        }

        if (request.getCountry() == null || request.getCountry().trim().isEmpty()) {
            throw new ValidationException("Field 'country' is required and cannot be blank");
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
