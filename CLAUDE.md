## Turf Management System

## Project Overview

**Turf Management System** is a SaaS platform connecting sports turf owners (public and private) with individuals and groups looking to book playing time. The platform serves four distinct user roles and handles the full lifecycle of turf discovery, slot booking, and cancellation.

---

## Core Problem Statement

Sports turf owners lack a centralized, digital way to list their facilities and manage time-based bookings. Customers (players, teams, groups) have no reliable way to discover available turfs, check real-time slot availability, and book or cancel sessions online. Turf Management System solves both sides of this marketplace.

---

## User Roles

| Role               | Description                                                                                                           |
| ------------------ | --------------------------------------------------------------------------------------------------------------------- |
| **Customer**       | Searches for turfs, views available slots, books and cancels sessions, may raise complaints regarding their bookings. |
| **Turf Owner**     | Lists and manages turfs, defines operating hours and slot availability, views bookings                                |
| **Staff**          | Can resolve the complaints.                                                                                           |
| **Platform Admin** | Manages the entire platform — approves turf listings, manages users, views platform-wide analytics                    |

---
# Architecture Overview 
---

## System Architecture Summary

Turf Management System is a two-tier architecture: a React Native + Expo frontend communicating over REST with a Spring Boot backend backed by MySQL. The frontend is built once and targets web (MVP) and mobile (Phase 2) from the same codebase.

```
[Browser / iOS / Android]
        ↓ HTTPS REST JSON
[Spring Security — JWT filter + Role guard]
        ↓
[Spring Boot Controllers → Services → JPA Repositories]
        ↓
[MySQL]          [Email Service]          [Cloud Storage]
```
## Project Folder Structure 

```
/
├── backend/              # root folder for backend 
|
├── frontend/             # root folder for frontend
|
└── CLAUDE.md
```

---

## Frontend Stack

|Layer|Technology|Reason|
|---|---|---|
|Framework|React Native + Expo SDK 52|Single codebase for web now, iOS/Android in 3–6 months|
|Routing|Expo Router v4 (file-based)|Web URLs + native screen navigation from same files|
|Styling|NativeWind v4 (Tailwind CSS)|Utility-first, fast UI iteration, high polish without custom StyleSheet|
|Component library|React Native Paper v5|Pre-built accessible components (Cards, Inputs, Dialogs, FAB)|
|Global state|Zustand v5|Minimal boilerplate, beginner-friendly|
|Server state / API|TanStack Query v5|Caching, loading/error states, background refetch|
|Forms + validation|React Hook Form v7 + Zod v3|Declarative forms, schema-based validation|
|HTTP client|Axios v1|REST calls to Spring Boot, JWT interceptors, 401 redirect|
|Date handling|date-fns v3|Slot date/time formatting|
|Secure storage|expo-secure-store v13|JWT token — Keychain (iOS) / Keystore (Android) / localStorage (web)|

### Frontend Folder Structure

```
├── frontend/             # root folder for frontend
	├── app/  
	|	├── (auth)/                  # Login, Register screens
	|	├── (customer)/              # Search, Turf detail, Book, My Bookings
	|	├── (owner)/                 # Dashboard, Manage turfs, Block slots
	|	├── (staff)/                 # Today's bookings, Check-in
	|	├── (admin)/                 # Approve listings, User management
	|	└── _layout.tsx              # Root layout — auth guard + role redirect
	|
	├── components/
	|	├── ui/                      # Button, Card, Badge, Input (shared)
	|	├── turf/                    # TurfCard, SlotPicker, AmenitiesChips
	|	└── booking/                 # BookingCard, StatusBadge, CancelModal
	|
	├── services/                    # Axios calls — one file per domain
	├── store/                       # Zustand stores (auth.store.ts, app.store.ts)
	├── hooks/                       # Custom hooks wrapping TanStack Query
	├── constants/                   # roles.ts, booking.ts, api.ts
	├── utils/                       # date.ts, storage.ts, validators.ts (Zod schemas)
```

### Role-Based Navigation

After login, root `_layout.tsx` reads role from JWT and redirects:

```
CUSTOMER  →  /(customer)/
OWNER     →  /(owner)/
STAFF     →  /(staff)/
ADMIN     →  /(admin)/
```

Each group's `_layout.tsx` contains a role guard that redirects unauthorised users to `/login`.



