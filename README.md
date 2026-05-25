# Transaction Review API

## Overview
A Spring Boot REST API that evaluates financial transactions against business rules and flags them for review. The API accepts transaction details (amount, category, country) and returns whether the transaction should be flagged based on configured business rules.

## Rules

1. **Amount Threshold** - Flag if transaction amount exceeds $10,000
2. **Blocked Categories** - Flag if category is in the blocked list (Gambling, Crypto, Weapons)
3. **Allowed Countries** - Flag if country is not in the allowed list (US, CA, GB, AU)

## How to Run

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Steps to Start the Application

1. Navigate to the project directory:
```bash
cd transaction-review-api
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

## How to Run Tests

```bash
mvn test
```

## Sample Requests

### Valid Transaction (NOT Flagged)

**Request:**
```bash
curl -X POST http://localhost:8080/api/transactions/review \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000.00,
    "category": "Electronics",
    "country": "US"
  }'
```

**Response:**
```json
{
  "flagged": false,
  "reasons": []
}
```

### Flagged Transaction

**Request:**
```bash
curl -X POST http://localhost:8080/api/transactions/review \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 12500.00,
    "category": "Electronics",
    "country": "US"
  }'
```

**Response:**
```json
{
  "flagged": true,
  "reasons": [
    "Amount exceeds limit of 10000.00"
  ]
}
```

## Known Limitations

- No database or persistent storage
- Rules are hard-coded in Java
- No authentication or authorization
- No logging implementation
- No API rate limiting
- Stateless (each request is independent)

## Hours Spent

Approximately 2.5 hours
