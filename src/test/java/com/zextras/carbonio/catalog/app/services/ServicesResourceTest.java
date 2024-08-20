package com.zextras.carbonio.catalog.app.services;

import com.zextras.carbonio.catalog.app.ConsulTestResource;
import com.zextras.carbonio.catalog.app.consul.ConsulToken;
import io.quarkus.test.Mock;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.inject.Produces;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@WithTestResource(value = ConsulTestResource.class)
class ServicesResourceTest {

  @Mock
  @Produces
  ConsulToken testConsulToken() {
    return new ConsulToken("TEST_CONSUL_TOKEN");
  }

  @Ignore
  void getEmptyServices() {
    given()
        .when()
        .get("services")
        .then()
        .statusCode(200)
        .body("items", empty());
  }

  @Test
  void geAlltServices() {
    given()
        .when()
        .get("services")
        .then()
        .statusCode(200)
        .body("items.size()", is(3));
  }

  @Test
  void getServiceNames() {
    given()
        .when()
        .get("services")
        .then()
        .statusCode(200)
        .body("items", hasItems(
                "carbonio-advanced",
                "carbonio-files",
                "consul"));
  }
}
