package com.example.ecommerce_app.Model;

public enum PaymentMethod {
    CREDIT_CARD("Credit Card", "Visa/MasterCard", "Details about credit card"),
    PAYPAL("PayPal", "PayPal Inc.", "Details about PayPal"),
    BANK_TRANSFER("Bank Transfer", "Local Bank", "Details about bank transfer");

    private final String methodName;
    private final String provider;
    private final String details;

    PaymentMethod(String methodName, String provider, String details) {
        this.methodName = methodName;
        this.provider = provider;
        this.details = details;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getProvider() {
        return provider;
    }

    public String getDetails() {
        return details;
    }

    public static PaymentMethod fromString(String method) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.name().equalsIgnoreCase(method)) {
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("Unknown payment method: " + method);
    }
}
