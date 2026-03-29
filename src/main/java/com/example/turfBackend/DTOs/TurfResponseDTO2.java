package com.example.turfBackend.DTOs;

import lombok.Data;

@Data
public class TurfResponseDTO2 {
    private String turfName;
    private String location;
    private Double pricePerSlot;
    private String status;
}
