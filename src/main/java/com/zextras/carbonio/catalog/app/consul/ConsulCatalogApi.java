/*
 * SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package com.zextras.carbonio.catalog.app.consul;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;

@Path("/v1/catalog")
@RegisterRestClient
public interface ConsulCatalogApi {

  // See: https://developer.hashicorp.com/consul/api-docs/catalog#list-services
  @GET
  @Path("/services")
  Map<String, List<String>> getAllServices(
      @HeaderParam("X-Consul-Token") String token);
}

