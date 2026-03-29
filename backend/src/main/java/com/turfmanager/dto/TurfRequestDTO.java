package com.turfmanager.dto;

import lombok.Data;

@Data
public class TurfRequestDTO {
    private String name;
    private String location;
    private Double pricePerSlot;
}
