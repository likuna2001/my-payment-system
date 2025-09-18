package com.payment.paymentSystem.exceptions;


public class UserNotFoundException extends PaymentException {

    private final String userId;

    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
