# API Integration Test Infrastructure

This file covers test infrastructure setup only. Claude Code reads this before writing any test class or fixture.

---

## Stack Decision

| Concern               | Choice                                                       |
| --------------------- | ------------------------------------------------------------ |
| Test framework        | JUnit 5 (Jupiter)                                            |
| API assertion library | REST Assured 5.x                                             |
| Spring test mode      | `@SpringBootTest` — real embedded server, random port        |
| Database              | MySQL — dedicated `turfbook_test` schema                     |
| Schema management     | Flyway — same migrations as prod applied to test schema      |
| Data reset            | `@Sql` scripts — truncate + seed before each test class      |
| Auth                  | Real JWT — login via `/auth/login`, attach token to requests |
| Spring profile        | `test` — separate datasource, email disabled                 |

---

## 1. Maven Dependencies

Add to `backend/pom.xml` inside `<dependencies>`:

xml

```xml
<!-- REST Assured -->
<dependency>
  <groupId>io.rest-assured</groupId>
  <artifactId>rest-assured</artifactId>
  <version>5.4.0</version>
  <scope>test</scope>
  <exclusions>
    <exclusion>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
    </exclusion>
  </exclusions>
</dependency>
<dependency>
  <groupId>io.rest-assured</groupId>
  <artifactId>json-path</artifactId>
  <version>5.4.0</version>
  <scope>test</scope>
</dependency>

<!-- JUnit 5 -->
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <scope>test</scope>
</dependency>

<!-- AssertJ -->
<dependency>
  <groupId>org.assertj</groupId>
  <artifactId>assertj-core</artifactId>
  <scope>test</scope>
</dependency>
```

Add to `<build><plugins>`:

xml

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.2.5</version>
  <configuration>
    <includes>
      <include>**/*Test.java</include>
    </includes>
  </configuration>
</plugin>
```

## 2. Folder Structure

```
backend/
├── src/
│   ├── main/
│   │   └── resources/
│   │       └── db/migration/          # Flyway migrations (shared with prod)
│   └── test/
│       ├── java/com/turfbook/
│       │   ├── BaseApiTest.java        # Base class — extend this in every test
│       │   ├── TestConstants.java      # All seed IDs and credentials
│       │   ├── auth/
│       │   │   └── AuthApiTest.java
│       │   ├── turf/
│       │   │   └── TurfApiTest.java
│       │   ├── slot/
│       │   │   └── SlotApiTest.java
│       │   ├── booking/
│       │   │   └── BookingApiTest.java
│       │   ├── owner/
│       │   │   └── OwnerApiTest.java
│       │   ├── staff/
│       │   │   └── StaffApiTest.java
│       │   └── admin/
│       │       └── AdminApiTest.java
│       └── resources/
│           ├── application-test.properties
│           └── fixtures/
│               ├── reset.sql
│               └── seed.sql
└── pom.xml
```

---

