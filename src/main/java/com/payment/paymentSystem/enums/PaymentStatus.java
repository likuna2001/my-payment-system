package com.payment.paymentSystem.enums;

public enum PaymentStatus {
    PENDING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public boolean isFinalStatus() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }


    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    @Override
    public String toString() {
        return description;
    }
}
