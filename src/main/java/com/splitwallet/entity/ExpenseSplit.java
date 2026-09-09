package com.splitwallet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense_splits")
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long creatorId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ExpenseSplit() {}

    public ExpenseSplit(Long id, Long creatorId, String title, BigDecimal totalAmount,
                        String description, SplitStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.creatorId = creatorId;
        this.title = title;
        this.totalAmount = totalAmount;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = SplitStatus.ACTIVE;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SplitStatus getStatus() { return status; }
    public void setStatus(SplitStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static ExpenseSplitBuilder builder() { return new ExpenseSplitBuilder(); }

    public static class ExpenseSplitBuilder {
        private Long id;
        private Long creatorId;
        private String title;
        private BigDecimal totalAmount;
        private String description;
        private SplitStatus status;
        private LocalDateTime createdAt;

        public ExpenseSplitBuilder id(Long id) { this.id = id; return this; }
        public ExpenseSplitBuilder creatorId(Long creatorId) { this.creatorId = creatorId; return this; }
        public ExpenseSplitBuilder title(String title) { this.title = title; return this; }
        public ExpenseSplitBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public ExpenseSplitBuilder description(String description) { this.description = description; return this; }
        public ExpenseSplitBuilder status(SplitStatus status) { this.status = status; return this; }
        public ExpenseSplitBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ExpenseSplit build() {
            return new ExpenseSplit(id, creatorId, title, totalAmount, description, status, createdAt);
        }
    }
}
