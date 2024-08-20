package com.zextras.carbonio.catalog.app.services;

import com.zextras.carbonio.catalog.app.WireMockConsulTestResource;
import com.zextras.carbonio.catalog.app.consul.ConsulToken;
import io.quarkus.test.Mock;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@WithTestResource(value = WireMockConsulTestResource.class)
class ServicesResourceTest {

  @ConfigProperty(name = "consul.url")
  String consulUrl;

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
        .body("items.size()", is(4));
  }

  @Test
  void getServiceNames() {
    given()
        .when()
        .get("services")
        .then()
        .statusCode(200)
        .body("items", hasItem(
            anyOf(
                equalTo("carbonio-advanced"),
                equalTo("carbonio-files"))
        ));
  }
}
