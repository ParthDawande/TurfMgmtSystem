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

    // --- Owner 2 (for negative ownership tests) ---
    public static final Long OWNER2_ID = 5L;
    public static final String OWNER2_EMAIL = "owner2@test.com";

    // --- Staff 2 (for negative assignment tests) ---
    public static final Long STAFF2_ID = 6L;
    public static final String STAFF2_EMAIL = "staff2@test.com";

    // --- Turfs ---
    public static final Long TURF_ID = 1L;
    public static final String TURF_NAME = "Test Arena";
    public static final Long PENDING_TURF_ID = 2L;

    // --- Slots (OPEN, 7 days out) ---
    public static final Long OPEN_SLOT_A_ID = 1L;
    public static final Long OPEN_SLOT_B_ID = 2L;
    public static final Long OPEN_SLOT_C_ID = 3L;

    // --- Slot (BOOKED, 7 days out — cancellable) ---
    public static final Long BOOKED_SLOT_D_ID = 4L;

    // --- Slot (BOOKED, today — NOT cancellable) ---
    public static final Long NON_CANCELLABLE_SLOT_ID = 5L;

    // --- Slot (BOOKED, 7 days out — cancellable, no complaints) ---
    public static final Long CANCELLABLE_SLOT_E_ID = 6L;

    // --- Bookings ---
    public static final Long BOOKING_ID = 1L;                  // has complaints attached
    public static final Long NON_CANCELLABLE_BOOKING_ID = 2L;  // not cancellable (slot today)
    public static final Long CANCELLABLE_BOOKING_ID = 3L;      // cancellable, no complaints

    // --- Payments ---
    public static final Long PAYMENT_ID = 1L;
    public static final String PAYMENT_NUMBER = "PAY-TEST-0001";

    // --- Complaints ---
    public static final Long PENDING_COMPLAINT_ID = 1L;   // unassigned, status=Pending
    public static final Long WORKING_COMPLAINT_ID = 2L;    // assigned to STAFF, status=Working
}
