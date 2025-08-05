/*
 * SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package com.zextras.carbonio.catalog.app.consul;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsulTokenProviderTest {

  @Test
  void canReadAToken() {
    final var path = getClass().getClassLoader().getResource("data/token").getFile();
    final var token = new ConsulTokenProvider(path).getToken();
    assertEquals("sample token", token.value());
  }

  @Test
  void throwsTokenNotFoundExceptionOnMissingToken() {
    final var tokenProvider = new ConsulTokenProvider("/this/path/does-not/exists");
    assertThrows(TokenNotFoundException.class, tokenProvider::getToken);
  }

}
