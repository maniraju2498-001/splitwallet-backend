package com.splitwallet.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    // No-args constructor
    public UpdateProfileRequest() {
    }

    // All-args constructor
    public UpdateProfileRequest(String fullName, String mobile) {
        this.fullName = fullName;
        this.mobile = mobile;
    }

    // Getters
    public String getFullName() {
        return fullName;
    }

    public String getMobile() {
        return mobile;
    }

    // Setters
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fullName;
        private String mobile;

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder mobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public UpdateProfileRequest build() {
            return new UpdateProfileRequest(fullName, mobile);
        }
    }
}
