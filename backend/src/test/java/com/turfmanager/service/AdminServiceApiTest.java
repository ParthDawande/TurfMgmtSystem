package com.turfmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.turfmanager.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static com.turfmanager.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Admin API Tests")
class AdminServiceApiTest extends BaseApiTest {

    // --- Rule 5: Admin registers another admin ---
    @Test
    @DisplayName("POST /admin/registerAdmin — admin creates another admin account")
    void registerAdmin_success() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPost("/admin/registerAdmin", token, """
                {
                    "name": "New Admin",
                    "email": "newadmin@test.com",
                    "password": "Admin@123",
                    "phone": "8000000001",
                    "address": "Admin2 Address"
                }
                """);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Admin Registered Successfully");
    }

    // --- Rule 5: Admin registers staff ---
    @Test
    @DisplayName("POST /admin/registerStaff — admin creates a staff account")
    void registerStaff_success() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPost("/admin/registerStaff", token, """
                {
                    "name": "New Staff",
                    "email": "newstaff@test.com",
                    "password": "Staff@123",
                    "phone": "8000000002",
                    "address": "NewStaff Address"
                }
                """);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Staff registered successfully");
    }

    // --- Rule 7: Approve pending turf ---
    @Test
    @DisplayName("PUT /admin/approveTurf/{id} — approve pending turf, status becomes APPROVED")
    void approveTurf_success() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPut("/admin/approveTurf/" + PENDING_TURF_ID, token, "");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Turf Approved successfully.");
    }

    // --- Rule 25: Assign complaint to staff ---
    @Test
    @DisplayName("PUT /admin/assignComplaint/{id} — assign to STAFF user, status becomes Working")
    void assignComplaint_toStaff_success() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPut(
                "/admin/assignComplaint/" + PENDING_COMPLAINT_ID, token,
                "{\"staffId\":" + STAFF_ID + "}");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Complaint assigned successfully");
    }

    // --- Rule 25: Assign to non-staff fails ---
    @Test
    @DisplayName("PUT /admin/assignComplaint/{id} — assign to non-STAFF user returns error")
    void assignComplaint_toNonStaff_fails() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doPut(
                "/admin/assignComplaint/" + PENDING_COMPLAINT_ID, token,
                "{\"staffId\":" + CUSTOMER_ID + "}");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText()).isEqualTo("Selected user is not a staff member");
    }

    // --- Rule 25: View all complaints ---
    @Test
    @DisplayName("GET /admin/getAllComplaints — returns all complaints with details")
    void getAllComplaints_returnsAll() throws Exception {
        String token = loginAndGetToken(ADMIN_EMAIL, TEST_PASSWORD);

        HttpResponse<String> response = doGet("/admin/getAllComplaints", token);

        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = parseJson(response);
        assertThat(json.isArray()).isTrue();
        assertThat(json.size()).isEqualTo(2);
        assertThat(json.get(0).get("title").asText()).isNotBlank();
        assertThat(json.get(0).get("status").asText()).isNotBlank();
        assertThat(json.get(0).get("userName").asText()).isNotBlank();
    }
}
