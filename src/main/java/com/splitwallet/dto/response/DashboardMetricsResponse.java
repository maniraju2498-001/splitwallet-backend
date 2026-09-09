package com.splitwallet.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class DashboardMetricsResponse {
    private BigDecimal currentBalance;
    private BigDecimal totalMoneyAdded;
    private BigDecimal totalSplitAmount;
    private BigDecimal totalAmountDebited;
    private long pendingRequestsCount;
    private List<TransactionResponse> recentTransactions;
    private List<SplitResponse> recentSplits;

    public DashboardMetricsResponse() {}

    // Getters
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public BigDecimal getTotalMoneyAdded() { return totalMoneyAdded; }
    public BigDecimal getTotalSplitAmount() { return totalSplitAmount; }
    public BigDecimal getTotalAmountDebited() { return totalAmountDebited; }
    public long getPendingRequestsCount() { return pendingRequestsCount; }
    public List<TransactionResponse> getRecentTransactions() { return recentTransactions; }
    public List<SplitResponse> getRecentSplits() { return recentSplits; }

    // Setters
    public void setCurrentBalance(BigDecimal v) { this.currentBalance = v; }
    public void setTotalMoneyAdded(BigDecimal v) { this.totalMoneyAdded = v; }
    public void setTotalSplitAmount(BigDecimal v) { this.totalSplitAmount = v; }
    public void setTotalAmountDebited(BigDecimal v) { this.totalAmountDebited = v; }
    public void setPendingRequestsCount(long v) { this.pendingRequestsCount = v; }
    public void setRecentTransactions(List<TransactionResponse> v) { this.recentTransactions = v; }
    public void setRecentSplits(List<SplitResponse> v) { this.recentSplits = v; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BigDecimal currentBalance;
        private BigDecimal totalMoneyAdded;
        private BigDecimal totalSplitAmount;
        private BigDecimal totalAmountDebited;
        private long pendingRequestsCount;
        private List<TransactionResponse> recentTransactions;
        private List<SplitResponse> recentSplits;

        public Builder currentBalance(BigDecimal v) { this.currentBalance = v; return this; }
        public Builder totalMoneyAdded(BigDecimal v) { this.totalMoneyAdded = v; return this; }
        public Builder totalSplitAmount(BigDecimal v) { this.totalSplitAmount = v; return this; }
        public Builder totalAmountDebited(BigDecimal v) { this.totalAmountDebited = v; return this; }
        public Builder pendingRequestsCount(long v) { this.pendingRequestsCount = v; return this; }
        public Builder recentTransactions(List<TransactionResponse> v) { this.recentTransactions = v; return this; }
        public Builder recentSplits(List<SplitResponse> v) { this.recentSplits = v; return this; }

        public DashboardMetricsResponse build() {
            DashboardMetricsResponse r = new DashboardMetricsResponse();
            r.currentBalance = this.currentBalance;
            r.totalMoneyAdded = this.totalMoneyAdded;
            r.totalSplitAmount = this.totalSplitAmount;
            r.totalAmountDebited = this.totalAmountDebited;
            r.pendingRequestsCount = this.pendingRequestsCount;
            r.recentTransactions = this.recentTransactions;
            r.recentSplits = this.recentSplits;
            return r;
        }
    }
}
