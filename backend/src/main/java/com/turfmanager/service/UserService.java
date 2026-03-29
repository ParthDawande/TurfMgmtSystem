package com.turfmanager.service;

import com.turfmanager.dto.*;
import com.turfmanager.model.*;
import com.turfmanager.repository.*;
import com.turfmanager.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final SlotRepository slotRepository;
    private final TurfRepository turfRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ComplaintRepository complaintRepository;

    @Transactional
    public String createBookings(BookingRequestDTO request) {

        String email = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Payment payment = new Payment();
        payment.setPaymentNumber(UUID.randomUUID().toString());

        double totalAmount = 0;
        Turf turf = null;

        for (Long slotId : request.getSlotIds()) {

            Slot slot = slotRepository.findById(slotId)
                    .orElseThrow(() -> new RuntimeException("Slot not found"));

            if ("BOOKED".equals(slot.getStatus())) {
                throw new RuntimeException("Slot already booked");
            }

            slot.setStatus("BOOKED");

            Booking booking = new Booking();
            booking.setUser(user);
            booking.setSlot(slot);
            booking.setPayment(payment);

            payment.getBookings().add(booking);

            turf = slot.getTurf();
            totalAmount += turf.getPricePerSlot();
        }

        payment.setPricePerSlot(turf.getPricePerSlot());
        payment.setTotalAmount(totalAmount);
        paymentRepository.save(payment);

        return "Booking successfully done";
    }


    public List<BookingResponseDTO> getMyBookings() {
        String email = securityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Booking> bookings = bookingRepository.findAllByUserId(user.getId());
        List<BookingResponseDTO> bookingResponseDTOS = new ArrayList<>();
        for(int i=0;i<bookings.size();i++){
            BookingResponseDTO bookingResponseDTO = new BookingResponseDTO();
            bookingResponseDTO.setStatus(bookings.get(i).getSlot().getStatus());
            bookingResponseDTO.setPaymentNumber(bookings.get(i).getPayment().getPaymentNumber());
            bookingResponseDTO.setTotalAmount(bookings.get(i).getPayment().getTotalAmount());
            bookingResponseDTO.setPricePerSlot(bookings.get(i).getPayment().getPricePerSlot());
            bookingResponseDTO.setSlotType(bookings.get(i).getSlot().getSlotType());
            bookingResponseDTO.setSlotDate(bookings.get(i).getSlot().getSlotDate());
            bookingResponseDTO.setTurfName(bookings.get(i).getSlot().getTurf().getName());
            bookingResponseDTOS.add(bookingResponseDTO);
        }
        return bookingResponseDTOS;
    }

    @Transactional
    public String deleteBooking(Long bookingId) {
        String email = securityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if(!booking.getUser().getId().equals(currentUser.getId())){
            throw new RuntimeException("You are not allowed to cancel this booking");
        }

        Slot slot = booking.getSlot();

        LocalDate slotDate = slot.getSlotDate();
        char slotCode = slot.getSlotType();

        int startHour = 6 + (slotCode - 'A');

        LocalDateTime slotStartDateTime =
                slotDate.atTime(startHour, 0);

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(slotStartDateTime.minusHours(24))) {
            throw new RuntimeException(
                    "Booking can only be cancelled 24 hours before slot time"
            );
        }

        slot.setStatus("OPEN");

        Payment payment = booking.getPayment();
        payment.setTotalAmount(payment.getTotalAmount()- payment.getPricePerSlot());

        bookingRepository.delete(booking);

        return "Booking cancelled successfully.Refund:-"+payment.getPricePerSlot();
    }

    public String postComplaint(ComplaintRequestDTO request) {
        String email = securityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if(!booking.getUser().getId().equals(currentUser.getId())){
            throw new RuntimeException("You are not allowed to post complaint for this booking");
        }

        Complaint complaint = new Complaint();
        complaint.setUser(currentUser);
        complaint.setStatus("Pending");
        complaint.setBooking(booking);
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());

        complaintRepository.save(complaint);

        return "Complaint successfully posted";
    }

    public List<TurfResponseDTO> getAllTurfs() {
        List<TurfResponseDTO> responseDTOS = new ArrayList<>();
        List<Turf> turfs = turfRepository.findAll();
        for(int i=0;i<turfs.size();i++){
            Turf turf = turfs.get(i);
            if(turf.getStatus().equals("APPROVED")){
                TurfResponseDTO responseDTO = new TurfResponseDTO();
                responseDTO.setTurfName(turf.getName());
                responseDTO.setLocation(turf.getLocation());
                responseDTO.setPricePerSlot(turf.getPricePerSlot());
                responseDTO.setOwnerName(turf.getOwner().getName());
                responseDTOS.add(responseDTO);
            }
        }
        return responseDTOS;
    }

    public List<SlotResponseDTO> getSlots(Long turfId) {
        Turf turf = turfRepository.findById(turfId).orElseThrow(() -> new RuntimeException("Turf not found"));
        List<SlotResponseDTO> slotResponseDTOS = new ArrayList<>();
        for(int i=0;i<turf.getSlots().size();i++){
            Slot slot = turf.getSlots().get(i);
            SlotResponseDTO responseDTO = new SlotResponseDTO();
            char slotType = Character.toUpperCase(slot.getSlotType()); // A–R

            int startHour = 6 + (slotType - 'A');
            int endHour = startHour + 1;

            responseDTO.setStartTime(String.format("%02d:00", startHour));
            responseDTO.setEndTime(String.format("%02d:00", endHour));
            responseDTO.setStatus(slot.getStatus());
            responseDTO.setSlotDate(slot.getSlotDate());
            slotResponseDTOS.add(responseDTO);
        }
        return slotResponseDTOS;
    }
}

