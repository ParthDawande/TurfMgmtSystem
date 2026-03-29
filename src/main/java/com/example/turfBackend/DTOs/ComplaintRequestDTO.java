package com.example.turfBackend.DTOs;

import lombok.Data;

@Data
public class ComplaintRequestDTO {
    private String title;
    private String description;
    private Long bookingId;
}
