package com.example.ecommerce_app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthResponse {
    private String token;
    private UserAuthResponse user;
    private Long expiresAt;
}
