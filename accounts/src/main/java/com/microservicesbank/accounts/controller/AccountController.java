package com.microservicesbank.accounts.controller;

import com.microservicesbank.accounts.constants.AccountConstants;
import com.microservicesbank.accounts.dto.AccountsContactInfoDTO;
import com.microservicesbank.accounts.dto.CustomerDTO;
import com.microservicesbank.accounts.dto.ResponseDTO;
import com.microservicesbank.accounts.service.IAccountService;
import com.microservicesbank.common.dto.ErrorResponseDTO;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeoutException;

@Tag(
        name = "REST APIs for Accounts Microservice",
        description = "Accounts REST APIs to perform CRUD operations on Accounts Microservice"
)
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Validated
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private IAccountService accountService;

    private AccountsContactInfoDTO accountsContactInfoDTO;

    @Operation(
            summary = "Create new account REST API",
            description = "Creates a new account based on provided customer details and connect them"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status 201 CREATED"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "HTTP Status 400 BAD REQUEST",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("/createAccount")
    public ResponseEntity<ResponseDTO> createAccount(@RequestHeader("microbank-correlation-id") String correlationId,
                                                     @Valid @RequestBody CustomerDTO customerDTO) {

        logger.debug("microbank-correlation-id found: {}", correlationId);
        accountService.createAccount(customerDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ResponseDTO(AccountConstants.STATUS_201, AccountConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch account REST API",
            description = "Fetches account and customer details based on provided 10-digit mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status 200 OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status 404 NOT FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("/fetchAccount")
    public ResponseEntity<CustomerDTO> fetchAccountDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                           @RequestParam @Pattern(regexp = "^\\d{10}$",
                                                                   message = "Mobile number must be 10 digits") String mobileNumber) {

        logger.debug("microbank-correlation-id found: {}", correlationId);
        CustomerDTO customerDTO = accountService.fetchAccount(mobileNumber);

        return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
    }

    @Operation(
            summary = "Update account REST API",
            description = "Updates account details based on provided customer details with appropriate account number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status 200 OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status 404 NOT FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "HTTP Status 417 CONFLICT",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping("/updateAccount")
    public ResponseEntity<ResponseDTO> updateAccountDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                            @Valid @RequestBody CustomerDTO customerDTO) {

        logger.debug("microbank-correlation-id found: {}", correlationId);
        boolean isUpdated = accountService.updateAccount(customerDTO);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ResponseDTO(AccountConstants.STATUS_200, AccountConstants.MESSAGE_200));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ResponseDTO(AccountConstants.STATUS_417, AccountConstants.MESSAGE_417_UPDATE));

    }

    @Operation(
            summary = "Delete account REST API",
            description = "Deletes account and customer details based on provided 10-digit mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status 200 OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status 404 NOT FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "HTTP Status 417 CONFLICT",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @DeleteMapping("/deleteAccount")
    public ResponseEntity<ResponseDTO> deleteAccount(@RequestHeader("microbank-correlation-id") String correlationId,
                                                     @RequestParam @Pattern(regexp = "^\\d{10}$",
                                                             message = "Mobile number must be 10 digits") String mobileNumber) {

        logger.debug("microbank-correlation-id found: {}", correlationId);
        if (accountService.deleteAccount(mobileNumber)) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ResponseDTO(AccountConstants.STATUS_200, AccountConstants.MESSAGE_200));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ResponseDTO(AccountConstants.STATUS_417, AccountConstants.MESSAGE_417_DELETE));
    }

    @Retry(name = "getContactDetails", fallbackMethod = "getContactDetailsFallback")
    @GetMapping("/contactDetails")
    public ResponseEntity<AccountsContactInfoDTO> getContactInfo(@RequestHeader("microbank-correlation-id") String correlationId) throws TimeoutException {

//        throw new NullPointerException(); //to check the fallback method(while ignoreExceptions and retryExceptions are not mentioned)
//        logger.debug("getContactDetails() method invoked"); //to test if request is really retried
//        throw new TimeoutException(); //to check the fallback method(while retryExceptions with appropriate exception is mentioned)
        logger.debug("microbank-correlation-id found: {}", correlationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactInfoDTO);
    }

    public ResponseEntity<String> getContactDetailsFallback(Throwable throwable) {

        logger.debug("getContactDetailsFallback() method invoked");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Currently we can't access any contact details for you. Please try again later.");
    }
}
