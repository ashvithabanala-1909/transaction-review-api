package com.mtx.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TransactionResponse {
    
    @JsonProperty("flagged")
    private boolean flagged;
    
    @JsonProperty("reasons")
    private List<String> reasons;

    public TransactionResponse() {}

    public TransactionResponse(boolean flagged, List<String> reasons) {
        this.flagged = flagged;
        this.reasons = reasons;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }
}
