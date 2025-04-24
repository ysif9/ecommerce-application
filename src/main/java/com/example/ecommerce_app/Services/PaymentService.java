package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.*;
import com.example.ecommerce_app.Repositories.PaymentRepository;
import com.example.ecommerce_app.Repositories.UserOrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserOrderRepository orderRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserOrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public Payment processPayment(Long orderId, PaymentMethod method, double amount, LocalUser user) {
        UserOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        if (order.getUser().getID() != user.getID()) {
            throw new SecurityException("You can only pay for your own orders.");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setUser(user);
        payment.setMethod(method.toString());
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        // Simulate payment success
        boolean success = true;
        payment.setStatus(success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);

        return paymentRepository.save(payment);
    }

    public Payment getPaymentByOrderId(Long orderId) {
        UserOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new NoSuchElementException("Payment not found for order ID: " + orderId));
    }

    public Payment updatePaymentStatus(Long id, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Payment not found with ID: " + id));

        payment.setStatus(newStatus);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsForUser(LocalUser user) {
        return paymentRepository.findByUser(user);
    }

    public void addOrder(UserOrder order) {
        orderRepository.save(order);
    }

    public Payment updatePayment(Payment updatedPayment) {
        Payment existingPayment = paymentRepository.findById(updatedPayment.getId())
                .orElseThrow(() -> new NoSuchElementException("Payment not found with ID: " + updatedPayment.getId()));

        existingPayment.setMethod(updatedPayment.getMethod());
        existingPayment.setAmount(updatedPayment.getAmount());
        existingPayment.setStatus(updatedPayment.getStatus());
        existingPayment.setTransactionId(updatedPayment.getTransactionId());

        return paymentRepository.save(existingPayment);
    }


    public void handleNotification(PaymentNotification notification) {
        if (notification.getOrderId() != null) {
            try {
                // Find payment by order ID
                UserOrder order = orderRepository.findById(notification.getOrderId())
                        .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + notification.getOrderId()));

                Payment payment = paymentRepository.findByOrder(order)
                        .orElseThrow(() -> new NoSuchElementException("Payment not found for order ID: " + notification.getOrderId()));

                // Update payment based on notification
                if (notification.getStatus() != null) {
                    try {
                        // Try to convert string status to enum
                        PaymentStatus status = PaymentStatus.valueOf(notification.getStatus().toUpperCase());
                        payment.setStatus(status);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid payment status: " + notification.getStatus());
                    }
                }

                if (notification.getTransactionId() != null) {
                    payment.setTransactionId(notification.getTransactionId());
                }

                paymentRepository.save(payment);
            } catch (Exception e) {
                // Log the error instead of just printing to console
                System.err.println("Error processing payment notification: " + e.getMessage());
            }
        } else {
            System.err.println("Invalid payment notification: missing order ID");
        }
    }
}
