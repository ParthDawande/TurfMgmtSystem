package com.example.turfBackend.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "staff_id",nullable = true)
    private User staff;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
