package com.example.turfBackend.Controller;

import com.example.turfBackend.DTOs.*;
import com.example.turfBackend.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/createBookings")
    public String createBookings(@RequestBody BookingRequestDTO request){
        return userService.createBookings(request);
    }

    @DeleteMapping("/deleteBooking/{bookingId}")
    public String deleteBooking(@PathVariable Long bookingId){
        return userService.deleteBooking(bookingId);
    }

    @GetMapping("/getMyBookings")
    public List<BookingResponseDTO> getMyBookings(){
        return userService.getMyBookings();
    }

    @PostMapping("/postComplaint")
    public String postComplaint(@RequestBody ComplaintRequestDTO request){
        return userService.postComplaint(request);
    }

    @GetMapping("/getAllTurfs")
    public List<TurfResponseDTO> getAllTurfs(){
        return userService.getAllTurfs();
    }

    @GetMapping("/getSlots/{turfId}")
    public List<SlotResponseDTO> getSlots(@PathVariable Long turfId){
        return userService.getSlots(turfId);
    }


}
