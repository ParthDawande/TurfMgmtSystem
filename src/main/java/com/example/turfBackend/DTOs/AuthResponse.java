package com.example.turfBackend.DTOs;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data

public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
