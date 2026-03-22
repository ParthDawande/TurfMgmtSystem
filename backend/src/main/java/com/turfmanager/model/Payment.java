package com.turfmanager.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String paymentNumber;

    private double totalAmount;

    private double pricePerSlot;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();
}
