package com.turfmanager.service;

import com.turfmanager.dto.ComplaintResponseDTO;
import com.turfmanager.dto.StaffIdDTO;
import com.turfmanager.dto.UserRequestDTO;
import com.turfmanager.model.Complaint;
import com.turfmanager.model.Role;
import com.turfmanager.model.Turf;
import com.turfmanager.model.User;
import com.turfmanager.repository.ComplaintRepository;
import com.turfmanager.repository.TurfRepository;
import com.turfmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final TurfRepository turfRepository;
    private final PasswordEncoder encoder;
    private final ComplaintRepository complaintRepository;

    public String registerAdmin(UserRequestDTO request) {
        User admin = new User();
        admin.setAddress(request.getAddress());
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPhone(request.getPhone());
        admin.setPassword(encoder.encode(request.getPassword()));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);

        return "Admin Registered Successfully";
    }

    public String approveTurf(Long turfId) {
        Turf turf = turfRepository.findById(turfId).orElseThrow();
        turf.setStatus("APPROVED");
        turfRepository.save(turf);
        return "Turf Approved successfully.";
    }

    public String registerStaff(UserRequestDTO request) {
        User staff = new User();
        staff.setRole(Role.STAFF);
        staff.setName(request.getName());
        staff.setAddress(request.getAddress());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        staff.setPassword(encoder.encode(request.getPassword()));

        userRepository.save(staff);

        return "Staff registered successfully";
    }


    public String assignComplaint(Long complaintId, StaffIdDTO staffId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User staff = userRepository.findById(staffId.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        if (staff.getRole() != Role.STAFF) {
            throw new RuntimeException("Selected user is not a staff member");
        }
        complaint.setStaff(staff);
        complaint.setStatus("Working");
        complaintRepository.save(complaint);
        return "Complaint assigned successfully";
    }

    public List<ComplaintResponseDTO> getAllComplaints() {
        List<ComplaintResponseDTO> responseDTOS = new ArrayList<>();
        List<Complaint> complaints = complaintRepository.findAll();
        for(int i=0;i<complaints.size();i++){
            Complaint complaint = complaints.get(i);
            ComplaintResponseDTO complaintResponseDTO = new ComplaintResponseDTO();
            complaintResponseDTO.setStatus(complaint.getStatus());
            complaintResponseDTO.setId(complaint.getId());
            complaintResponseDTO.setTitle(complaint.getTitle());
            complaintResponseDTO.setDescription(complaint.getDescription());
            if(complaint.getStaff()==null){
                complaintResponseDTO.setStaffName("Not assigned to anyone");
            }
            else{
                complaintResponseDTO.setStaffName(complaint.getStaff().getName());
            }
            complaintResponseDTO.setBookingId(complaint.getBooking().getId());
            complaintResponseDTO.setUserName(complaint.getUser().getName());
            complaintResponseDTO.setPhone(complaint.getUser().getPhone());
            responseDTOS.add(complaintResponseDTO);
        }
        return responseDTOS;
    }
}
