package com.splitwallet.dto.response;

import com.splitwallet.entity.SplitStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SplitResponse {
    private Long id;
    private Long creatorId;
    private String creatorName;
    private String title;
    private BigDecimal totalAmount;
    private String description;
    private SplitStatus status;
    private LocalDateTime createdAt;
    private List<SplitParticipantResponse> participants;

    // No-args constructor
    public SplitResponse() {
    }

    // All-args constructor
    public SplitResponse(Long id, Long creatorId, String creatorName, String title,
                         BigDecimal totalAmount, String description, SplitStatus status,
                         LocalDateTime createdAt, List<SplitParticipantResponse> participants) {
        this.id = id;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.title = title;
        this.totalAmount = totalAmount;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.participants = participants;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getDescription() {
        return description;
    }

    public SplitStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<SplitParticipantResponse> getParticipants() {
        return participants;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(SplitStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setParticipants(List<SplitParticipantResponse> participants) {
        this.participants = participants;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long creatorId;
        private String creatorName;
        private String title;
        private BigDecimal totalAmount;
        private String description;
        private SplitStatus status;
        private LocalDateTime createdAt;
        private List<SplitParticipantResponse> participants;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder creatorId(Long creatorId) {
            this.creatorId = creatorId;
            return this;
        }

        public Builder creatorName(String creatorName) {
            this.creatorName = creatorName;
            return this;
        }

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

        public Builder status(SplitStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder participants(List<SplitParticipantResponse> participants) {
            this.participants = participants;
            return this;
        }

        public SplitResponse build() {
            return new SplitResponse(id, creatorId, creatorName, title, totalAmount,
                    description, status, createdAt, participants);
        }
    }
}
