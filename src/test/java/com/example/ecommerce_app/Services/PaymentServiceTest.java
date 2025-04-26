package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.*;
import com.example.ecommerce_app.Repositories.PaymentRepository;
import com.example.ecommerce_app.Repositories.UserOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserOrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private LocalUser user;
    private UserOrder order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test user
        user = new LocalUser();
        user.setID(1L);
        user.setUsername("testuser");

        // Initialize test order
        order = new UserOrder();
        order.setOrderID(1L);
        order.setUser(user);

        // Initialize test payment
        payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setUser(user);
        payment.setAmount(100.0);
        payment.setMethod(PaymentMethod.CREDIT_CARD.toString());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("1: Test successful payment processing")
    void processPayment_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.processPayment(1L, PaymentMethod.CREDIT_CARD, 100.0, user);

        assertNotNull(result);
        assertEquals(100.0, result.getAmount());
        assertEquals(PaymentMethod.CREDIT_CARD.toString(), result.getMethod());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("2: Test payment processing with non-existent order")
    void processPayment_orderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                paymentService.processPayment(999L, PaymentMethod.CREDIT_CARD, 100.0, user));
    }

    @Test
    @DisplayName("3: Test payment processing with unauthorized user")
    void processPayment_unauthorizedUser() {
        LocalUser differentUser = new LocalUser();
        differentUser.setID(2L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(SecurityException.class, () ->
                paymentService.processPayment(1L, PaymentMethod.CREDIT_CARD, 100.0, differentUser));
    }

    @Test
    @DisplayName("4: Test retrieving payment by order ID success")
    void getPaymentByOrderId_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentByOrderId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100.0, result.getAmount());
        assertEquals(PaymentMethod.CREDIT_CARD.toString(), result.getMethod());
    }

    @Test
    @DisplayName("5: Test retrieving payment by non-existent order ID")
    void getPaymentByOrderId_orderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> paymentService.getPaymentByOrderId(999L));
    }

    @Test
    @DisplayName("6: Test retrieving payment when no payment exists for order")
    void getPaymentByOrderId_paymentNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> paymentService.getPaymentByOrderId(1L));
    }

    @Test
    @DisplayName("7: Test updating payment status success")
    void updatePaymentStatus_success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.updatePaymentStatus(1L, PaymentStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    @DisplayName("8: Test updating status of non-existent payment")
    void updatePaymentStatus_paymentNotFound() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                paymentService.updatePaymentStatus(999L, PaymentStatus.COMPLETED));
    }

    @Test
    @DisplayName("9: Test retrieving payments for user")
    void getPaymentsForUser_success() {
        when(paymentRepository.findByUser(user)).thenReturn(List.of(payment));

        List<Payment> result = paymentService.getPaymentsForUser(user);

        assertEquals(1, result.size());
        assertEquals(payment, result.get(0));
    }

    @Test
    @DisplayName("10: Test retrieving payments for user with no payments")
    void getPaymentsForUser_noPayments() {
        when(paymentRepository.findByUser(user)).thenReturn(List.of());

        List<Payment> result = paymentService.getPaymentsForUser(user);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("11: Test adding order success")
    void addOrder_success() {
        when(orderRepository.save(order)).thenReturn(order);

        paymentService.addOrder(order);

        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("12: Test updating payment details success")
    void updatePayment_success() {
        Payment updatedPayment = new Payment();
        updatedPayment.setId(1L);
        updatedPayment.setAmount(150.0);
        updatedPayment.setMethod(PaymentMethod.PAYPAL.toString());
        updatedPayment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);

        Payment result = paymentService.updatePayment(updatedPayment);

        assertNotNull(result);
        assertEquals(150.0, result.getAmount());
        assertEquals(PaymentMethod.PAYPAL.toString(), result.getMethod());
        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("13: Test updating non-existent payment")
    void updatePayment_paymentNotFound() {
        Payment updatedPayment = new Payment();
        updatedPayment.setId(999L);

        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> paymentService.updatePayment(updatedPayment));
    }

    @Test
    @DisplayName("14: Test handling payment notification")
    void handlePaymentNotification_success() {
        PaymentNotification notification = new PaymentNotification();
        notification.setTransactionId("TX123");
        notification.setStatus("SUCCESS");

        paymentService.handleNotification(notification);

        // Since handleNotification only logs, verify no exceptions are thrown
        verifyNoInteractions(paymentRepository, orderRepository);
    }
}