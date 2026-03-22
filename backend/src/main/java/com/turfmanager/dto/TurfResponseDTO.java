package com.turfmanager.dto;

import lombok.Data;

@Data
public class TurfResponseDTO {
    private String turfName;
    private String location;
    private Double pricePerSlot;
    private String ownerName;
}
