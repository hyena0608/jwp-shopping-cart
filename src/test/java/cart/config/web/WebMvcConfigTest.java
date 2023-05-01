package cart.config.web;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebMvcConfigTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.port;
    }

    @Test
    void 메서드명() {
        given()
//                .header("Authorization", "Basic ZW1haWxAZW1haWwuY29tOnBhc3N3b3Jk")
                .log().all()
        .when()
                .get("/")
        .then()
                .statusCode(HttpStatus.OK.value());
    }

}
