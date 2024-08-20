package com.zextras.carbonio.catalog.app.services;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;

@QuarkusTest
class ServicesResourceTest {
  @Test
  void getEmptyServicesList() {
    given()
        .when()
        .get("services")
        .then()
        .statusCode(200)
        .body("items", empty());
  }
}
