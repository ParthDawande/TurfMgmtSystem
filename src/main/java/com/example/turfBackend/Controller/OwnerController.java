package com.example.turfBackend.Controller;

import com.example.turfBackend.DTOs.SlotRequest;
import com.example.turfBackend.DTOs.TurfRequestDTO;
import com.example.turfBackend.DTOs.TurfResponseDTO;
import com.example.turfBackend.DTOs.TurfResponseDTO2;
import com.example.turfBackend.Service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping("/requestTurf")
    public String registerTurf(@RequestBody TurfRequestDTO request){
        return ownerService.registerTurf(request);
    }

    @GetMapping("/getMyTurfs")
    public List<TurfResponseDTO2> getMyTurfs(){
        return ownerService.getMyTurfs();
    }

    @PostMapping("/createSlots/{turfId}")
    public String createSlots(@RequestBody SlotRequest slots, @PathVariable Long turfId){
        return ownerService.createSlots(slots,turfId);
    }




}
