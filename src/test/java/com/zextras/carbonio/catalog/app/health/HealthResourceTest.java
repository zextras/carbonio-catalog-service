package com.zextras.carbonio.catalog.app.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class HealthResourceTest {
  @Test
  void testHealthStatus() {
    HealthResource healthResource = new HealthResource();
    assertEquals(healthResource.healthStatus().getStatus(), 200);
  }

  @Test
  void testLiveStatus() {
    HealthResource healthResource = new HealthResource();
    assertEquals(healthResource.liveStatus().getStatus(), 200);
  }

  @Test
  void testReadyStatus() {
    HealthResource healthResource = new HealthResource();
    assertEquals(healthResource.readyStatus().getStatus(), 200);
  }
}