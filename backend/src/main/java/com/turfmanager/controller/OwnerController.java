package com.turfmanager.controller;

import com.turfmanager.dto.SlotRequest;
import com.turfmanager.dto.TurfRequestDTO;
import com.turfmanager.dto.TurfResponseDTO;
import com.turfmanager.dto.TurfResponseDTO2;
import com.turfmanager.service.OwnerService;
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
