package com.zextras.carbonio.catalog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceNameTest {

  @Test
  void noServices() {
    final var names = ServiceName.fromAll(Set.of());
    assertEquals(0, names.length);
  }

  @Test
  void manyServices() {
    final var names = ServiceName.fromAll(Set.of("test1", "test2"));
    assertArrayEquals(new String[]{"test1", "test2"}, names);
  }

  @ParameterizedTest
  @ValueSource(strings = {"consul", "test1-sidecar-proxy"})
  void excludeUninterestedServices(String serviceName) {
    final var names = ServiceName.fromAll(Set.of("test1", serviceName, "test2"));
    assertArrayEquals(new String[]{"test1", "test2"}, names);
  }
}