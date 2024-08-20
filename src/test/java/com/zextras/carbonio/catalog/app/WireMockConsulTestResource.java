package com.zextras.carbonio.catalog.app;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WireMockConsulTestResource implements QuarkusTestResourceLifecycleManager {

  private WireMockServer wireMockServer;

  @Override
  public Map<String, String> start() {
    wireMockServer = new WireMockServer(options().dynamicPort());
    wireMockServer.start();

    wireMockServer.stubFor(get("/v1/catalog/services")
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(readResourceContent("data/consul-catalog-services-response.json"))));

    return Map.of("consul.url", String.format("http://localhost:%s", wireMockServer.port()));
  }

  private String readResourceContent(String name) {
    final var path = getClass().getClassLoader().getResource(name).getFile();
    try {
      return Files.readString(Path.of(path));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void stop() {
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
  }
}
