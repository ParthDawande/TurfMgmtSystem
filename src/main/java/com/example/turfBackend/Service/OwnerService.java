package com.example.turfBackend.Service;

import com.example.turfBackend.DTOs.SlotRequest;
import com.example.turfBackend.DTOs.TurfRequestDTO;
import com.example.turfBackend.DTOs.TurfResponseDTO2;
import com.example.turfBackend.Entity.Slot;
import com.example.turfBackend.Entity.Turf;
import com.example.turfBackend.Entity.User;
import com.example.turfBackend.Repository.SlotRepository;
import com.example.turfBackend.Repository.TurfRepository;
import com.example.turfBackend.Repository.UserRepository;
import com.example.turfBackend.Util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final TurfRepository turfRepository;
    private final UserRepository userRepository;
    private final SlotRepository slotRepository;
    private final SecurityUtil securityUtil;

    public String registerTurf(TurfRequestDTO request) {
        Turf turf = new Turf();
        String email = securityUtil.getCurrentUserEmail();
        User owner = userRepository.findByEmail(email).orElseThrow();

        turf.setOwner(owner);
        turf.setName(request.getName());
        turf.setLocation(request.getLocation());
        turf.setPricePerSlot(request.getPricePerSlot());
        turf.setStatus("PENDING");

        turfRepository.save(turf);

        return "Turf successfully requested.In waiting.";
    }

    @Transactional
    public String createSlots(SlotRequest slots, Long turfId) {
        LocalDate date = slots.getDate();
        String slotStr = slots.getSlots();

        String email = securityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Turf turf = turfRepository.findById(turfId)
                .orElseThrow(() -> new RuntimeException("Turf not found"));


        if (!turf.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to create slots for this turf");
        }


        if (!turf.getStatus().equals("APPROVED")) {
            throw new RuntimeException("Slots can be created only for approved turfs");
        }

        for(int i = 0;i<slotStr.length();i++){
            if (slotStr.charAt(i) < 'A' || slotStr.charAt(i) > 'R') {
                throw new RuntimeException("Invalid slot type: " + slotStr.charAt(i) + ". Allowed slots are a to r only");
            }

            boolean exists = slotRepository.existsByTurfIdAndSlotDateAndSlotType(turf.getId(), date, slotStr.charAt(i));
            if (exists) {
                throw new RuntimeException("Slot " + slotStr.charAt(i) + " already exists on " + date);
            }
            Slot slot = new Slot();
            slot.setSlotType(slotStr.charAt(i));
            slot.setSlotDate(date);
            slot.setStatus("OPEN");
            slot.setTurf(turf);
            slotRepository.save(slot);
        }
        return "Slots successfully created";
    }

    public List<TurfResponseDTO2> getMyTurfs() {
        String email = securityUtil.getCurrentUserEmail();
        User currentOwner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        List<Turf> myTurfs = turfRepository.getByOwnerId(currentOwner.getId());
        List<TurfResponseDTO2> responseDTO2s = new ArrayList<>();

        for(int i=0;i<myTurfs.size();i++){
            Turf turf = myTurfs.get(i);
            TurfResponseDTO2 responseDTO2 = new TurfResponseDTO2();
            responseDTO2.setLocation(turf.getLocation());
            responseDTO2.setStatus(turf.getStatus());
            responseDTO2.setTurfName(turf.getName());
            responseDTO2.setPricePerSlot(turf.getPricePerSlot());
            responseDTO2s.add(responseDTO2);
        }

        return responseDTO2s;
    }
}
