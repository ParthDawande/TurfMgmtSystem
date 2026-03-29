package com.example.turfBackend.Controller;

import com.example.turfBackend.DTOs.ComplaintResponseDTO;
import com.example.turfBackend.DTOs.StaffIdDTO;
import com.example.turfBackend.DTOs.UserRequestDTO;
import com.example.turfBackend.Service.AdminService;

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
