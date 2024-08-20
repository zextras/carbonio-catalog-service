package com.zextras.carbonio.catalog;

import org.junit.jupiter.api.Test;

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

  @Test
  void excludeUninterestedServices() {
    final var names = ServiceName.fromAll(Set.of("consul", "test1", "test1-sidecar-proxy", "test2"));
    assertArrayEquals(new String[]{"test1", "test2"}, names);
  }
}