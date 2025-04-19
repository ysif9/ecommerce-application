package com.example.ecommerce_app.DTO;

public record UserAuthResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String role
)
{}
