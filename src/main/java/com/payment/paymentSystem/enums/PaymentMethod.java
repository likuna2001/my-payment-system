package com.payment.paymentSystem.enums;


public enum PaymentMethod {
    CREDIT_CARD("Credit Card", true),
    DEBIT_CARD("Debit Card", true),
    BANK_TRANSFER("Bank Transfer", true),
    PAYPAL("PayPal", true),
    CASH("Cash", false),
    CRYPTOCURRENCY("Cryptocurrency", true);

    private final String description;
    private final boolean online;

    PaymentMethod(String description, boolean online) {
        this.description = description;
        this.online = online;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOnlineMethod() {
        return online;
    }

    @Override
    public String toString() {
        return description;
    }
}
