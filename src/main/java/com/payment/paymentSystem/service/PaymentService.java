package com.payment.paymentSystem.service;

import com.payment.paymentSystem.enums.PaymentStatus;
import com.payment.paymentSystem.entity.Payment;
import com.payment.paymentSystem.entity.PaymentRequest;
import com.payment.paymentSystem.entity.User;
import java.util.*;
import java.util.stream.Collectors;

public class PaymentService {
    private final Map<String, Payment> payments;
    private final UserService userService;

    public PaymentService(UserService userService) {
        this.payments = new HashMap<>();
        this.userService = userService;
    }

    public Payment processPayment(PaymentRequest request) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("Invalid payment request");
        }

        Optional<User> sender = userService.findUserById(request.getSenderUserId());
        Optional<User> receiver = userService.findUserById(request.getReceiverUserId());

        if (sender.isEmpty()) {
            throw new IllegalArgumentException("Sender user not found");
        }

        if (receiver.isEmpty()) {
            throw new IllegalArgumentException("Receiver user not found");
        }

        if (!sender.get().isActive()) {
            throw new IllegalArgumentException("Sender user is not active");
        }

        if (!receiver.get().isActive()) {
            throw new IllegalArgumentException("Receiver user is not active");
        }

        Payment payment = new Payment(request);

        // Only set auto-generated reference if none provided
        if (payment.getReference() == null || payment.getReference().isEmpty()) {
            payment.setReference("PAY_" + System.currentTimeMillis());
        }

        payments.put(payment.getId(), payment);

        try {
            if (!sender.get().hasSufficientBalance(request.getAmount())) {
                payment.markAsFailed("Insufficient balance");
                System.out.println("Payment failed: Insufficient balance");
                return payment;
            }

            boolean deductSuccess = userService.deductBalance(request.getSenderUserId(), request.getAmount());
            if (!deductSuccess) {
                payment.markAsFailed("Failed to deduct amount");
                return payment;
            }

            boolean addSuccess = userService.addBalance(request.getReceiverUserId(), request.getAmount());
            if (!addSuccess) {
                userService.addBalance(request.getSenderUserId(), request.getAmount());
                payment.markAsFailed("Failed to add amount to receiver");
                return payment;
            }

            payment.markAsCompleted();
            System.out.printf("✅ Payment successful: %.2f ₾ from %s to %s%n",
                    request.getAmount(),
                    sender.get().getFullName(),
                    receiver.get().getFullName());

        } catch (Exception e) {
            payment.markAsFailed("System error: " + e.getMessage());
            System.out.println("❌ Payment error: " + e.getMessage());
        }

        return payment;
    }

    public Optional<Payment> getPaymentById(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    public Optional<Payment> getPaymentByReference(String reference) {
        return payments.values().stream()
                .filter(payment -> reference.equals(payment.getReference()))
                .findFirst();
    }

    public List<Payment> getPaymentsByUserId(String userId) {
        return payments.values().stream()
                .filter(payment -> userId.equals(payment.getSenderId()) ||
                        userId.equals(payment.getReceiverId()))
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<Payment> getSentPayments(String userId) {
        return payments.values().stream()
                .filter(payment -> userId.equals(payment.getSenderId()))
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<Payment> getReceivedPayments(String userId) {
        return payments.values().stream()
                .filter(payment -> userId.equals(payment.getReceiverId()))
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == status)
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<Payment> getAllPayments() {
        return payments.values().stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByAmountRange(double minAmount, double maxAmount) {
        return payments.values().stream()
                .filter(payment -> payment.getAmount() >= minAmount && payment.getAmount() <= maxAmount)
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<Payment> getTodayPayments() {
        return payments.values().stream()
                .filter(payment -> payment.getCreatedAt().toLocalDate().equals(java.time.LocalDate.now()))
                .collect(Collectors.toList());
    }

    public boolean cancelPayment(String paymentId, String userId) {
        Payment payment = payments.get(paymentId);

        if (payment == null) {
            return false;
        }

        if (!userId.equals(payment.getSenderId())) {
            return false;
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            return false;
        }

        payment.markAsCancelled();
        System.out.println("Payment cancelled: " + payment.getId());
        return true;
    }

    public double getTotalPaymentsAmount() {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public long getSuccessfulPaymentsCount() {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .count();
    }

    public long getFailedPaymentsCount() {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.FAILED)
                .count();
    }

    public double getAveragePaymentAmount() {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .average()
                .orElse(0.0);
    }

    public int getPaymentsCount() {
        return payments.size();
    }

    public Map<String, Double> getUserBalanceHistory(String userId) {
        Map<String, Double> history = new HashMap<>();

        double sent = getSentPayments(userId).stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .sum();

        double received = getReceivedPayments(userId).stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .sum();

        history.put("sent", sent);
        history.put("received", received);
        history.put("net", received - sent);

        return history;
    }
}