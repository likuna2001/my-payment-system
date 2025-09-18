package com.payment.paymentSystem;

import com.payment.paymentSystem.enums.Currency;
import com.payment.paymentSystem.enums.PaymentMethod;
import com.payment.paymentSystem.enums.PaymentStatus;
import com.payment.paymentSystem.entity.Payment;
import com.payment.paymentSystem.entity.PaymentRequest;
import com.payment.paymentSystem.service.PaymentService;
import com.payment.paymentSystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class PaymentSystemApplicationTests {

	private UserService userService;
	private PaymentService paymentService;

	@BeforeEach
	void setUp() {
		userService = new UserService();
		paymentService = new PaymentService(userService);
	}

	@Test
	void testSuccessfulPaymentWithBuilder() {

		PaymentRequest request = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(50.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("ტესტ გადახდა")
				.setReferenceNumber("TEST001")
				.build();

		Payment payment = paymentService.processPayment(request);

		assertNotNull(payment);
		assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
		assertEquals(50.0, payment.getAmount());
		assertNotNull(payment.getReference());
		assertTrue(payment.isSuccessful());
	}

	@Test
	void testSuccessfulPaymentWithConstructor() {

		PaymentRequest request = new PaymentRequest("USER_1", "USER_2", 75.0, "ტესტ გადახდა");

		Payment payment = paymentService.processPayment(request);

		assertNotNull(payment);
		assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
		assertEquals(75.0, payment.getAmount());
		assertEquals(Currency.GEL, payment.getCurrency()); // default currency
		assertEquals(PaymentMethod.BANK_TRANSFER, payment.getPaymentMethod()); // default method
	}

	@Test
	void testInsufficientBalance() {

		PaymentRequest request = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(2000.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("ზედმეტი თანხის ტესტი")
				.build();

		Payment payment = paymentService.processPayment(request);

		assertNotNull(payment);
		assertEquals(PaymentStatus.FAILED, payment.getStatus());
		assertTrue(payment.isFailed());
		assertNotNull(payment.getErrorMessage());
	}

	@Test
	void testInvalidSender() {

		PaymentRequest request = PaymentRequest.builder()
				.setSenderUserId("INVALID_USER")
				.setReceiverUserId("USER_2")
				.setAmount(50.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("ტესტი")
				.build();

		assertThrows(IllegalArgumentException.class, () -> {
			paymentService.processPayment(request);
		});
	}

	@Test
	void testInvalidReceiver() {

		PaymentRequest request = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("INVALID_USER")
				.setAmount(50.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("ტესტი")
				.build();

		assertThrows(IllegalArgumentException.class, () -> {
			paymentService.processPayment(request);
		});
	}

	@Test
	void testDifferentCurrencies() {

		PaymentRequest usdRequest = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(10.0)
				.setCurrency(Currency.USD)
				.setPaymentMethod(PaymentMethod.CREDIT_CARD)
				.setDescription("USD ტესტი")
				.build();

		PaymentRequest eurRequest = PaymentRequest.builder()
				.setSenderUserId("USER_2")
				.setReceiverUserId("USER_3")
				.setAmount(5.0)
				.setCurrency(Currency.EUR)
				.setPaymentMethod(PaymentMethod.DEBIT_CARD)
				.setDescription("EUR ტესტი")
				.build();

		Payment usdPayment = paymentService.processPayment(usdRequest);
		Payment eurPayment = paymentService.processPayment(eurRequest);

		assertEquals(Currency.USD, usdPayment.getCurrency());
		assertEquals(Currency.EUR, eurPayment.getCurrency());
		assertEquals(10.0, usdPayment.getAmount());
		assertEquals(5.0, eurPayment.getAmount());
	}

	@Test
	void testPaymentHistory() {

		PaymentRequest request1 = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(100.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("ტესტი 1")
				.setReferenceNumber("TEST005")
				.build();

		PaymentRequest request2 = PaymentRequest.builder()
				.setSenderUserId("USER_2")
				.setReceiverUserId("USER_1")
				.setAmount(50.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.CREDIT_CARD)
				.setDescription("ტესტი 2")
				.setReferenceNumber("TEST006")
				.build();

		paymentService.processPayment(request1);
		paymentService.processPayment(request2);

		assertEquals(2, paymentService.getPaymentsByUserId("USER_1").size());
		assertEquals(2, paymentService.getPaymentsByUserId("USER_2").size());
		assertEquals(1, paymentService.getSentPayments("USER_1").size());
		assertEquals(1, paymentService.getReceivedPayments("USER_1").size());
	}

	@Test
	void testPaymentByReference() {

		String referenceNumber = "TEST_REF_123";
		PaymentRequest request = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(25.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("რეფერენსის ტესტი")
				.setReferenceNumber(referenceNumber)
				.build();

		Payment payment = paymentService.processPayment(request);
		Optional<Payment> foundPayment = paymentService.getPaymentByReference(referenceNumber);

		assertTrue(foundPayment.isPresent());
		assertEquals(payment.getId(), foundPayment.get().getId());
	}

	@Test
	void testPaymentsByStatus() {

		PaymentRequest successRequest = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(30.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("წარმატებული")
				.build();

		PaymentRequest failRequest = PaymentRequest.builder()
				.setSenderUserId("USER_3")
				.setReceiverUserId("USER_1")
				.setAmount(1000.0) // მეტი ვიდრე USER_3-ის ბალანსი
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("ვერ შესრულებული")
				.build();

		paymentService.processPayment(successRequest);
		paymentService.processPayment(failRequest);

		List<Payment> completedPayments = paymentService.getPaymentsByStatus(PaymentStatus.COMPLETED);
		List<Payment> failedPayments = paymentService.getPaymentsByStatus(PaymentStatus.FAILED);

		assertFalse(completedPayments.isEmpty());
		assertFalse(failedPayments.isEmpty());

		assertTrue(completedPayments.stream().allMatch(Payment::isSuccessful));

		assertTrue(failedPayments.stream().allMatch(Payment::isFailed));
	}

	@Test
	void testPaymentStatistics() {
		// სტატისტიკის ტესტი
		PaymentRequest request1 = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(100.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("სტატისტიკა 1")
				.build();

		PaymentRequest request2 = PaymentRequest.builder()
				.setSenderUserId("USER_2")
				.setReceiverUserId("USER_3")
				.setAmount(50.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.CREDIT_CARD)
				.setDescription("სტატისტიკა 2")
				.build();

		paymentService.processPayment(request1);
		paymentService.processPayment(request2);

		assertTrue(paymentService.getSuccessfulPaymentsCount() >= 2);
		assertTrue(paymentService.getTotalPaymentsAmount() >= 150.0);
		assertTrue(paymentService.getAveragePaymentAmount() > 0);
		assertEquals(paymentService.getPaymentsCount(), paymentService.getAllPayments().size());
	}

	@Test
	void testUserBalanceHistory() {
		PaymentRequest outgoingRequest = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(200.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("გაგზავნილი")
				.build();

		PaymentRequest incomingRequest = PaymentRequest.builder()
				.setSenderUserId("USER_3")
				.setReceiverUserId("USER_1")
				.setAmount(150.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.CREDIT_CARD)
				.setDescription("მიღებული")
				.build();

		paymentService.processPayment(outgoingRequest);
		paymentService.processPayment(incomingRequest);

		Map<String, Double> history = paymentService.getUserBalanceHistory("USER_1");

		assertTrue(history.containsKey("sent"));
		assertTrue(history.containsKey("received"));
		assertTrue(history.containsKey("net"));
		assertEquals(200.0, history.get("sent"));
		assertEquals(150.0, history.get("received"));
		assertEquals(-50.0, history.get("net")); // 150 - 200 = -50
	}

	@Test
	void testPaymentCancellation() {

		PaymentRequest request = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(75.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("გაუქმების ტესტი")
				.build();

		Payment payment = paymentService.processPayment(request);


		boolean cancelled = paymentService.cancelPayment(payment.getId(), "USER_1");
		assertFalse(cancelled);

	}

	@Test
	void testInvalidPaymentRequest() {
		PaymentRequest invalidRequest = PaymentRequest.builder()
				.setSenderUserId("") // ცარიელი sender
				.setReceiverUserId("USER_2")
				.setAmount(50.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("არასწორი ტესტი")
				.build();

		assertThrows(IllegalArgumentException.class, () -> {
			paymentService.processPayment(invalidRequest);
		});
	}

	@Test
	void testPaymentAmountRange() {

		PaymentRequest smallRequest = PaymentRequest.builder()
				.setSenderUserId("USER_1")
				.setReceiverUserId("USER_2")
				.setAmount(10.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.BANK_TRANSFER)
				.setDescription("პატარა თანხა")
				.build();

		PaymentRequest largeRequest = PaymentRequest.builder()
				.setSenderUserId("USER_2")
				.setReceiverUserId("USER_3")
				.setAmount(200.0)
				.setCurrency(Currency.GEL)
				.setPaymentMethod(PaymentMethod.CREDIT_CARD)
				.setDescription("დიდი თანხა")
				.build();

		paymentService.processPayment(smallRequest);
		paymentService.processPayment(largeRequest);

		List<Payment> smallPayments = paymentService.getPaymentsByAmountRange(0, 50);
		List<Payment> largePayments = paymentService.getPaymentsByAmountRange(100, 300);

		assertFalse(smallPayments.isEmpty());
		assertFalse(largePayments.isEmpty());


		assertTrue(smallPayments.stream().allMatch(p -> p.getAmount() <= 50));

		assertTrue(largePayments.stream().allMatch(p -> p.getAmount() >= 100));
	}
}
