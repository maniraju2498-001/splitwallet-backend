package com.splitwallet.dto.response;

import com.splitwallet.entity.ApprovalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SplitParticipantResponse {
    private Long id;
    private Long splitId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String splitTitle;
    private String splitDescription;
    private String creatorName;
    private BigDecimal amount;
    private ApprovalStatus approvalStatus;
    private Boolean debited;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;

    public SplitParticipantResponse() {}

    // Getters
    public Long getId() { return id; }
    public Long getSplitId() { return splitId; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getSplitTitle() { return splitTitle; }
    public String getSplitDescription() { return splitDescription; }
    public String getCreatorName() { return creatorName; }
    public BigDecimal getAmount() { return amount; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public Boolean getDebited() { return debited; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setSplitId(Long splitId) { this.splitId = splitId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setSplitTitle(String splitTitle) { this.splitTitle = splitTitle; }
    public void setSplitDescription(String splitDescription) { this.splitDescription = splitDescription; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public void setDebited(Boolean debited) { this.debited = debited; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long splitId;
        private Long userId;
        private String userName;
        private String userEmail;
        private String splitTitle;
        private String splitDescription;
        private String creatorName;
        private BigDecimal amount;
        private ApprovalStatus approvalStatus;
        private Boolean debited;
        private LocalDateTime approvedAt;
        private LocalDateTime rejectedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder splitId(Long splitId) { this.splitId = splitId; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder userName(String userName) { this.userName = userName; return this; }
        public Builder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public Builder splitTitle(String splitTitle) { this.splitTitle = splitTitle; return this; }
        public Builder splitDescription(String splitDescription) { this.splitDescription = splitDescription; return this; }
        public Builder creatorName(String creatorName) { this.creatorName = creatorName; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder approvalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; return this; }
        public Builder debited(Boolean debited) { this.debited = debited; return this; }
        public Builder approvedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; return this; }
        public Builder rejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; return this; }

        public SplitParticipantResponse build() {
            SplitParticipantResponse r = new SplitParticipantResponse();
            r.id = this.id; r.splitId = this.splitId; r.userId = this.userId;
            r.userName = this.userName; r.userEmail = this.userEmail;
            r.splitTitle = this.splitTitle; r.splitDescription = this.splitDescription;
            r.creatorName = this.creatorName; r.amount = this.amount;
            r.approvalStatus = this.approvalStatus; r.debited = this.debited;
            r.approvedAt = this.approvedAt; r.rejectedAt = this.rejectedAt;
            return r;
        }
    }
}
