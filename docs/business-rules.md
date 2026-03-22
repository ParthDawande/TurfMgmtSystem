## Business Rules

### Booking

- Customers can only book AVAILABLE slots.
- A slot becomes BOOKED immediately upon confirmed booking (no holds/pending state in MVP).
- One booking per slot — no double booking under any circumstance.
- Booking is tied to the authenticated Customer's account.

### Cancellation

- Customers may cancel a booking up to **24 hours before** the slot start time.
- Cancellations after the cutoff are rejected with a clear error message.
- Cancelled slots are returned to AVAILABLE status immediately.

### Slots

- Slots are **fixed duration** (default: 60 minutes).
- Slots are generated based on the turf's operating hours and slot duration.
- Slot generation can be done on-demand (when Owner saves turf settings) or lazily (when Customer queries availability for a date).
- Owners and Staff can BLOCK individual slots regardless of availability.

### Turf Listing

- New turf listings require **Platform Admin approval** before becoming publicly visible.
- An Owner can have multiple turfs.
- Staff are assigned to specific turfs, not globally.