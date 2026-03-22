package com.turfmanager.dto;

import lombok.Data;

@Data
public class ComplaintResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String userName;
    private String phone;
    private String staffName;
    private Long bookingId;
}
