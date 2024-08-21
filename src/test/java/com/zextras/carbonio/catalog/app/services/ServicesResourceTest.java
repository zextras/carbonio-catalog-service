package com.zextras.carbonio.catalog.app.services;

import com.zextras.carbonio.catalog.app.ConsulTestResource;
import com.zextras.carbonio.catalog.app.InjectConsul;
import com.zextras.carbonio.catalog.app.consul.ConsulToken;
import io.quarkus.test.Mock;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.inject.Produces;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.consul.ConsulContainer;

import java.io.IOException;

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

  @InjectConsul // this a custom annotation you are defining in your own application
  ConsulContainer consul;

  @BeforeEach
  void setUp() throws IOException, InterruptedException {
    registerServices("carbonio-advanced", "carbonio-files", "carbonio-files-sidecar-proxy", "carbonio-advanced-sidecar-proxy");
  }

  @AfterEach
  void tearDown() throws IOException, InterruptedException {
    deregisterServices("carbonio-advanced", "carbonio-files", "carbonio-files-sidecar-proxy", "carbonio-advanced-sidecar-proxy");
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
  void geAllServices() {
    given()
        .when()
        .get("services")
        .then()
        .statusCode(200)
        .body("items.size()", is(2));
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
                "carbonio-files"));
  }

  private void registerServices(String... services) throws IOException, InterruptedException {
    for (String service : services) {
      consul.execInContainer(new String[]{"/bin/sh", "-c", "consul services register -name=" + service});
    }
  }

  private void deregisterServices(String... services) throws IOException, InterruptedException {
    for (String service : services) {
      consul.execInContainer(new String[]{"/bin/sh", "-c", "consul services deregister -id=" + service});
    }
  }
}
