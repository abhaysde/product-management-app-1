package com.gspann.api_gateway;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class GatewayApplicationTests {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void contextLoads() {
        // Check if the Spring context loads successfully
        assertThat(routeLocator).isNotNull();
    }

    @Test
    void routesShouldBeConfigured() {
        var routes = routeLocator.getRoutes().collectList().block();
        assertThat(routes).isNotEmpty();

        boolean hasAuthService = routes.stream()
                .anyMatch(r -> r.getId().equals("auth-service"));
        boolean hasProductService = routes.stream()
                .anyMatch(r -> r.getId().equals("product-service"));

        assertThat(hasAuthService).isTrue();
        assertThat(hasProductService).isTrue();
    }

    @Test
    void testAuthRoutePredicates() {
        var routes = routeLocator.getRoutes().collectList().block();
        var authRoute = routes.stream()
                .filter(r -> r.getId().equals("auth-service"))
                .findFirst()
                .orElseThrow();

        assertThat(authRoute.getUri().toString()).isEqualTo("http://localhost:8083");
    }

}
