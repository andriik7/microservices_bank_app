package com.microservicesbank.accounts.service.client;

import com.microservicesbank.accounts.dto.CardDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("cards")
public interface CardsFeignClient {

    @GetMapping(value = "/api/fetchCard", consumes = "application/json")
    public ResponseEntity<CardDTO> fetchCardDetails(@RequestParam String mobileNumber);

}
