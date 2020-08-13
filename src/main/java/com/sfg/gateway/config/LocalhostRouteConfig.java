package com.sfg.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!local-discovery")
@Configuration
public class LocalhostRouteConfig {

    @Bean
    RouteLocator localhostRoutes(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(r -> r.path("/api/v1/beer*", "/api/v1/beer/*", "/api/vi/beerUpc/*")//any path matching "/api/v1/beer/*" and "/api/vi/beerUpc/*"
                        .uri("http://localhost:8080")
                        .id("beer-service"))
                .route(r -> r.path("/api/v1/customers/**")
                        .uri("http://localhost:8081")
                        .id("order-service"))
                .route(r -> r.path("/api/v1/beer/*/inventory")
                        .filters(f-> f.circuitBreaker(c -> c.setName("inventoryCB").setFallbackUri("forward:/inventory-failover").setRouteId("inv-failover")))
                        .uri("http://localhost:8082")
                        .id("inventory-service"))
                .route(r -> r.path("/inventory-failover/**")
                        .uri("lb://inventory-failover")
                        .id("inventory-failover-service"))
                .build();
    }
}
