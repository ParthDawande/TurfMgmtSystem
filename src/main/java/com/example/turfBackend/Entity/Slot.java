package com.example.turfBackend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate slotDate;
    private Character slotType;
    private String status;

    @ManyToOne
    @JoinColumn(name = "turf_id")
    private Turf turf;
}
