package com.payment.paymentSystem;

import com.payment.paymentSystem.enums.Currency;
import com.payment.paymentSystem.enums.PaymentMethod;
import com.payment.paymentSystem.enums.PaymentStatus;
import com.payment.paymentSystem.entity.Payment;
import com.payment.paymentSystem.entity.PaymentRequest;
import com.payment.paymentSystem.entity.User;
import com.payment.paymentSystem.service.PaymentService;
import com.payment.paymentSystem.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class PaymentSystemApplication {
	private static final Scanner scanner = new Scanner(System.in);
	private static UserService userService;
	private static PaymentService paymentService;


	private static final int MENU_SHOW_USERS = 1;
	private static final int MENU_REGISTER_USER = 2;
	private static final int MENU_ADD_BALANCE = 3;
	private static final int MENU_MAKE_PAYMENT = 4;
	private static final int MENU_SHOW_USER_PAYMENTS = 5;
	private static final int MENU_SHOW_ALL_PAYMENTS = 6;
	private static final int MENU_SHOW_STATISTICS = 7;
	private static final int MENU_SHOW_PAYMENTS_BY_STATUS = 8;
	private static final int MENU_EXIT = 0;

	public static void main(String[] args) {
		System.out.println("Payment System");
		System.out.println("Welcome!");
		System.out.println();

		initializeServices();

		runAutomaticDemo();

		runInteractiveMenu();
	}

	private static void initializeServices() {
		userService = new UserService();
		paymentService = new PaymentService(userService);
		System.out.println("System initialized!");
		System.out.println("Number of users: " + userService.getUserCount());
		System.out.println();
	}

	private static void runAutomaticDemo() {
		System.out.println("=== Automatic Demo ===");


		System.out.println("\n1.Existing users:");
		showAllUsers();


		System.out.println("\n2.New user registration:");
		try {
			User newUser = userService.registerUser("Anna", "", "ana@example.com", "555000111");
			userService.addBalance(newUser.getId(), 300.0);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}


		System.out.println("\n3.Payment examples:");
		performSamplePayments();


		System.out.println("\n4.System statistics:");
		showSystemStatistics();

		System.out.println("\n === Demo completed ===\n");
	}

	private static void performSamplePayments() {

		PaymentRequest request1 = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(100.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("Test payment 1")
				.setReferenceNumber("REF001")
				.build();
		Payment payment1 = paymentService.processPayment(request1);
		System.out.println("Payment 1 status: " + payment1.getStatus());


		PaymentRequest request2 = PaymentRequest.builder()
				.setSenderUserId("USER_2")
				.setReceiverUserId("USER_3")
				.setAmount(50.0)
				.setCurrency(Currency.USD)
				.setPaymentMethod(PaymentMethod.CREDIT_CARD)
				.setDescription("Test payment 2 (USD)")
				.setReferenceNumber("REF002")
				.build();
		Payment payment2 = paymentService.processPayment(request2);
		System.out.println("Payment 2 status: " + payment2.getStatus());


		PaymentRequest request3 = PaymentRequest.builder()
				.setSenderUserId("USER_3")
				.setReceiverUserId("USER_1")
				.setAmount(2000.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.DEBIT_CARD)
				.setDescription("Test payment 3 (insufficient balance)")
				.setReferenceNumber("REF003")
				.build();
		Payment payment3 = paymentService.processPayment(request3);
		System.out.println(" Payment 3 status: " + payment3.getStatus());
		if (payment3.isFailed()) {
			System.out.println(" Error reason: " + payment3.getErrorMessage());
		}
	}

	private static void runInteractiveMenu() {
		while (true) {
			showMenu();
			int choice = getIntInput(" Choose option: ");

			switch (choice) {
				case MENU_SHOW_USERS -> showAllUsers();
				case MENU_REGISTER_USER -> registerNewUser();
				case MENU_ADD_BALANCE -> addBalanceToUser();
				case MENU_MAKE_PAYMENT -> makePayment();
				case MENU_SHOW_USER_PAYMENTS -> showUserPayments();
				case MENU_SHOW_ALL_PAYMENTS -> showAllPayments();
				case MENU_SHOW_STATISTICS -> showSystemStatistics();
				case MENU_SHOW_PAYMENTS_BY_STATUS -> showPaymentsByStatus();
				case MENU_EXIT -> {
					System.out.println(" Thank you for using the payment system!");
					return;
				}
				default -> System.out.println(" Invalid choice! Please try again.");
			}
			System.out.println();
		}
	}

	private static void showMenu() {
		System.out.println(" === Menu ===");
		System.out.println("1. Show all users");
		System.out.println("2. Register new user");
		System.out.println("3. Add balance");
		System.out.println("4. Make payment");
		System.out.println("5. User payments");
		System.out.println("6. Show all payments");
		System.out.println("7. System statistics");
		System.out.println("8. Payments by status");
		System.out.println("0. Exit");
		System.out.println("==================");
	}

	private static void showAllUsers() {
		List<User> users = userService.getAllUsers();
		System.out.println(" === All Users ===");
		for (User user : users) {
			System.out.printf(" %s |  %s |  %s |  %.2f ₾ | %s%n",
					user.getId(), user.getFullName(), user.getEmail(),
					user.getBalance(), user.isActive() ? " Active" : " Inactive");
		}
	}

	private static void registerNewUser() {
		System.out.println(" === New User Registration ===");
		System.out.print("First name: ");
		String firstName = scanner.nextLine();
		System.out.print("Last name: ");
		String lastName = scanner.nextLine();
		System.out.print("Email: ");
		String email = scanner.nextLine();
		System.out.print("Phone: ");
		String phone = scanner.nextLine();

		try {
			User user = userService.registerUser(firstName, lastName, email, phone);
			System.out.println(" User successfully registered!");
			System.out.println(" User ID: " + user.getId());
		} catch (Exception e) {
			System.out.println(" Error: " + e.getMessage());
		}
	}

	private static void addBalanceToUser() {
		System.out.println(" === Add Balance ===");
		showAllUsers();
		System.out.print("User ID: ");
		String userId = scanner.nextLine().trim();
		double amount = getDoubleInput("Amount (₾): ");

		try {
			boolean success = userService.addBalance(userId, amount);
			if (success) {
				System.out.println(" Balance successfully added!");
			} else {
				System.out.println(" User not found!");
			}
		} catch (Exception e) {
			System.out.println(" Error: " + e.getMessage());
		}
	}

	private static void makePayment() {
		System.out.println(" === Make Payment ===");
		showAllUsers();

		System.out.print("Sender ID: ");
		String senderId = scanner.nextLine().trim();
		System.out.print("Receiver ID: ");
		String receiverId = scanner.nextLine().trim();
		double amount = getDoubleInput("Amount: ");

		System.out.println("Currency:");
		Currency[] currencies = Currency.values();
		for (int i = 0; i < currencies.length; i++) {
			System.out.println((i + 1) + ". " + currencies[i]);
		}
		int currencyChoice = getIntInput("Choose currency: ") - 1;
		Currency currency = currencies[currencyChoice];

		System.out.println("Payment method:");
		PaymentMethod[] methods = PaymentMethod.values();
		for (int i = 0; i < methods.length; i++) {
			System.out.println((i + 1) + ". " + methods[i]);
		}
		int methodChoice = getIntInput("Choose method: ") - 1;
		PaymentMethod method = methods[methodChoice];

		System.out.print("Description (optional): ");
		String description = scanner.nextLine();

		try {

			PaymentRequest request = PaymentRequest.builder()
					.setSenderUserId(senderId)
					.setReceiverUserId(receiverId)
					.setAmount(amount)
					.setCurrency(currency)
					.setPaymentMethod(method)
					.setDescription(description)
					.setReferenceNumber("PAY_" + System.currentTimeMillis())
					.build();
			Payment payment = paymentService.processPayment(request);

			System.out.println(" Payment ID: " + payment.getId());
			System.out.println(" Status: " + payment.getStatus());
			System.out.println(" Reference: " + payment.getReference());

			if (payment.isFailed()) {
				System.out.println(" Error: " + payment.getErrorMessage());
			}
		} catch (Exception e) {
			System.out.println(" Error: " + e.getMessage());
		}
	}

	private static void showUserPayments() {
		System.out.println(" === User Payments ===");
		showAllUsers();
		System.out.print("User ID: ");
		String userId = scanner.nextLine().trim();

		List<Payment> payments = paymentService.getPaymentsByUserId(userId);
		if (payments.isEmpty()) {
			System.out.println(" No payments found.");
			return;
		}

		System.out.println("=== Payment History ===");
		for (Payment payment : payments) {
			String direction = userId.equals(payment.getSenderId()) ? "📤 Sent" : "📥 Received";
			System.out.printf("%s | %s | %.2f %s | %s | %s%n",
					payment.getId(), direction, payment.getAmount(),
					payment.getCurrency().getSymbol(), payment.getStatus(),
					payment.getCreatedAt().toString().substring(0, 19));
		}


		Map<String, Double> history = paymentService.getUserBalanceHistory(userId);
		System.out.println("\n=== Balance History ===");
		System.out.printf(" Sent: %.2f ₾%n", history.get("sent"));
		System.out.printf(" Received: %.2f ₾%n", history.get("received"));
		System.out.printf(" Net: %.2f ₾%n", history.get("net"));
	}

	private static void showAllPayments() {
		System.out.println(" === All Payments ===");
		List<Payment> payments = paymentService.getAllPayments();

		if (payments.isEmpty()) {
			System.out.println(" No payments found.");
			return;
		}

		for (Payment payment : payments) {
			System.out.printf(" %s | %s ➡ %s | %.2f %s | %s | %s%n",
					payment.getId(), payment.getSenderId(), payment.getReceiverId(),
					payment.getAmount(), payment.getCurrency().getSymbol(),
					payment.getStatus(), payment.getCreatedAt().toString().substring(0, 19));
		}
	}

	private static void showSystemStatistics() {
		System.out.println(" === System Statistics ===");
		System.out.println(" Total users: " + userService.getUserCount());
		System.out.println(" Active users: " + userService.getActiveUserCount());
		System.out.println("Total payments: " + paymentService.getPaymentsCount());
		System.out.println("Successful payments: " + paymentService.getSuccessfulPaymentsCount());
		System.out.println(" Failed payments: " + paymentService.getFailedPaymentsCount());
		System.out.printf(" Total payment amount: %.2f ₾%n", paymentService.getTotalPaymentsAmount());
		System.out.printf(" Average payment: %.2f ₾%n", paymentService.getAveragePaymentAmount());
		System.out.printf(" Total balance: %.2f ₾%n", userService.getTotalBalance());
		System.out.printf(" Average balance: %.2f ₾%n", userService.getAverageBalance());
	}

	private static void showPaymentsByStatus() {
		System.out.println(" === Payments by Status ===");
		PaymentStatus[] statuses = PaymentStatus.values();
		for (int i = 0; i < statuses.length; i++) {
			System.out.println((i + 1) + ". " + statuses[i]);
		}

		int choice = getIntInput("Choose status: ") - 1;
		PaymentStatus status = statuses[choice];

		List<Payment> payments = paymentService.getPaymentsByStatus(status);
		if (payments.isEmpty()) {
			System.out.println(" No payments found with this status.");
			return;
		}

		System.out.println("=== Payments: " + status + " ===");
		for (Payment payment : payments) {
			System.out.printf(" %s | %s ️ %s | %.2f %s | %s%n",
					payment.getId(), payment.getSenderId(), payment.getReceiverId(),
					payment.getAmount(), payment.getCurrency().getSymbol(),
					payment.getCreatedAt().toString().substring(0, 19));
		}
	}


	private static int getIntInput(String prompt) {
		while (true) {
			try {
				System.out.print(prompt);
				return Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println(" Please enter a valid integer!");
			}
		}
	}

	private static double getDoubleInput(String prompt) {
		while (true) {
			try {
				System.out.print(prompt);
				return Double.parseDouble(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println(" Please enter a valid number!");
			}
		}
	}
}
