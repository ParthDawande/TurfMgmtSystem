package com.example.turfBackend.DTOs;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
}
