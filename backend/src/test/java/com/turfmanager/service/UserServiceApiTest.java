package com.turfmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.turfmanager.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static com.turfmanager.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User/Customer API Tests")
class UserServiceApiTest extends BaseApiTest {

    // --- Rule 8: Only APPROVED turfs visible ---
    @Test
    @DisplayName("GET /user/getAllTurfs — returns only approved turfs")
    void getAllTurfs_returnsOnlyApproved() throws Exception {
        String token = loginAndGetToken(CUSTOMER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doGet("/user/getAllTurfs", token);

        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = parseJson(response);
        // Seed has 1 APPROVED + 1 PENDING — only APPROVED should be returned
        assertThat(json.size()).isEqualTo(1);
        assertThat(json.get(0).get("turfName").asText()).isEqualTo("Test Arena");
    }

    // --- Rule 10: Slot type A-R maps to 06:00-00:00 ---
    @Test
    @DisplayName("GET /user/getSlots/{turfId} — returns mapped time windows")
    void getSlots_returnsMappedTimeWindows() throws Exception {
        String token = loginAndGetToken(CUSTOMER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doGet("/user/getSlots/" + TURF_ID, token);

        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode slots = parseJson(response);
        assertThat(slots.size()).isGreaterThanOrEqualTo(4);

        // Find slot A (06:00-07:00)
        boolean foundSlotA = false;
        for (JsonNode slot : slots) {
            if ("06:00".equals(slot.get("startTime").asText())) {
                assertThat(slot.get("endTime").asText()).isEqualTo("07:00");
                foundSlotA = true;
            }
        }
        assertThat(foundSlotA).isTrue();
    }

    // --- Rule 10: Invalid turf ---
    @Test
    @DisplayName("GET /user/getSlots/{turfId} — non-existent turf returns error")
    void getSlots_invalidTurf_fails() throws Exception {
        String token = loginAndGetToken(CUSTOMER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doGet("/user/getSlots/9999", token);

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText()).isEqualTo("Turf not found");
    }

    // --- Rule 16, 18: Single slot booking ---
    @Test
    @DisplayName("POST /user/createBookings — book single OPEN slot successfully")
    void createBooking_singleSlot_success() throws Exception {
        String token = loginAndGetToken(CUSTOMER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPost("/user/createBookings", token,
                "{\"slotIds\":[" + OPEN_SLOT_A_ID + "]}");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Booking successfully done");
    }

    // --- Rule 16, 18: Multi-slot booking with payment ---
    @Test
    @DisplayName("POST /user/createBookings — book multiple OPEN slots, totalAmount = sum")
    void createBooking_multipleSlots_success() throws Exception {
        String token = loginAndGetToken(CUSTOMER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPost("/user/createBookings", token,
                "{\"slotIds\":[" + OPEN_SLOT_B_ID + "," + OPEN_SLOT_C_ID + "]}");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Booking successfully done");
    }

    // --- Rule 17: Already booked slot ---
    @Test
    @DisplayName("POST /user/createBookings — already BOOKED slot returns error")
    void createBooking_alreadyBookedSlot_fails() throws Exception {
        String token = loginAndGetToken(CUSTOMER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPost("/user/createBookings", token,
                "{\"slotIds\":[" + BOOKED_SLOT_D_ID + "]}");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText()).isEqualTo("Slot already booked");
    }

    // --- Rule 19: Own bookings only ---
    @Test
    @DisplayName("GET /user/getMyBookings — returns only the authenticated user's bookings")
    void getMyBookings_returnsOnlyOwnBookings() throws Exception {
        String token = loginAndGetToken(CUSTOMER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doGet("/user/getMyBookings", token);

        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode bookings = parseJson(response);
        // Customer has 3 bookings in seed data
        assertThat(bookings.size()).isEqualTo(3);
        for (JsonNode booking : bookings) {
            assertThat(booking.get("turfName").asText()).isEqualTo("Test Arena");
        }
    }

    // --- Rule 20, 21, 22: Cancel >24h before ---
    @Test
    @DisplayName("DELETE /user/deleteBooking/{id} — cancel >24h before slot, slot reverts to OPEN")
    void cancelBooking_moreThan24hBefore_success() throws Exception {
        String token = loginAndGetToken(CUSTOMER_EMAIL, TEST_PASSWORD);

        // Booking 3 is for a slot 7 days from now, has no complaints — safe to cancel
        HttpResponse<String> response = doDelete("/user/deleteBooking/" + CANCELLABLE_BOOKING_ID, token);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Booking cancelled successfully");
    }

    // --- Rule 20: Cancel <24h before ---
    @Test
    @DisplayName("DELETE /user/deleteBooking/{id} — cancel <24h before slot returns error")
    void cancelBooking_lessThan24hBefore_fails() throws Exception {
        String token = loginAndGetToken(CUSTOMER_EMAIL, TEST_PASSWORD);

        // Booking 2 is for a slot today — within the 24h restriction
        HttpResponse<String> response = doDelete(
                "/user/deleteBooking/" + NON_CANCELLABLE_BOOKING_ID, token);

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText()).contains("24 hours before slot time");
    }

    // --- Rule 19: Not booking owner ---
    @Test
    @DisplayName("DELETE /user/deleteBooking/{id} — different user cannot cancel")
    void cancelBooking_notOwner_fails() throws Exception {
        // Owner is authenticated but does not own booking 1
        String token = loginAndGetToken(OWNER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doDelete("/user/deleteBooking/" + BOOKING_ID, token);

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText())
                .isEqualTo("You are not allowed to cancel this booking");
    }

    // --- Rule 23, 24: File complaint ---
    @Test
    @DisplayName("POST /user/postComplaint — file complaint against own booking, status=Pending")
    void postComplaint_success() throws Exception {
        String token = loginAndGetToken(CUSTOMER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPost("/user/postComplaint", token,
                """
                {
                    "title": "Broken floodlight",
                    "description": "One of the floodlights was not working during my session.",
                    "bookingId": %d
                }
                """.formatted(BOOKING_ID));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Complaint successfully posted");
    }

    // --- Rule 23: Not booking owner ---
    @Test
    @DisplayName("POST /user/postComplaint — complaint against another user's booking returns error")
    void postComplaint_notBookingOwner_fails() throws Exception {
        // Owner is authenticated but does not own booking 1
        String token = loginAndGetToken(OWNER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPost("/user/postComplaint", token,
                """
                {
                    "title": "Fake complaint",
                    "description": "This should be rejected.",
                    "bookingId": %d
                }
                """.formatted(BOOKING_ID));

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText())
                .isEqualTo("You are not allowed to post complaint for this booking");
    }
}
