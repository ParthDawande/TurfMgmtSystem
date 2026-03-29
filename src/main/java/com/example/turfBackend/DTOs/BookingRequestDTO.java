package com.example.turfBackend.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class BookingRequestDTO {
    private List<Long> slotIds;
}
