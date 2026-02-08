package com.example.turfBackend.DTOs;

import lombok.Data;

@Data
public class TurfRequestDTO {
    private String name;
    private String location;
    private Double pricePerSlot;
}
