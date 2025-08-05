/*
 * SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
/**
 * Health check logic follows the technical_specifications/health-check.md guideline provide by organization dev guide.
 */

package com.zextras.carbonio.catalog.app.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@ApplicationScoped
@Path("/health")
@Tag(name = "Health Resource")
public class HealthResource {
  @GET
  public Response healthStatus() {
    return Response
        .status(Response.Status.OK)
        .build();
  }

  @GET
  @Path("/live")
  public Response liveStatus() {
    return Response.status(Response.Status.OK).build();
  }

  @GET
  @Path("/ready")
  public Response readyStatus() {
    return liveStatus();
  }
}