package com.example.turfBackend.Entity;

import com.example.turfBackend.Entity.ENUM.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

}
