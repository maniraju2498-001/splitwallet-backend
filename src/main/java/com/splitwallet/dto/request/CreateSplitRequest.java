package com.splitwallet.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class CreateSplitRequest {

    @NotBlank(message = "Expense title is required")
    private String title;

    @NotNull(message = "Total expense amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    private String description;

    @NotEmpty(message = "At least one participant share must be provided")
    @Valid
    private List<ParticipantShareRequest> participants;

    // No-args constructor
    public CreateSplitRequest() {
    }

    // All-args constructor
    public CreateSplitRequest(String title, BigDecimal totalAmount, String description,
                              List<ParticipantShareRequest> participants) {
        this.title = title;
        this.totalAmount = totalAmount;
        this.description = description;
        this.participants = participants;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getDescription() {
        return description;
    }

    public List<ParticipantShareRequest> getParticipants() {
        return participants;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParticipants(List<ParticipantShareRequest> participants) {
        this.participants = participants;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private BigDecimal totalAmount;
        private String description;
        private List<ParticipantShareRequest> participants;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder participants(List<ParticipantShareRequest> participants) {
            this.participants = participants;
            return this;
        }

        public CreateSplitRequest build() {
            return new CreateSplitRequest(title, totalAmount, description, participants);
        }
    }
}
