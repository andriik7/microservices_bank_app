package com.microservicesbank.accounts.controller;

import com.microservicesbank.accounts.dto.CustomerDetailsDTO;
import com.microservicesbank.accounts.dto.ErrorResponseDTO;
import com.microservicesbank.accounts.service.ICustomerService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "REST APIs for Accounts Microservice's Customer",
        description = "REST API for microservicesbank to fetch cards and loans"
)
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Validated
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final ICustomerService customerService;

    @Operation(summary = "Fetch customer details REST API",
            description = "Fetch customer details based on mobile number")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status 200 OK"),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status 404 NOT FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)))
            ,
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
//    @Retry(name = "fetchCustomerDetailsRetry", fallbackMethod = "fetchCustomerDetailsAfterRetryFallback")
    @RateLimiter(name = "fetchCustomerDetailsRateLimiter", fallbackMethod = "fetchCustomerDetailsRateLimiterFallback")
    @GetMapping("/fetchCustomerDetails")
    public ResponseEntity<CustomerDetailsDTO> fetchCustomerDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                                   @RequestParam @Pattern(regexp = "^\\d{10}$",
                                                                           message = "Mobile number must be 10 digits") String mobileNumber) {

//        throw new RuntimeException("Forced Exception");
        //logger.debug("microbank-correlation-id found: {}", correlationId); // this will not be logged because since
        // feature/observability-monitoring we use OpenTelemetry and distributed tracing
        logger.debug("fetchCustomerDetails() method started");
        CustomerDetailsDTO customerDetailsDTO = customerService.fetchCustomerDetails(mobileNumber, correlationId);
        logger.debug("fetchCustomerDetails() method ended");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerDetailsDTO);
    }

//    public ResponseEntity<CustomerDetailsDTO> fetchCustomerDetailsAfterRetryFallback(@RequestHeader("microbank-correlation-id") String correlationId,
//                                                                                     @RequestParam @Pattern(regexp = "^\\d{10}$",
//                                                                                             message = "Mobile number must be 10 digits") String mobileNumber,
//                                                                                     Throwable throwable) {
//
//        logger.debug("fetchCustomerDetailsAfterRetryFallback() method invoked with an exception: {}", throwable.getMessage());
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(new CustomerDetailsDTO());
//    }

    public ResponseEntity<String> fetchCustomerDetailsRateLimiterFallback(@RequestHeader("microbank-correlation-id") String correlationId,
                                                                           @RequestParam @Pattern(regexp = "^\\d{10}$",
                                                                                   message = "Mobile number must be 10 digits") String mobileNumber,
                                                                           Throwable throwable) {

        logger.debug("fetchCustomerDetailsRateLimiterFallback() method invoked");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Cool down. You are trying to access the resource too frequently. Please wait some time and try again later.");
    }

}
