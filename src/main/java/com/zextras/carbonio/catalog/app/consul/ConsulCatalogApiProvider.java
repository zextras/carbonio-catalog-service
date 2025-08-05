/*
 * SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
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

