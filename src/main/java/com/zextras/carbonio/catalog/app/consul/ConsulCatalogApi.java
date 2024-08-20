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

  @GET
  @Path("/services")
  Map<String, List<String>> getAllServices(
      @HeaderParam("X-Consul-Token") String token);
}

