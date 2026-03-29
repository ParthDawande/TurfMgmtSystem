-- Seed test data
-- All passwords are BCrypt hash of "Test@123"

-- Users: one per role + extra owner and staff for negative tests
INSERT INTO user (id, name, email, password, phone, address, role) VALUES
(1, 'Test Admin',    'admin@test.com',    '$2a$10$0R5nMMLer/5CHGwT2HOFau1GkPZ6jzhpiuRRVramIX0POArsMyKte', '9000000001', 'Admin Address',    'ADMIN'),
(2, 'Test Owner',    'owner@test.com',    '$2a$10$0R5nMMLer/5CHGwT2HOFau1GkPZ6jzhpiuRRVramIX0POArsMyKte', '9000000002', 'Owner Address',    'OWNER'),
(3, 'Test Staff',    'staff@test.com',    '$2a$10$0R5nMMLer/5CHGwT2HOFau1GkPZ6jzhpiuRRVramIX0POArsMyKte', '9000000003', 'Staff Address',    'STAFF'),
(4, 'Test Customer', 'customer@test.com', '$2a$10$0R5nMMLer/5CHGwT2HOFau1GkPZ6jzhpiuRRVramIX0POArsMyKte', '9000000004', 'Customer Address', 'USER'),
(5, 'Test Owner 2',  'owner2@test.com',   '$2a$10$0R5nMMLer/5CHGwT2HOFau1GkPZ6jzhpiuRRVramIX0POArsMyKte', '9000000005', 'Owner2 Address',   'OWNER'),
(6, 'Test Staff 2',  'staff2@test.com',   '$2a$10$0R5nMMLer/5CHGwT2HOFau1GkPZ6jzhpiuRRVramIX0POArsMyKte', '9000000006', 'Staff2 Address',   'STAFF');

-- Turfs: one approved, one pending (for negative slot-creation test)
INSERT INTO turf (id, name, location, price_per_slot, status, owner_id) VALUES
(1, 'Test Arena',    'Test Location',   500.00, 'APPROVED', 2),
(2, 'Pending Arena', 'Test Location 2', 600.00, 'PENDING',  2);

-- Open slots 7 days from now (cancellable — well beyond 24h window)
INSERT INTO slot (id, slot_date, slot_type, status, turf_id) VALUES
(1, DATEADD('DAY', 7, CURRENT_DATE), 'A', 'OPEN', 1),
(2, DATEADD('DAY', 7, CURRENT_DATE), 'B', 'OPEN', 1),
(3, DATEADD('DAY', 7, CURRENT_DATE), 'C', 'OPEN', 1);

-- Booked slot 7 days from now (cancellable booking for customer)
INSERT INTO slot (id, slot_date, slot_type, status, turf_id) VALUES
(4, DATEADD('DAY', 7, CURRENT_DATE), 'D', 'BOOKED', 1);

-- Booked slot TODAY (NOT cancellable — within 24h window)
INSERT INTO slot (id, slot_date, slot_type, status, turf_id) VALUES
(5, CURRENT_DATE, 'A', 'BOOKED', 1);

-- Booked slot 7 days from now — cancellable, with NO complaints referencing it
INSERT INTO slot (id, slot_date, slot_type, status, turf_id) VALUES
(6, DATEADD('DAY', 7, CURRENT_DATE), 'E', 'BOOKED', 1);

-- Payments
INSERT INTO payment (id, payment_number, total_amount, price_per_slot) VALUES
(1, 'PAY-TEST-0001', 500.00, 500.00),
(2, 'PAY-TEST-0002', 500.00, 500.00),
(3, 'PAY-TEST-0003', 500.00, 500.00);

-- Bookings
INSERT INTO booking (id, slot_id, user_id, payment_id) VALUES
(1, 4, 4, 1),
(2, 5, 4, 2),
(3, 6, 4, 3);

-- Complaints: one pending (unassigned), one assigned to staff (working)
INSERT INTO complaint (id, title, description, status, user_id, staff_id, booking_id) VALUES
(1, 'Poor ground condition',  'The turf had puddles and uneven patches during my session.', 'Pending', 4, NULL, 1),
(2, 'Late access to facility', 'Staff opened the gate 20 minutes after my slot started.',    'Working', 4, 3,    1);
