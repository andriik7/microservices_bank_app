package com.microservicesbank.accounts.service.client;

import com.microservicesbank.accounts.dto.LoanDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "loans", fallback = LoansFallback.class)
public interface LoansFeignClient {

    @GetMapping(value = "/api/fetchLoan", consumes = "application/json")
    public ResponseEntity<LoanDTO> fetchLoanDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                    @RequestParam String mobileNumber);

}
