package com.turfmanager.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SlotRequest {
    private LocalDate date;
    private String slots;
}
