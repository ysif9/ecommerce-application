package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {

    private final Map<Long, Payment> paymentStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    private final Map<Long, UserOrder> orderStore = new HashMap<>();

    @PostConstruct
    public void init() {
        // testing data
    }

    public Payment processPayment(Long orderId, PaymentMethod method, double amount, LocalUser user) {
        UserOrder order = orderStore.get(orderId);
        if (order == null) {
            throw new NoSuchElementException("Order not found with ID: " + orderId);
        }

        if (order.getUser().getID() != user.getID()) {
            throw new SecurityException("You can only pay for your own orders.");
        }

        Payment payment = new Payment();
        payment.setId(idGenerator.getAndIncrement());
        payment.setOrder(order);
        payment.setUser(user);
        payment.setMethod(method.toString());
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        // Simulate payment success
        boolean success = true;
        payment.setStatus(success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);

        paymentStore.put(payment.getId(), payment);
        return payment;
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentStore.values().stream()
                .filter(p -> p.getOrder().getOrderID() == orderId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Payment not found for order ID: " + orderId));
    }

    public Payment updatePaymentStatus(Long id, PaymentStatus newStatus) {
        Payment payment = paymentStore.get(id);
        if (payment == null) {
            throw new NoSuchElementException("Payment not found with ID: " + id);
        }

        payment.setStatus(newStatus);
        return payment;
    }

    public List<Payment> getPaymentsForUser(LocalUser user) {
        List<Payment> userPayments = new ArrayList<>();
        for (Payment payment : paymentStore.values()) {
            if (payment.getUser().getID() == user.getID()) {
                userPayments.add(payment);
            }
        }
        return userPayments;
    }

    public void addOrder(UserOrder order) {
        orderStore.put(order.getOrderID(), order);
    }

    public Payment updatePayment(Payment updatedPayment) {

        Payment existingPayment = paymentStore.get(updatedPayment.getId());
        if (existingPayment == null) {
            throw new NoSuchElementException("Payment not found with ID: " + updatedPayment.getId());
        }
        existingPayment.setMethod(updatedPayment.getMethod());
        existingPayment.setAmount(updatedPayment.getAmount());
        existingPayment.setStatus(updatedPayment.getStatus());
        existingPayment.setTransactionId(updatedPayment.getTransactionId());
        return updatedPayment;
    }


    public void handleNotification(PaymentNotification notification) {
        System.out.println("Handling payment notification: " + notification);
    }
}
