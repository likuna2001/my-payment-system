package com.payment.paymentSystem.entity;

import com.payment.paymentSystem.enums.Currency;
import com.payment.paymentSystem.enums.PaymentMethod;
import com.payment.paymentSystem.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Payment {
    private String id;
    private String senderId;
    private String receiverId;
    private double amount;
    private Currency currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String description;
    private String reference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String errorMessage;

    public Payment() {
        this.id = UUID.randomUUID().toString();
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.currency = Currency.GEL;
    }

    public Payment(PaymentRequest request) {
        this();
        this.senderId = request.getSenderUserId();
        this.receiverId = request.getReceiverUserId();
        this.amount = request.getAmount();
        this.currency = request.getCurrency();
        this.paymentMethod = request.getPaymentMethod();
        this.description = request.getDescription();
        this.reference = request.getReferenceNumber();
    }

    public Payment(String senderId, String receiverId, double amount, String description) {
        this();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.description = description;
        this.paymentMethod = PaymentMethod.BANK_TRANSFER;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public void markAsFailed(String errorMessage) {
        this.status = PaymentStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCompleted() {
        this.status = PaymentStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCancelled() {
        this.status = PaymentStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isSuccessful() { return status == PaymentStatus.COMPLETED; }
    public boolean isFailed() { return status == PaymentStatus.FAILED; }
    public boolean isCancelled() { return status == PaymentStatus.CANCELLED; }
    public boolean isPending() { return status == PaymentStatus.PENDING; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", reference='" + reference + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}