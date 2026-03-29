package com.turfmanager.controller;

import com.turfmanager.dto.ComplaintResponseDTO;
import com.turfmanager.dto.StaffIdDTO;
import com.turfmanager.dto.UserRequestDTO;
import com.turfmanager.service.AdminService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;


    @PostMapping("/registerAdmin")
    public String registerAdmin(@RequestBody UserRequestDTO request){
        return adminService.registerAdmin(request);
    }

    @PostMapping("/registerStaff")
    public String registerStaff(@RequestBody UserRequestDTO request){
        return adminService.registerStaff(request);
    }

    @PutMapping("/approveTurf/{turfId}")
    public String approveTurf(@PathVariable Long turfId){
        return adminService.approveTurf(turfId);
    }

    @PutMapping("/assignComplaint/{complaintId}")
    public String assignComplaint(@PathVariable Long complaintId,@RequestBody StaffIdDTO staffId){
        return adminService.assignComplaint(complaintId,staffId);
    }

    @GetMapping("/getAllComplaints")
    public List<ComplaintResponseDTO> getAllComplaints(){
        return adminService.getAllComplaints();
    }

}
