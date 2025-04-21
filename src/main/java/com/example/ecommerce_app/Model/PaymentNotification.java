package com.example.ecommerce_app.Model;

public class PaymentNotification {
    private String transactionId;
    private String status;
    private Double amount;
    private Long orderId;
    private Long userId;

    public PaymentNotification() {
    }

    public PaymentNotification(String transactionId, String status, Double amount, Long orderId, Long userId) {
        this.transactionId = transactionId;
        this.status = status;
        this.amount = amount;
        this.orderId = orderId;
        this.userId = userId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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
}
