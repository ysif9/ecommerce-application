package com.example.ecommerce_app.DTO;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
}
