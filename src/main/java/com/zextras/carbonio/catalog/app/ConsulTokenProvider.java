package com.zextras.carbonio.catalog.app;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConsulTokenProvider {
  private final String consulTokenPath;

  @Inject
  public ConsulTokenProvider(@ConfigProperty(name = "consul.token.filepath") String consulTokenPath) {
    this.consulTokenPath = consulTokenPath;
  }

  @Produces
  public ConsulToken getToken() {
    try {
      return new ConsulToken(Files.readString(Paths.get(consulTokenPath)));
    } catch (IOException e) {
      throw new TokenNotFoundException(e);
    }
  }
}

