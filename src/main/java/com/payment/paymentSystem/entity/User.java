package com.payment.paymentSystem.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private double balance;
    private LocalDateTime createdAt;
    private boolean isActive;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.balance = 0.0;
    }

    public User(String id, String firstName, String lastName, String email, String phoneNumber) {
        this();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User(String id, String firstName, String lastName, String email, String phoneNumber, double balance) {
        this(id, firstName, lastName, email, phoneNumber);
        this.balance = balance;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = Math.max(0, balance); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getFullName() { return firstName + " " + lastName; }

    public void addToBalance(double amount) {
        if (amount > 0) this.balance += amount;
    }

    public boolean deductFromBalance(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    public boolean hasSufficientBalance(double amount) { return this.balance >= amount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", balance=" + balance +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}