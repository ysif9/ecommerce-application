package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.*;
import com.example.ecommerce_app.Repositories.PaymentRepository;
import com.example.ecommerce_app.Repositories.UserOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

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
        payment.setStatus(PaymentStatus.COMPLETED);

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
        System.out.println("Handling payment notification: " + notification);
    }
}