---

## Backend Stack

|Layer|Technology|
|---|---|
|Runtime|Java 17+|
|Framework|Spring Boot 3.x|
|API style|REST — all routes prefixed `/api/v1/`|
|Security|Spring Security — JWT stateless auth + role-based method guards|
|ORM|Spring Data JPA + Hibernate|
|Database|MySQL 8|
|Migration|Flyway (recommended) or Liquibase|
|Email|Spring Mail (JavaMailSender)|
|File storage|AWS S3 or Cloudinary — store URL in DB, not the file|

### Backend Package Structure

```
├── backend/              # root folder for backend 
|	├── prisma/ 
|	│   └── schema.prisma 
│   ├── src/
|		├── com.turf-manager/
			├── controller/        # REST endpoints — thin, delegate to service
			├── service/           # Business logic — BookingService, SlotService, etc.
			├── repository/        # Spring Data JPA interfaces
			├── model/             # JPA entities — User, Turf, Slot, Booking, StaffAssignment
			├── dto/               # Request/Response DTOs (never expose entity directly)
			├── security/          # JwtFilter, SecurityConfig, UserDetailsServiceImpl
			├── exception/         # GlobalExceptionHandler (@ControllerAdvice)
			└── util/              # SlotGenerator, DateUtils, EmailService
```


### API Conventions

- Base path: `/api/v1/`
- Auth header: `Authorization: Bearer <jwt>`
- Consistent response envelope:

```json
{ "success": true, "data": {}, "message": "Optional message" }
{ "success": false, "message": "Error description", "errors": [] }
```

- Pagination on all list endpoints — default page size 20
- Soft delete: `deletedAt` timestamp, never hard delete User or Turf rows

### Security Model

Spring Security filter chain validates JWT on every request before it reaches a controller. Role enforcement via `@PreAuthorize` on service methods or controller-level `@Secured`.

```
POST /api/v1/auth/login            → public
POST /api/v1/auth/register         → public
GET  /api/v1/turfs                 → public (approved turfs only)
GET  /api/v1/turfs/{id}/slots      → public
POST /api/v1/bookings              → CUSTOMER only
GET  /api/v1/owner/turfs           → OWNER only
PUT  /api/v1/owner/slots/{id}      → OWNER or STAFF
GET  /api/v1/admin/turfs/pending   → ADMIN only
```

---

## Cross-Cutting Concerns

| Concern                | Approach                                                                        |
| ---------------------- | ------------------------------------------------------------------------------- |
| Booking race condition | DB transaction + unique constraint on (slotId, status=BOOKED)                   |
| Password storage       | BCrypt hashing — never store or return plain text                               |
| Image uploads          | Frontend sends to S3/Cloudinary directly, stores URL via backend API            |
| Token expiry           | Access token short-lived (15–60 min); refresh token in Phase 2                  |
| Error handling         | `@ControllerAdvice` GlobalExceptionHandler maps exceptions to HTTP status codes |
| CORS                   | Spring Security CorsConfigurationSource — allow Expo web origin in dev          |




---

## Key Development Guidelines for Claude Code

1. **Always enforce role-based access control** at the middleware level, not just in controllers.
2. **Never expose passwordHash** in any API response. Strip it before returning user objects.
3. **Slot availability must be atomic** — use DB transactions when creating a booking to prevent race conditions.
4. **Validate all date/time inputs** — use a library like `date-fns` or `dayjs`. Assume times are stored in UTC, displayed in local timezone.
5. **Soft delete** turfs and users — add `deletedAt` field rather than hard deleting rows.
6. **Environment variables** for all secrets, API keys, DB connection strings. Never hardcode.
7. **Pagination** on all list endpoints (turfs, bookings, users) — default page size 20.
8. **Image uploads** go to cloud storage (S3/Cloudinary); store only the URL in MySQL.
9. **Write services separately from controllers** — business logic lives in `/services`, not in route handlers.

---

## Naming Conventions

- **Files:** `camelCase.js` for utilities, `PascalCase.js` for React components
- **DB columns:** `snake_case` (Prisma maps these to camelCase in JS)
- **API routes:** `kebab-case` (e.g., `/my-bookings`)
- **Env vars:** `SCREAMING_SNAKE_CASE`
- **React components:** PascalCase, one component per file

---
