-- Seed test data
-- All passwords are BCrypt hash of "Test@123"

-- Users: one per role
INSERT INTO user (id, name, email, password, phone, address, role) VALUES
(1, 'Test Admin',    'admin@test.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9000000001', 'Admin Address',    'ADMIN'),
(2, 'Test Owner',    'owner@test.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9000000002', 'Owner Address',    'OWNER'),
(3, 'Test Staff',    'staff@test.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9000000003', 'Staff Address',    'STAFF'),
(4, 'Test Customer', 'customer@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9000000004', 'Customer Address', 'USER');

-- One approved turf owned by Test Owner
INSERT INTO turf (id, name, location, price_per_slot, status, owner_id) VALUES
(1, 'Test Arena', 'Test Location', 500.00, 'APPROVED', 2);

-- A few open slots for tomorrow (slot types A=06:00, B=07:00, C=08:00)
INSERT INTO slot (id, slot_date, slot_type, status, turf_id) VALUES
(1, CURDATE() + INTERVAL 7 DAY, 'A', 'OPEN', 1),
(2, CURDATE() + INTERVAL 7 DAY, 'B', 'OPEN', 1),
(3, CURDATE() + INTERVAL 7 DAY, 'C', 'OPEN', 1);

-- One booking + payment for the customer on a separate slot
INSERT INTO slot (id, slot_date, slot_type, status, turf_id) VALUES
(4, CURDATE() + INTERVAL 7 DAY, 'D', 'BOOKED', 1);

INSERT INTO payment (id, payment_number, total_amount, price_per_slot) VALUES
(1, 'PAY-TEST-0001', 500.00, 500.00);

INSERT INTO booking (id, slot_id, user_id, payment_id) VALUES
(1, 4, 4, 1);
