package com.microservicesbank.accounts.service.client;

import com.microservicesbank.accounts.dto.CardDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cards", fallback = CardsFallback.class)
public interface CardsFeignClient {

    @GetMapping(value = "/api/fetchCard", consumes = "application/json")
    public ResponseEntity<CardDTO> fetchCardDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                    @RequestParam String mobileNumber);

}
