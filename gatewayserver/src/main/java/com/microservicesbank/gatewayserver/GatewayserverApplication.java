package com.microservicesbank.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    @Bean
    public RouteLocator microbankRouteConfig(RouteLocatorBuilder builder) {
        return builder.routes()
                      .route(r -> r.path("/microbank/accounts/**")
                                   .filters(f -> f.rewritePath("/microbank/accounts/(?<segment>.*)", "/${segment}")
                                                  .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                                  .circuitBreaker(config -> config.setName("accountsCircuitBreaker")))
                                   .uri("lb://ACCOUNTS"))
                      .route(r -> r.path("/microbank/cards/**")
                                   .filters(f -> f.rewritePath("/microbank/cards/(?<segment>.*)", "/${segment}")
                                                  .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                                   .uri("lb://CARDS"))
                      .route(r -> r.path("/microbank/loans/**")
                                   .filters(f -> f.rewritePath("/microbank/loans/(?<segment>.*)", "/${segment}")
                                                  .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                                   .uri("lb://LOANS"))
                      .build();
    }
}
