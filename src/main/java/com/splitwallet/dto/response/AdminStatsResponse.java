package com.splitwallet.dto.response;

public class AdminStatsResponse {
    private long totalUsers;
    private long totalWallets;
    private long totalSplits;
    private long totalTransactions;
    private long pendingRequests;

    public AdminStatsResponse() {}

    public long getTotalUsers() { return totalUsers; }
    public long getTotalWallets() { return totalWallets; }
    public long getTotalSplits() { return totalSplits; }
    public long getTotalTransactions() { return totalTransactions; }
    public long getPendingRequests() { return pendingRequests; }

    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public void setTotalWallets(long totalWallets) { this.totalWallets = totalWallets; }
    public void setTotalSplits(long totalSplits) { this.totalSplits = totalSplits; }
    public void setTotalTransactions(long totalTransactions) { this.totalTransactions = totalTransactions; }
    public void setPendingRequests(long pendingRequests) { this.pendingRequests = pendingRequests; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalUsers;
        private long totalWallets;
        private long totalSplits;
        private long totalTransactions;
        private long pendingRequests;

        public Builder totalUsers(long v) { this.totalUsers = v; return this; }
        public Builder totalWallets(long v) { this.totalWallets = v; return this; }
        public Builder totalSplits(long v) { this.totalSplits = v; return this; }
        public Builder totalTransactions(long v) { this.totalTransactions = v; return this; }
        public Builder pendingRequests(long v) { this.pendingRequests = v; return this; }

        public AdminStatsResponse build() {
            AdminStatsResponse r = new AdminStatsResponse();
            r.totalUsers = this.totalUsers;
            r.totalWallets = this.totalWallets;
            r.totalSplits = this.totalSplits;
            r.totalTransactions = this.totalTransactions;
            r.pendingRequests = this.pendingRequests;
            return r;
        }
    }
}
