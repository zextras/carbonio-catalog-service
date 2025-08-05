/*
 * SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package com.zextras.carbonio.catalog.app.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
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