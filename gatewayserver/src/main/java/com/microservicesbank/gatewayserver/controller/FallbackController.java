package com.microservicesbank.gatewayserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/customerDetailsError")
    public Mono<String> fallback() {
        return Mono.just("Customer service is chilling. Please try again later!");
    }

    @GetMapping("/contactSupport")
    public Mono<String> contactSupport() {
        return Mono.just("An error occurred. Please try again later and contact support!");
    }

}
