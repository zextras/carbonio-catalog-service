package com.zextras.carbonio.catalog.app;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.consul.ConsulContainer;

import java.util.Map;

public class ConsulTestResource implements QuarkusTestResourceLifecycleManager {

  ConsulContainer consul;

  @Override
  public Map<String, String> start() {
    consul = new ConsulContainer("consul:1.10.12")
        .withConsulCommand("services register -name=carbonio-advanced")
        .withConsulCommand("services register -name=carbonio-files");
    consul.start();
    return Map.of("consul.url", String.format("http://%s:%d", consul.getHost(), consul.getFirstMappedPort()));
  }

  @Override
  public void stop() {
    if (consul != null) {
      consul.stop();
    }
  }
}
