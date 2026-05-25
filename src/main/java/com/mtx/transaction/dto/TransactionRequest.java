package com.mtx.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionRequest {
    
    @JsonProperty("amount")
    private Double amount;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("country")
    private String country;

    public TransactionRequest() {}

    public TransactionRequest(Double amount, String category, String country) {
        this.amount = amount;
        this.category = category;
        this.country = country;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
