package com.zextras.carbonio.catalog.app.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@ApplicationScoped
@Path("/services")
@Produces({MediaType.APPLICATION_JSON})
@Tag(name = "Services Resource")
public class ServicesResource {
  @GET
  public GetServicesResponse getServices() {
    // log.infof("GET quota for: %s", req);
    return new GetServicesResponse(new String[] {  });
  }
}
