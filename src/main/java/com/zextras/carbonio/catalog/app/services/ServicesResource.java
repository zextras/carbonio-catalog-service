package com.zextras.carbonio.catalog.app.services;

import com.zextras.carbonio.catalog.ServiceName;
import com.zextras.carbonio.catalog.app.consul.ConsulCatalogApi;
import com.zextras.carbonio.catalog.app.consul.ConsulToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@ApplicationScoped
@Path("/services")
@Produces({MediaType.APPLICATION_JSON})
@Tag(name = "Services Resource")
public class ServicesResource {
  private static final Logger log = Logger.getLogger("SERVICES");

  private final ConsulToken token;
  private final ConsulCatalogApi consulCatalogApi;

  @Inject
  public ServicesResource(ConsulToken token, ConsulCatalogApi consulCatalogApi) {
    this.token = token;
    this.consulCatalogApi = consulCatalogApi;
  }

  @GET
  public GetServicesResponse getServices() {
    log.info("GET services");
    final var services = consulCatalogApi.getAllServices(token.value().trim());
    final var serviceNames = ServiceName.fromAll(services.keySet());
    return new GetServicesResponse(serviceNames);
  }
}
