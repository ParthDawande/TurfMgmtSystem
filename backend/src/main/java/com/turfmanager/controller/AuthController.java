package com.turfmanager.controller;

import com.turfmanager.dto.AuthResponse;
import com.turfmanager.dto.LoginRequest;
import com.turfmanager.dto.UserRequestDTO;
import com.turfmanager.service.AdminService;
import com.turfmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AdminService adminService;

    @PostMapping("/registerUser")
    public String registerUser(@RequestBody UserRequestDTO request){
        return authService.registerUser(request);
    }

    @PostMapping("/registerOwner")
    public String registerOwner(@RequestBody UserRequestDTO request){
        return authService.registerOwner(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

}
