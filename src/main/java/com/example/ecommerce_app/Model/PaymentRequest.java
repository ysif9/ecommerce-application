package com.example.ecommerce_app.Model;

public class PaymentRequest {
    private String method;
    private Double amount;
    private String transactionId;
    private Long orderId;
    private Long userId;
    private String paymentMethod;

    public PaymentRequest() {
    }

    public PaymentRequest(String method, Double amount, String transactionId, Long orderId, Long userId) {
        this.method = method;
        this.amount = amount;
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.userId = userId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalUser getLocalUser() {
        return new LocalUser(null, null, null, null, null, null, null, null, null);
    }  
    
}
