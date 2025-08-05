/*
 * SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package com.zextras.carbonio.catalog.app;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.consul.ConsulContainer;

import java.util.Map;

public class ConsulTestResource implements QuarkusTestResourceLifecycleManager {

  ConsulContainer consul;

  @Override
  public Map<String, String> start() {
    consul = new ConsulContainer("consul:1.10.12");
    consul.start();
    return Map.of("consul.url", String.format("http://%s:%d", consul.getHost(), consul.getFirstMappedPort()));
  }

  @Override
  public void stop() {
    if (consul != null) {
      consul.stop();
    }
  }

  @Override
  public void inject(TestInjector testInjector) {
    testInjector.injectIntoFields(consul, new TestInjector.AnnotatedAndMatchesType(InjectConsul.class, ConsulContainer.class));
  }
}
