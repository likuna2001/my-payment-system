package com.payment.paymentSystem.service;

import com.payment.paymentSystem.entity.User;
import java.util.*;
import java.util.stream.Collectors;


public class UserService {
    private final Map<String, User> users;
    private int userIdSequence = 1;

    public UserService() {
        this.users = new HashMap<>();
        initializeTestUsers();
    }


    public User registerUser(String firstName, String lastName, String email, String phoneNumber) {

        validateUserInput(firstName, lastName, email, phoneNumber);


        if (isEmailExists(email)) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        String userId = generateUserId();
        User newUser = new User(userId, firstName, lastName, email, phoneNumber);
        users.put(userId, newUser);

        System.out.println("User successfully registered: " + newUser.getFullName());
        return newUser;
    }


    public Optional<User> findUserById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }


    public Optional<User> findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }


    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }


    public List<User> getActiveUsers() {
        return users.values().stream()
                .filter(User::isActive)
                .collect(Collectors.toList());
    }



    public boolean addBalance(String userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        User user = users.get(userId);
        if (user == null) {
            System.out.println("User not found with ID: " + userId);
            return false;
        }

        user.addToBalance(amount);
        System.out.printf("Balance added: %s - %.2f ₾ (new balance: %.2f ₾)%n",
                user.getFullName(), amount, user.getBalance());
        return true;
    }


    public boolean deductBalance(String userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        User user = users.get(userId);
        if (user == null) {
            System.out.println("User not found with ID: " + userId);
            return false;
        }

        boolean success = user.deductFromBalance(amount);
        if (success) {
            System.out.printf("Balance deducted: %s - %.2f ₾ (remaining: %.2f ₾)%n",
                    user.getFullName(), amount, user.getBalance());
        } else {
            System.out.printf("Insufficient balance: %s (available: %.2f ₾)%n",
                    user.getFullName(), user.getBalance());
        }
        return success;
    }


    public int getUserCount() {
        return users.size();
    }


    public long getActiveUserCount() {
        return users.values().stream()
                .filter(User::isActive)
                .count();
    }


    public long getInactiveUserCount() {
        return users.values().stream()
                .filter(user -> !user.isActive())
                .count();
    }


    public double getAverageBalance() {
        return users.values().stream()
                .filter(User::isActive)
                .mapToDouble(User::getBalance)
                .average()
                .orElse(0.0);
    }


    public double getMaxBalance() {
        return users.values().stream()
                .filter(User::isActive)
                .mapToDouble(User::getBalance)
                .max()
                .orElse(0.0);
    }


    public double getTotalBalance() {
        return users.values().stream()
                .filter(User::isActive)
                .mapToDouble(User::getBalance)
                .sum();
    }


    private void validateUserInput(String firstName, String lastName, String email, String phoneNumber) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email == null || email.trim().isEmpty() || !isValidEmailFormat(email)) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
    }

    private boolean isValidEmailFormat(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isEmailExists(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    private String generateUserId() {
        return "USER_" + (userIdSequence++);
    }

    private void initializeTestUsers() {

        User user1 = new User("USER_1", "Giorgi", "", "giorgi@example.com", "555123456");
        user1.setBalance(1000.0);
        users.put("USER_1", user1);

        User user2 = new User("USER_2", "Nino", "", "nino@example.com", "555654321");
        user2.setBalance(500.0);
        users.put("USER_2", user2);

        User user3 = new User("USER_3", "Davit", "", "davit@example.com", "555789012");
        user3.setBalance(750.0);
        users.put("USER_3", user3);

        userIdSequence = 4;
        System.out.println("Test users initialized");
    }
}
