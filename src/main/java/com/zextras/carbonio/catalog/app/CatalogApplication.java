package com.zextras.carbonio.catalog.app;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(title = "Carbonio Catalog API", version = "1.0.0-BETA"),
    servers = {
        @Server(url = "http://localhost:10000"),
        @Server(url = "https://kc-dev1-prymta1.demo.zextras.io"),
        @Server(url = "https://kc-dev2-prymta1.demo.zextras.io"),
        @Server(url = "https://kc-dev3-prymta1.demo.zextras.io"),
    })
public class CatalogApplication extends Application {
}
