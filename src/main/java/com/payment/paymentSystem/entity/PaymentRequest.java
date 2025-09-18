package com.payment.paymentSystem.entity;

import com.payment.paymentSystem.enums.Currency;
import com.payment.paymentSystem.enums.PaymentMethod;
import java.util.Objects;

public class PaymentRequest {
    private String senderUserId;
    private String receiverUserId;
    private double amount;
    private Currency currency;
    private PaymentMethod paymentMethod;
    private String description;
    private String referenceNumber;

    private PaymentRequest() {
        this.currency = Currency.GEL;
        this.paymentMethod = PaymentMethod.BANK_TRANSFER;
        this.description = "";
        this.referenceNumber = "";
    }

    public PaymentRequest(String senderUserId, String receiverUserId, double amount, String description) {
        this();
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.amount = amount;
        this.description = description;
    }

    public PaymentRequest(String senderUserId, String receiverUserId, double amount,
                          Currency currency, PaymentMethod paymentMethod, String description, String referenceNumber) {
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.amount = amount;
        this.currency = currency != null ? currency : Currency.GEL;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.BANK_TRANSFER;
        this.description = description != null ? description : "";
        this.referenceNumber = referenceNumber != null ? referenceNumber : "";
    }

    public String getSenderUserId() { return senderUserId; }
    public void setSenderUserId(String senderUserId) { this.senderUserId = senderUserId; }
    public String getReceiverUserId() { return receiverUserId; }
    public void setReceiverUserId(String receiverUserId) { this.receiverUserId = receiverUserId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.amount = amount;
    }
    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency != null ? currency : Currency.GEL; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.BANK_TRANSFER; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description != null ? description : ""; }
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber != null ? referenceNumber : ""; }

    public boolean isValid() {
        return senderUserId != null && !senderUserId.trim().isEmpty()
                && receiverUserId != null && !receiverUserId.trim().isEmpty()
                && amount > 0
                && currency != null
                && paymentMethod != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentRequest)) return false;
        PaymentRequest that = (PaymentRequest) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(senderUserId, that.senderUserId) &&
                Objects.equals(receiverUserId, that.receiverUserId) &&
                currency == that.currency &&
                paymentMethod == that.paymentMethod &&
                Objects.equals(description, that.description) &&
                Objects.equals(referenceNumber, that.referenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderUserId, receiverUserId, amount, currency, paymentMethod, description, referenceNumber);
    }

    public static class Builder {
        private String senderUserId;
        private String receiverUserId;
        private double amount;
        private Currency currency = Currency.GEL;
        private PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;
        private String description = "";
        private String referenceNumber = "";

        public Builder setSenderUserId(String senderUserId) { this.senderUserId = senderUserId; return this; }
        public Builder setReceiverUserId(String receiverUserId) { this.receiverUserId = receiverUserId; return this; }
        public Builder setAmount(double amount) { this.amount = amount; return this; }
        public Builder setCurrency(Currency currency) { this.currency = currency != null ? currency : Currency.GEL; return this; }
        public Builder setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.BANK_TRANSFER; return this; }
        public Builder setDescription(String description) { this.description = description != null ? description : ""; return this; }
        public Builder setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber != null ? referenceNumber : ""; return this; }

        public PaymentRequest build() {
            PaymentRequest request = new PaymentRequest();
            request.senderUserId = this.senderUserId;
            request.receiverUserId = this.receiverUserId;
            request.amount = this.amount;
            request.currency = this.currency;
            request.paymentMethod = this.paymentMethod;
            request.description = this.description;
            request.referenceNumber = this.referenceNumber;
            return request;
        }
    }

    public static Builder builder() { return new Builder(); }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "senderUserId='" + senderUserId + '\'' +
                ", receiverUserId='" + receiverUserId + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                ", paymentMethod=" + paymentMethod +
                ", description='" + description + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                '}';
    }
}