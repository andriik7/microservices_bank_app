package com.microservicesbank.gatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    @Bean
    public RouteLocator microbankRouteConfig(RouteLocatorBuilder builder) {
        return builder.routes()
                      //to ignore global fallback
                      .route(r -> r.path("/microbank/accounts/api/fetchCustomerDetails/**")
                                   .filters(f -> f.rewritePath("/microbank/accounts/(?<segment>.*)", "/${segment}")
                                                  .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                                   .uri("lb://ACCOUNTS"))
                      .route(r -> r.path("/microbank/accounts/**")
                                   .filters(f -> f.rewritePath("/microbank/accounts/(?<segment>.*)", "/${segment}")
                                                  .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                                  .circuitBreaker(config -> config
                                                          .setName("accountsCircuitBreaker")
                                                          .setFallbackUri("forward:/fallback/contactSupport")))
                                   .uri("lb://ACCOUNTS"))
                      .route(r -> r.path("/microbank/cards/**")
                                   .filters(f -> f.rewritePath("/microbank/cards/(?<segment>.*)", "/${segment}")
                                                  .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                                  .circuitBreaker(config -> config
                                                          .setName("cardsCircuitBreaker")
                                                          .setFallbackUri("forward:/fallback/contactSupport"))
                                                  .retry(retryConfig -> retryConfig
                                                          .setRetries(3)
                                                          .setMethods(HttpMethod.GET)
                                                          .setBackoff(Duration.ofMillis(400), Duration.ofMillis(2000), 2, true)))
                                   .uri("lb://CARDS"))
                      .route(r -> r.path("/microbank/loans/**")
                                   .filters(f -> f.rewritePath("/microbank/loans/(?<segment>.*)", "/${segment}")
                                                  .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                                  .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                                          .setKeyResolver(userKeyResolver())))
//                                                  .circuitBreaker(config -> config.setName("loansCircuitBreaker")
//                                                          .setFallbackUri("forward:/fallback/contactSupport"))
                                   .uri("lb://LOANS"))
                      .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(
            id -> new Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                    .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(5)).build()).build() // 5 second default timeout for circuit breaker
        );
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
                .defaultIfEmpty("anonymous");
    }
}
