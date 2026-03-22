package com.turfmanager.service;

import com.turfmanager.dto.ComplaintResponseDTO;
import com.turfmanager.dto.UserRequestDTO;
import com.turfmanager.model.Complaint;
import com.turfmanager.model.Role;
import com.turfmanager.model.User;
import com.turfmanager.repository.ComplaintRepository;
import com.turfmanager.repository.UserRepository;
import com.turfmanager.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final ComplaintRepository complaintRepository;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;

    public String resolveComplaint(Long complaintId) {
        String email = securityUtil.getCurrentUserEmail();
        User staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (staff.getRole() != Role.STAFF) {
            throw new RuntimeException("Only staff can resolve complaints");
        }

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getStaff() == null) {
            throw new RuntimeException("Complaint is not assigned to any staff");
        }

        if (!complaint.getStaff().getId().equals(staff.getId())) {
            throw new RuntimeException("You are not assigned to this complaint");
        }

        if (!"Working".equals(complaint.getStatus())) {
            throw new RuntimeException("Only working complaints can be resolved");
        }

        complaint.setStatus("Resolved");
        complaintRepository.save(complaint);

        return "Complaint successfully resolved";
    }

    public List<ComplaintResponseDTO> getMyComplaints() {
        String email = securityUtil.getCurrentUserEmail();
        User staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Complaint> complaints = complaintRepository.findByStaffId(staff.getId());
        List<ComplaintResponseDTO> responseDTOS = new ArrayList<>();
        for(int i=0;i<complaints.size();i++){
            Complaint complaint = complaints.get(i);
            ComplaintResponseDTO complaintResponseDTO = new ComplaintResponseDTO();
            complaintResponseDTO.setStatus(complaint.getStatus());
            complaintResponseDTO.setId(complaint.getId());
            complaintResponseDTO.setTitle(complaint.getTitle());
            complaintResponseDTO.setDescription(complaint.getDescription());
            complaintResponseDTO.setStaffName(complaint.getStaff().getName());
            complaintResponseDTO.setBookingId(complaint.getBooking().getId());
            complaintResponseDTO.setUserName(complaint.getUser().getName());
            complaintResponseDTO.setPhone(complaint.getUser().getPhone());
            responseDTOS.add(complaintResponseDTO);
        }
        return responseDTOS;
    }
}
