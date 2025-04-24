package com.example.ecommerce_app.ControllerUnitTest;

import com.example.ecommerce_app.Controllers.PaymentController;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.Payment;
import com.example.ecommerce_app.Model.PaymentStatus;
import com.example.ecommerce_app.Model.PaymentNotification;
import com.example.ecommerce_app.Model.PaymentRequest;
import com.example.ecommerce_app.Model.UserOrder;
import com.example.ecommerce_app.Services.PaymentService;
import com.example.ecommerce_app.Services.UserOrderService;
import com.example.ecommerce_app.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerMVCTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserOrderService orderService;

    @Autowired
    private ObjectMapper mapper;

    private String token;
    private LocalUser testUser;
    private UserOrder testOrder;
    private Payment testPayment;

    @BeforeEach
    void setup() {
        // Mock the token (since we're not doing real authentication)
        token = "mocked-jwt-token";

        // Create test user
        testUser = new LocalUser();
        testUser.setId(1L); // Assuming LocalUser has setId method
        testUser.setUsername("testuser");

        // Create test order
        testOrder = new UserOrder();
        testOrder.setOrderID(1L);
        testOrder.setUser(testUser);

        // Create test payment
        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setOrder(testOrder);
        testPayment.setAmount(100.0);
        testPayment.setMethod("CREDIT_CARD");
        testPayment.setUser(testUser);
        testPayment.setCreatedAt(LocalDateTime.now());
        testPayment.setStatus(PaymentStatus.PENDING);

        // Mock service behaviors
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userService.getUserById(999999L)).thenReturn(null);
        when(orderService.getOrderById(1L)).thenReturn(testOrder);
        when(orderService.getOrderById(999999L)).thenReturn(null);
        when(paymentService.getPaymentByOrderId(1L)).thenReturn(testPayment);
        when(paymentService.getPaymentByOrderId(999999L)).thenReturn(null);
        when(paymentService.getPaymentById(1L)).thenReturn(testPayment);
        when(paymentService.getPaymentById(999999L)).thenReturn(null);
        when(paymentService.processPayment(any(), any(), anyDouble(), any())).thenReturn(testPayment);
        when(paymentService.updatePayment(any())).thenReturn(testPayment);
        doNothing().when(paymentService).handleNotification(any());
    }

    @Test
    @DisplayName("Test1: Get payment by order ID success")
    void getPaymentByOrderId_success() throws Exception {
        mockMvc.perform(get("/api/payments/{orderId}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.amount", is(100.0)))
                .andExpect(jsonPath("$.method", is("CREDIT_CARD")))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    @DisplayName("Test2: Get payment by non-existent order ID returns not found")
    void getPaymentByOrderId_nonExistent() throws Exception {
        mockMvc.perform(get("/api/payments/{orderId}", 999999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test3: Process payment success")
    void processPayment_success() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(1L);
        paymentRequest.setOrderId(1L);
        paymentRequest.setAmount(100.0);
        paymentRequest.setPaymentMethod("CREDIT_CARD");

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(100.0)))
                .andExpect(jsonPath("$.method", is("CREDIT_CARD")))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    @DisplayName("Test4: Process payment with non-existent user fails")
    void processPayment_userNotFound() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(999999L);
        paymentRequest.setOrderId(1L);
        paymentRequest.setAmount(100.0);
        paymentRequest.setPaymentMethod("CREDIT_CARD");

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test5: Update payment success")
    void updatePayment_success() throws Exception {
        Payment updatedPayment = new Payment();
        updatedPayment.setId(1L);
        updatedPayment.setOrder(testOrder);
        updatedPayment.setAmount(150.0);
        updatedPayment.setMethod("PAYPAL");
        updatedPayment.setUser(testUser);
        updatedPayment.setCreatedAt(LocalDateTime.now());
        updatedPayment.setStatus(PaymentStatus.COMPLETED);

        when(paymentService.updatePayment(any())).thenReturn(updatedPayment);

        mockMvc.perform(put("/api/payments/{id}", 1L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedPayment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(150.0)))
                .andExpect(jsonPath("$.method", is("PAYPAL")))
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    @DisplayName("Test6: Update non-existent payment returns not found")
    void updatePayment_nonExistent() throws Exception {
        Payment updatedPayment = new Payment();
        updatedPayment.setId(999999L);
        updatedPayment.setOrder(testOrder);
        updatedPayment.setAmount(150.0);
        updatedPayment.setMethod("PAYPAL");
        updatedPayment.setUser(testUser);
        updatedPayment.setCreatedAt(LocalDateTime.now());
        updatedPayment.setStatus(PaymentStatus.COMPLETED);

        mockMvc.perform(put("/api/payments/999999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedPayment)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test7: Handle payment notification success")
    void handlePaymentNotification_success() throws Exception {
        PaymentNotification notification = new PaymentNotification();
        notification.setTransactionId("TX123");
        notification.setStatus("SUCCESS");

        mockMvc.perform(post("/api/payments/notify")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(notification)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test8: Unauthorized access to payment endpoints")
    void unauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isUnauthorized());
    }
}