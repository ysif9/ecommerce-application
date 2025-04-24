package com.example.ecommerce_app.Controllers;

import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.Payment;
import com.example.ecommerce_app.Services.PaymentService;
import com.example.ecommerce_app.Model.PaymentRequest;
import com.example.ecommerce_app.Services.UserService;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerce_app.Model.PaymentNotification;
import com.example.ecommerce_app.Model.PaymentMethod;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    // GET /api/payments/{orderId}
    @GetMapping("/{orderId}")
    public Payment getPaymentByOrderId(@PathVariable Long orderId) {
        return paymentService.getPaymentByOrderId(orderId);
    }

    // POST /api/payments
    @PostMapping
    public Payment processPayment(@RequestBody PaymentRequest paymentRequest) {
        // Get the user from the database using the userId
        LocalUser user = userService.getUserById(paymentRequest.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + paymentRequest.getUserId());
        }

        return paymentService.processPayment(
            paymentRequest.getOrderId(),
            PaymentMethod.fromString(paymentRequest.getPaymentMethod()),
            paymentRequest.getAmount(),
            user
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
