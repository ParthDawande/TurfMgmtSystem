package com.turfmanager;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;

/**
 * Base class for all API integration tests.
 *
 * <ul>
 *   <li>Starts a real embedded server on a random port</li>
 *   <li>Activates the "test" Spring profile (application-test.properties)</li>
 *   <li>Resets and re-seeds the database before each test class</li>
 * </ul>
 *
 * Extend this class in every test — never use @SpringBootTest directly.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(
    scripts = {
        "classpath:fixtures/reset.sql",
        "classpath:fixtures/seed.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public abstract class BaseApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }

    /**
     * Logs in with the given credentials and returns the JWT token.
     */
    protected String loginAndGetToken(String email, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
            .when()
                .post("/auth/login")
            .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
