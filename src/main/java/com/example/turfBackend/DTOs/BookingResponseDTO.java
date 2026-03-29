package com.example.turfBackend.DTOs;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingResponseDTO {
    private LocalDate slotDate;
    private Character slotType;
    private String status;
    private String turfName;
    private String paymentNumber;
    private double totalAmount;
    private double pricePerSlot;
}
