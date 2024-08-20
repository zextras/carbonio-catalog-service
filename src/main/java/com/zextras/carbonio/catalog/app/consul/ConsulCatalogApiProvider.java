package com.zextras.carbonio.catalog.app.consul;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;

public class ConsulCatalogApiProvider {

  private final String consulUrl;

  @Inject
  public ConsulCatalogApiProvider(@ConfigProperty(name = "consul.url") String consulUrl) {
    this.consulUrl = consulUrl;
  }

  @Produces
  public ConsulCatalogApi getConsulCatalogAPi() {
    return RestClientBuilder.newBuilder()
        .baseUri(URI.create(consulUrl))
        .build(ConsulCatalogApi.class);
  }

}

