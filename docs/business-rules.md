
---
## Data Model

| Entity        | Key Fields                                              | Relationships                                                                     |
| ------------- | ------------------------------------------------------- | --------------------------------------------------------------------------------- |
| **User**      | name, email, password, phone, address, role             | Owner of Turfs; makes Bookings; files Complaints; assigned as Staff on Complaints |
| **Turf**      | name, location, pricePerSlot, status                    | Belongs to one Owner (User); has many Slots                                       |
| **Slot**      | slotDate, slotType (char A–R), status                   | Belongs to one Turf                                                               |
| **Booking**   | —                                                       | Links one User + one Slot + one Payment                                           |
| **Payment**   | paymentNumber (UUID, unique), totalAmount, pricePerSlot | Has many Bookings (cascade ALL)                                                   |
| **Complaint** | title, description, status                              | Filed by a User, against a Booking, optionally assigned to a Staff (User)         |
| **Role**      | enum: `ADMIN`, `OWNER`, `USER`, `STAFF`                 | —                                                                                 |

---

## Authentication & Registration Rules

1. **Passwords** are BCrypt-encoded before storage — never stored in plain text.
2. **Login** looks up user by email; if not found or password doesn't match, throws `"Invalid credentials"`.
3. **JWT token** is generated containing the user's email and role, with a **1-hour expiry**.
4. **Self-registration** is available for `USER` and `OWNER` roles only (via public `/auth/` endpoints).
5. **Admin and Staff registration** is restricted to the `ADMIN` role (via `/admin/` endpoints).

---

## Turf Management Rules

6. **Turf creation** — An owner submits a turf request. The turf is created with status `"PENDING"` and linked to the authenticated owner.
7. **Turf approval** — Only an Admin can approve a turf (sets status to `"APPROVED"`).
8. **Turf visibility** — Customers can only see turfs with status `"APPROVED"`.
9. **Owner scoping** — Owners can only view their own turfs (filtered by `owner_id`).

---

## Slot Rules

10. **Slot types** are encoded as characters `A` through `R` (18 possible slots), each representing a 1-hour window starting at **06:00**. Slot `A` = 06:00–07:00, `B` = 07:00–08:00, ..., `R` = 23:00–00:00.
11. **Slot creation** is transactional (`@Transactional`). Multiple slots can be created in one request by passing a string of slot characters (e.g., `"ABCDE"`).
12. **Only the turf owner** can create slots for their turf — ownership is verified by comparing `turf.owner.id` to the authenticated user's ID.
13. **Slots can only be created for approved turfs** — status must be `"APPROVED"`.
14. **No duplicate slots** — a slot with the same `(turfId, slotDate, slotType)` cannot exist twice. Checked via `existsByTurfIdAndSlotDateAndSlotType`.
15. **Slot status** starts as `"OPEN"` and transitions to `"BOOKED"` when a booking is made; reverts to `"OPEN"` on cancellation.

---

## Booking Rules

16. **Booking is transactional** (`@Transactional`). A user can book multiple slots in a single request.
17. **A slot that is already `"BOOKED"` cannot be booked again** — checked before each slot in the batch.
18. **One Payment per booking request** — a single `Payment` record with a UUID `paymentNumber` groups all bookings in the request. `totalAmount` = sum of `pricePerSlot` for each slot booked.
19. **Only the booking owner can cancel** — verified by comparing `booking.user.id` to the authenticated user.
20. **24-hour cancellation policy** — a booking can only be cancelled if `now` is more than 24 hours before the slot's start time. Slot start time is derived from the slot code: `startHour = 6 + (slotCode - 'A')`.
21. **On cancellation**: slot status reverts to `"OPEN"`, payment `totalAmount` is reduced by `pricePerSlot`, and the booking record is **hard deleted**.
22. **Refund amount** = `pricePerSlot` (returned in the response message).

---

## Complaint Rules

23. **Only the booking owner** can file a complaint against that booking.
24. **Complaint initial status** is `"Pending"`.
25. **Admin assigns complaints to staff** — sets the `staff` field and changes status to `"Working"`. The assigned user must have role `STAFF`.
26. **Only the assigned staff member can resolve** a complaint — verified by comparing `complaint.staff.id` to authenticated user.
27. **Only complaints with status `"Working"` can be resolved** — resolved status becomes `"Resolved"`.
28. **Complaint cannot be resolved if unassigned** — `complaint.staff` must not be null.

---

## Complaint Status Lifecycle

```
Pending  →  Working  →  Resolved
  (filed)    (admin      (staff
              assigns)    resolves)
```

## Slot Status Lifecycle

```
OPEN  →  BOOKED  →  OPEN
 (created)  (user     (user
             books)    cancels)
```

## Turf Status Lifecycle

```
PENDING  →  APPROVED
 (owner      (admin
  requests)   approves)
```