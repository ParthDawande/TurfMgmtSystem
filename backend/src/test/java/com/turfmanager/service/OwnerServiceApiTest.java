package com.turfmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.turfmanager.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.time.LocalDate;

import static com.turfmanager.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Owner API Tests")
class OwnerServiceApiTest extends BaseApiTest {

    // --- Rule 6: New turf starts as PENDING ---
    @Test
    @DisplayName("POST /owner/requestTurf — new turf is created with status PENDING")
    void registerTurf_createdAsPending() throws Exception {
        String token = loginAndGetToken(OWNER_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPost("/owner/requestTurf", token, """
                {
                    "name": "New Turf",
                    "location": "New Location",
                    "pricePerSlot": 700.00
                }
                """);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Turf successfully requested");

        // Verify it appears in owner's turfs with PENDING status
        HttpResponse<String> turfsResponse = doGet("/owner/getMyTurfs", token);
        JsonNode turfs = parseJson(turfsResponse);

        boolean foundPending = false;
        for (JsonNode turf : turfs) {
            if ("New Turf".equals(turf.get("turfName").asText())) {
                assertThat(turf.get("status").asText()).isEqualTo("PENDING");
                foundPending = true;
            }
        }
        assertThat(foundPending).isTrue();
    }

    // --- Rule 11, 13: Create slots on approved turf ---
    @Test
    @DisplayName("POST /owner/createSlots/{id} — create multiple slots on approved turf")
    void createSlots_approvedTurf_success() throws Exception {
        String token = loginAndGetToken(OWNER_EMAIL, TEST_PASSWORD);
        String futureDate = LocalDate.now().plusDays(10).toString();

        HttpResponse<String> response = doPost(
                "/owner/createSlots/" + TURF_ID, token,
                "{\"date\":\"" + futureDate + "\",\"slots\":\"EFG\"}");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Slots successfully created");
    }

    // --- Rule 13: Unapproved turf ---
    @Test
    @DisplayName("POST /owner/createSlots/{id} — slots on PENDING turf returns error")
    void createSlots_unapprovedTurf_fails() throws Exception {
        String token = loginAndGetToken(OWNER_EMAIL, TEST_PASSWORD);
        String futureDate = LocalDate.now().plusDays(10).toString();

        HttpResponse<String> response = doPost(
                "/owner/createSlots/" + PENDING_TURF_ID, token,
                "{\"date\":\"" + futureDate + "\",\"slots\":\"A\"}");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText())
                .isEqualTo("Slots can be created only for approved turfs");
    }

    // --- Rule 12: Not the turf owner ---
    @Test
    @DisplayName("POST /owner/createSlots/{id} — different owner cannot create slots")
    void createSlots_notOwner_fails() throws Exception {
        // Owner2 does NOT own turf 1
        String token = loginAndGetToken(OWNER2_EMAIL, TEST_PASSWORD);
        String futureDate = LocalDate.now().plusDays(10).toString();

        HttpResponse<String> response = doPost(
                "/owner/createSlots/" + TURF_ID, token,
                "{\"date\":\"" + futureDate + "\",\"slots\":\"A\"}");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText())
                .isEqualTo("You are not allowed to create slots for this turf");
    }

    // --- Rule 14: Duplicate slot ---
    @Test
    @DisplayName("POST /owner/createSlots/{id} — duplicate (turf, date, slotType) returns error")
    void createSlots_duplicateSlot_fails() throws Exception {
        String token = loginAndGetToken(OWNER_EMAIL, TEST_PASSWORD);
        // Slot 'A' already exists for +7 days in seed data
        String existingDate = LocalDate.now().plusDays(7).toString();

        HttpResponse<String> response = doPost(
                "/owner/createSlots/" + TURF_ID, token,
                "{\"date\":\"" + existingDate + "\",\"slots\":\"A\"}");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText()).contains("already exists");
    }

    // --- Rule 10: Invalid slot type ---
    @Test
    @DisplayName("POST /owner/createSlots/{id} — slot type outside A-R returns error")
    void createSlots_invalidSlotType_fails() throws Exception {
        String token = loginAndGetToken(OWNER_EMAIL, TEST_PASSWORD);
        String futureDate = LocalDate.now().plusDays(10).toString();

        HttpResponse<String> response = doPost(
                "/owner/createSlots/" + TURF_ID, token,
                "{\"date\":\"" + futureDate + "\",\"slots\":\"Z\"}");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText()).contains("Invalid slot type");
    }

    // --- Rule 9: Owner sees only own turfs ---
    @Test
    @DisplayName("GET /owner/getMyTurfs — returns only the authenticated owner's turfs")
    void getMyTurfs_returnsOnlyOwnTurfs() throws Exception {
        // Owner 1 owns both Test Arena and Pending Arena
        String token = loginAndGetToken(OWNER_EMAIL, TEST_PASSWORD);
        HttpResponse<String> response = doGet("/owner/getMyTurfs", token);

        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode turfs = parseJson(response);
        assertThat(turfs.size()).isEqualTo(2);

        // Owner 2 owns no turfs
        String token2 = loginAndGetToken(OWNER2_EMAIL, TEST_PASSWORD);
        HttpResponse<String> response2 = doGet("/owner/getMyTurfs", token2);

        assertThat(response2.statusCode()).isEqualTo(200);
        JsonNode turfs2 = parseJson(response2);
        assertThat(turfs2.size()).isEqualTo(0);
    }
}
