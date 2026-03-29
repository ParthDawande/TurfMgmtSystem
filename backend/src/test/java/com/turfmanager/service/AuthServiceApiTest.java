package com.turfmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.turfmanager.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static com.turfmanager.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Auth API Tests")
class AuthServiceApiTest extends BaseApiTest {

    // --- Rule 4: Self-registration for USER role ---
    @Test
    @DisplayName("POST /auth/registerUser — register a customer successfully")
    void registerUser_success() throws Exception {
        HttpResponse<String> response = doPost("/auth/registerUser", null, """
                {
                    "name": "New User",
                    "email": "newuser@test.com",
                    "password": "NewPass@123",
                    "phone": "9999999999",
                    "address": "New Address"
                }
                """);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("User Registered Successfully");
    }

    // --- Rule 4: Self-registration for OWNER role ---
    @Test
    @DisplayName("POST /auth/registerOwner — register an owner successfully")
    void registerOwner_success() throws Exception {
        HttpResponse<String> response = doPost("/auth/registerOwner", null, """
                {
                    "name": "New Owner",
                    "email": "newowner@test.com",
                    "password": "NewPass@123",
                    "phone": "9999999998",
                    "address": "Owner Address"
                }
                """);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Owner Registered Successfully");
    }

    // --- Rule 2, 3: Login returns JWT ---
    @Test
    @DisplayName("POST /auth/login — valid credentials return JWT token")
    void login_validCredentials_returnsToken() throws Exception {
        String body = "{\"email\":\"" + CUSTOMER_EMAIL + "\",\"password\":\"" + TEST_PASSWORD + "\"}";
        HttpResponse<String> response = doPost("/auth/login", null, body);

        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = parseJson(response);
        assertThat(json.get("token").asText()).isNotBlank();
    }

    // --- Rule 2: Wrong password ---
    @Test
    @DisplayName("POST /auth/login — wrong password returns error")
    void login_wrongPassword_fails() throws Exception {
        HttpResponse<String> response = doPost("/auth/login", null,
                "{\"email\":\"" + CUSTOMER_EMAIL + "\",\"password\":\"WrongPassword\"}");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText()).isEqualTo("Invalid credentials");
    }

    // --- Rule 2: Non-existent email ---
    @Test
    @DisplayName("POST /auth/login — non-existent email returns error")
    void login_nonExistentEmail_fails() throws Exception {
        HttpResponse<String> response = doPost("/auth/login", null,
                "{\"email\":\"nobody@test.com\",\"password\":\"" + TEST_PASSWORD + "\"}");

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = parseJson(response);
        assertThat(json.get("message").asText()).isEqualTo("Invalid credentials");
    }
}
