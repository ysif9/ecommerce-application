package com.example.ecommerce_app.Controllers;

import com.example.ecommerce_app.Model.Payment;
import com.example.ecommerce_app.Services.PaymentService;
import com.example.ecommerce_app.Model.PaymentRequest;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerce_app.Model.PaymentNotification;
import com.example.ecommerce_app.Model.PaymentMethod;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // GET /api/payments/{orderId}
    @GetMapping("/{orderId}")
    public Optional<Payment> getPaymentByOrderId(@PathVariable Long orderId) {
        return Optional.ofNullable(paymentService.getPaymentByOrderId(orderId));
    }

    // POST /api/payments
    @PostMapping
    public Payment processPayment(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.processPayment(
            paymentRequest.getOrderId(),
            PaymentMethod.fromString(paymentRequest.getPaymentMethod()),
            paymentRequest.getAmount(),
            paymentRequest.getLocalUser()
        );
    }

    // PUT /api/payments/{id}
    @PutMapping("/{id}")
    public Payment updatePayment(@PathVariable Long id, @RequestBody Payment updatedPayment) {
        updatedPayment.setId(id);
        return paymentService.updatePayment(updatedPayment);
    }

    // POST /api/payments/notify
    @PostMapping("/notify")
    public void handlePaymentNotification(@RequestBody PaymentNotification notification) {
        paymentService.handleNotification(notification);
    }
}
