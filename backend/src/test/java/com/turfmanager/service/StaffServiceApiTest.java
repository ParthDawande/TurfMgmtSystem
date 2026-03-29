package com.turfmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.turfmanager.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static com.turfmanager.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Staff API Tests")
class StaffServiceApiTest extends BaseApiTest {

    // --- Rule 26, 27: Resolve assigned Working complaint ---
    @Test
    @DisplayName("PUT /staff/resolveComplaint/{id} — resolve own assigned Working complaint")
    void resolveComplaint_assigned_success() throws Exception {
        // Complaint 2 is assigned to Staff (id=3) with status=Working
        String token = loginAndGetToken(STAFF_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPut(
                "/staff/resolveComplaint/" + WORKING_COMPLAINT_ID, token, "");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Complaint successfully resolved");
    }

    // --- Rule 28: Unassigned complaint ---
    @Test
    @DisplayName("PUT /staff/resolveComplaint/{id} — unassigned complaint returns error")
    void resolveComplaint_unassigned_fails() throws Exception {
        // Complaint 1 is Pending with no staff assigned
        String token = loginAndGetToken(STAFF_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPut(
                "/staff/resolveComplaint/" + PENDING_COMPLAINT_ID, token, "");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText())
                .isEqualTo("Complaint is not assigned to any staff");
    }

    // --- Rule 26: Assigned to different staff ---
    @Test
    @DisplayName("PUT /staff/resolveComplaint/{id} — complaint assigned to other staff returns error")
    void resolveComplaint_assignedToOtherStaff_fails() throws Exception {
        // Complaint 2 is assigned to Staff 1, but Staff 2 tries to resolve
        String token = loginAndGetToken(STAFF2_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPut(
                "/staff/resolveComplaint/" + WORKING_COMPLAINT_ID, token, "");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText())
                .isEqualTo("You are not assigned to this complaint");
    }

    // --- Rule 27: Not in Working status ---
    @Test
    @DisplayName("PUT /staff/resolveComplaint/{id} — already-Resolved complaint returns error")
    void resolveComplaint_notWorkingStatus_fails() throws Exception {
        String token = loginAndGetToken(STAFF_EMAIL, TEST_PASSWORD);

        // First resolve complaint 2
        doPut("/staff/resolveComplaint/" + WORKING_COMPLAINT_ID, token, "");

        // Try to resolve again — now status is "Resolved", not "Working"
        HttpResponse<String> response = doPut(
                "/staff/resolveComplaint/" + WORKING_COMPLAINT_ID, token, "");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText())
                .isEqualTo("Only working complaints can be resolved");
    }

    // --- Rule 26: Staff sees only assigned complaints ---
    @Test
    @DisplayName("GET /staff/getMyComplaints — returns only complaints assigned to this staff")
    void getMyComplaints_returnsOnlyAssigned() throws Exception {
        // Staff 1 has complaint 2 assigned
        String token = loginAndGetToken(STAFF_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doGet("/staff/getMyComplaints", token);

        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode complaints = parseJson(response);
        assertThat(complaints.size()).isEqualTo(1);
        assertThat(complaints.get(0).get("title").asText()).isEqualTo("Late access to facility");
        assertThat(complaints.get(0).get("status").asText()).isEqualTo("Working");

        // Staff 2 has no complaints assigned
        String token2 = loginAndGetToken(STAFF2_EMAIL, TEST_PASSWORD);
        HttpResponse<String> response2 = doGet("/staff/getMyComplaints", token2);

        assertThat(response2.statusCode()).isEqualTo(200);
        JsonNode complaints2 = parseJson(response2);
        assertThat(complaints2.size()).isEqualTo(0);
    }
}
