package com.splitwallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ParticipantShareRequest {

    @NotNull(message = "Participant userId is required")
    private Long userId;

    @NotNull(message = "Share amount is required")
    @DecimalMin(value = "0.01", message = "Share amount must be greater than 0")
    private BigDecimal amount;

    // No-args constructor
    public ParticipantShareRequest() {
    }

    // All-args constructor
    public ParticipantShareRequest(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId;
        private BigDecimal amount;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ParticipantShareRequest build() {
            return new ParticipantShareRequest(userId, amount);
        }
    }
}
