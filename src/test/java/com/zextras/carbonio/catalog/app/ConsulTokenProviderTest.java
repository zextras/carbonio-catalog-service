package com.zextras.carbonio.catalog.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsulTokenProviderTest {

  @Test
  void canReadAToken() {
    String path = getClass().getClassLoader().getResource("data/token").getFile();
    var token = new ConsulTokenProvider(path).getToken();
    assertEquals("sample token", token.value());
  }

  @Test
  void throwsTokenNotFoundExceptionOnMissingToken() {
    var tokenProvider = new ConsulTokenProvider("/this/path/does-not/exists");
    assertThrows(TokenNotFoundException.class, tokenProvider::getToken);
  }

}
