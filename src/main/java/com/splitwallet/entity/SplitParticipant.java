package com.splitwallet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "split_participants")
public class SplitParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long splitId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus;

    @Column(nullable = false)
    private Boolean debited;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    public SplitParticipant() {}

    public SplitParticipant(Long id, Long splitId, Long userId, BigDecimal amount,
                             ApprovalStatus approvalStatus, Boolean debited,
                             LocalDateTime approvedAt, LocalDateTime rejectedAt) {
        this.id = id;
        this.splitId = splitId;
        this.userId = userId;
        this.amount = amount;
        this.approvalStatus = approvalStatus;
        this.debited = debited;
        this.approvedAt = approvedAt;
        this.rejectedAt = rejectedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.approvalStatus == null) {
            this.approvalStatus = ApprovalStatus.PENDING;
        }
        if (this.debited == null) {
            this.debited = false;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSplitId() { return splitId; }
    public void setSplitId(Long splitId) { this.splitId = splitId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public Boolean getDebited() { return debited; }
    public void setDebited(Boolean debited) { this.debited = debited; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }

    // Builder
    public static SplitParticipantBuilder builder() { return new SplitParticipantBuilder(); }

    public static class SplitParticipantBuilder {
        private Long id;
        private Long splitId;
        private Long userId;
        private BigDecimal amount;
        private ApprovalStatus approvalStatus;
        private Boolean debited;
        private LocalDateTime approvedAt;
        private LocalDateTime rejectedAt;

        public SplitParticipantBuilder id(Long id) { this.id = id; return this; }
        public SplitParticipantBuilder splitId(Long splitId) { this.splitId = splitId; return this; }
        public SplitParticipantBuilder userId(Long userId) { this.userId = userId; return this; }
        public SplitParticipantBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public SplitParticipantBuilder approvalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; return this; }
        public SplitParticipantBuilder debited(Boolean debited) { this.debited = debited; return this; }
        public SplitParticipantBuilder approvedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; return this; }
        public SplitParticipantBuilder rejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; return this; }

        public SplitParticipant build() {
            return new SplitParticipant(id, splitId, userId, amount, approvalStatus, debited, approvedAt, rejectedAt);
        }
    }
}
