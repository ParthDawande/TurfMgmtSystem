package com.example.turfBackend.DTOs;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SlotResponseDTO {
    private LocalDate slotDate;
    private String startTime;
    private String endTime;
    private String status;
}
