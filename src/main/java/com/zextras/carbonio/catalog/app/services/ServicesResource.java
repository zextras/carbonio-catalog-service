package com.zextras.carbonio.catalog.app.services;

import jakarta.enterprise.context.ApplicationScoped;
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
  @GET
  public GetServicesResponse getServices() {
    log.infof("GET services");
    return new GetServicesResponse(new String[] {  });
  }
}
