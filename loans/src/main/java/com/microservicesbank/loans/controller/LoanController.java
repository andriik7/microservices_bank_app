package com.microservicesbank.loans.controller;

import com.microservicesbank.common.dto.ErrorResponseDTO;
import com.microservicesbank.loans.constants.LoanConstants;
import com.microservicesbank.loans.dto.LoanDTO;
import com.microservicesbank.loans.dto.LoansContactInfoDTO;
import com.microservicesbank.loans.dto.ResponseDTO;
import com.microservicesbank.loans.service.ILoanService;
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

@Tag(
        name = "REST APIs for Loans Microservice",
        description = "Loans REST APIs to perform CRUD operations on Loans Microservice"
)
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Validated
public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

    private ILoanService loanService;

    private LoansContactInfoDTO loansContactInfoDTO;

    @Operation(
            summary = "Create new loan REST API",
            description = "Creates a new loan based on provided 10-digit mobile number"
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
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("/createLoan")
    public ResponseEntity<ResponseDTO> createLoan(@RequestHeader("microbank-correlation-id") String correlationId,
                                                  @RequestParam @Pattern(regexp = "^\\d{10}$",
                                                          message = "Mobile number must be 10 digits") String mobileNumber) {

        logger.debug("createLoan() method started");
        loanService.createLoan(mobileNumber);
        logger.debug("createLoan() method ended");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDTO(LoanConstants.STATUS_201, LoanConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch loan REST API",
            description = "Fetches loan details based on provided 10-digit mobile number"
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
    @GetMapping("/fetchLoan")
    public ResponseEntity<LoanDTO> fetchLoanDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                    @RequestParam @Pattern(regexp = "^\\d{10}$",
                                                            message = "Mobile number must be 10 digits") String mobileNumber) {

        logger.debug("fetchLoan() method started");
        LoanDTO loanDTO = loanService.fetchLoan(mobileNumber);
        logger.debug("fetchLoan() method ended");

        return ResponseEntity.status(HttpStatus.OK).body(loanDTO);
    }

    @Operation(
            summary = "Update loan REST API",
            description = "Updates loan details based on provided loan details with appropriate loan number"
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
    @PutMapping("/updateLoan")
    public ResponseEntity<ResponseDTO> updateLoanDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                         @Valid @RequestBody LoanDTO loanDTO) {

        logger.debug("updateLoanDetails() method started");
        boolean isUpdated = loanService.updateLoan(loanDTO);
        logger.debug("updateLoanDetails() method ended");

        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDTO(LoanConstants.STATUS_200, LoanConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new ResponseDTO(LoanConstants.STATUS_417, LoanConstants.MESSAGE_417_UPDATE));
    }

    @Operation(
            summary = "Delete loan REST API",
            description = "Deletes loan details based on provided 10-digit mobile number"
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
    @DeleteMapping("/deleteLoan")
    public ResponseEntity<ResponseDTO> deleteLoanDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                         @RequestParam @Pattern(regexp = "^\\d{10}$",
                                                                 message = "Mobile number must be 10 digits") String mobileNumber) {

        logger.debug("deleteLoanDetails() method started");
        boolean isDeleted = loanService.deleteLoan(mobileNumber);
        logger.debug("deleteLoanDetails() method ended");

        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDTO(LoanConstants.STATUS_200, LoanConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new ResponseDTO(LoanConstants.STATUS_417, LoanConstants.MESSAGE_417_DELETE));
    }

    @GetMapping("/contactDetails")
    public ResponseEntity<LoansContactInfoDTO> getContactInfo(@RequestHeader("microbank-correlation-id") String correlationId) {

        logger.debug("getContactInfo() method started");
        logger.debug("getContactInfo() method ended");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loansContactInfoDTO);
    }
}
