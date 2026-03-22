package com.turfmanager;

/**
 * Shared constants for all API integration tests.
 * Values must match fixtures/seed.sql exactly.
 */
public final class TestConstants {

    private TestConstants() {}

    // Shared password (plain text) — BCrypt hash is in seed.sql
    public static final String TEST_PASSWORD = "Test@123";

    // --- Admin ---
    public static final Long ADMIN_ID = 1L;
    public static final String ADMIN_EMAIL = "admin@test.com";

    // --- Owner ---
    public static final Long OWNER_ID = 2L;
    public static final String OWNER_EMAIL = "owner@test.com";

    // --- Staff ---
    public static final Long STAFF_ID = 3L;
    public static final String STAFF_EMAIL = "staff@test.com";

    // --- Customer ---
    public static final Long CUSTOMER_ID = 4L;
    public static final String CUSTOMER_EMAIL = "customer@test.com";

    // --- Turf ---
    public static final Long TURF_ID = 1L;
    public static final String TURF_NAME = "Test Arena";

    // --- Slots (OPEN) ---
    public static final Long OPEN_SLOT_A_ID = 1L;
    public static final Long OPEN_SLOT_B_ID = 2L;
    public static final Long OPEN_SLOT_C_ID = 3L;

    // --- Slot (BOOKED) ---
    public static final Long BOOKED_SLOT_D_ID = 4L;

    // --- Booking ---
    public static final Long BOOKING_ID = 1L;

    // --- Payment ---
    public static final Long PAYMENT_ID = 1L;
    public static final String PAYMENT_NUMBER = "PAY-TEST-0001";
}
