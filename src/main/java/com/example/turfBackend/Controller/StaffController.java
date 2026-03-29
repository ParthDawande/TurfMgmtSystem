package com.example.turfBackend.Controller;

import com.example.turfBackend.DTOs.ComplaintResponseDTO;
import com.example.turfBackend.Service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PutMapping("/resolveComplaint/{complaintId}")
    public String resolveComplaint(@PathVariable Long complaintId){
        return staffService.resolveComplaint(complaintId);
    }

    @GetMapping("/getMyComplaints")
    public List<ComplaintResponseDTO> getMyComplaints(){
        return staffService.getMyComplaints();
    }
}
