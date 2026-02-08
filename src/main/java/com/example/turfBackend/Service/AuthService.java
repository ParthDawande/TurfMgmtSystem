package com.example.turfBackend.Service;

import com.example.turfBackend.DTOs.AuthResponse;
import com.example.turfBackend.DTOs.LoginRequest;
import com.example.turfBackend.DTOs.UserRequestDTO;
import com.example.turfBackend.Entity.ENUM.Role;
import com.example.turfBackend.Entity.User;
import com.example.turfBackend.Repository.UserRepository;
import com.example.turfBackend.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public String registerUser(UserRequestDTO request) {
        User user = new User();
        user.setAddress(request.getAddress());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return "User Registered Successfully";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if(!encoder.matches(request.getPassword(),user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(),user.getRole());
        return new AuthResponse(token);
    }

    public String registerOwner(UserRequestDTO request) {
        User owner = new User();
        owner.setAddress(request.getAddress());
        owner.setName(request.getName());
        owner.setEmail(request.getEmail());
        owner.setPhone(request.getPhone());
        owner.setPassword(encoder.encode(request.getPassword()));
        owner.setRole(Role.OWNER);

        userRepository.save(owner);

        return "Owner Registered Successfully";
    }

}
