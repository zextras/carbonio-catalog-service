/*
 * SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package com.zextras.carbonio.catalog.app.services;

import com.zextras.carbonio.catalog.app.ConsulTestResource;
import com.zextras.carbonio.catalog.app.InjectConsul;
import com.zextras.carbonio.catalog.app.consul.ConsulToken;
import io.quarkus.test.Mock;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.inject.Produces;
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

  public static final String[] ALL_SERVICES = {"carbonio-advanced", "carbonio-files", "carbonio-files-sidecar-proxy", "carbonio-advanced-sidecar-proxy", "consul"};

  @Mock
  @Produces
  ConsulToken testConsulToken() {
    return new ConsulToken("TEST_CONSUL_TOKEN");
  }

  @InjectConsul
  ConsulContainer consul;

  @BeforeEach
  void setUp() throws IOException, InterruptedException {
    registerServices(ALL_SERVICES);
  }

  @AfterEach
  void tearDown() throws IOException, InterruptedException {
    deregisterServices(ALL_SERVICES);
  }

  @Test
  void getEmptyServices() throws IOException, InterruptedException {
    deregisterServices(ALL_SERVICES);
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
