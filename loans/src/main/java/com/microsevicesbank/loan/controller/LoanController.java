package com.microsevicesbank.loan.controller;

import com.microsevicesbank.loan.constants.LoanConstants;
import com.microsevicesbank.loan.dto.ErrorResponseDTO;
import com.microsevicesbank.loan.dto.LoanDTO;
import com.microsevicesbank.loan.dto.ResponseDTO;
import com.microsevicesbank.loan.service.ILoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
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

    private ILoanService loanService;

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
    public ResponseEntity<ResponseDTO> createLoan(@RequestParam @Pattern(regexp = "^\\d{10}$",
            message = "Mobile number must be 10 digits") String mobileNumber) {

        loanService.createLoan(mobileNumber);

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
    public ResponseEntity<ResponseDTO> fetchLoanDetails(@RequestParam @Pattern(regexp = "^\\d{10}$",
            message = "Mobile number must be 10 digits") String mobileNumber) {

        loanService.fetchLoan(mobileNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(LoanConstants.STATUS_200, LoanConstants.MESSAGE_200));
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
    public ResponseEntity<ResponseDTO> updateLoanDetails(@Valid @RequestBody LoanDTO loanDTO) {

        boolean isUpdated = loanService.updateLoan(loanDTO);

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
    public ResponseEntity<ResponseDTO> deleteLoanDetails(@RequestParam @Pattern(regexp = "^\\d{10}$",
            message = "Mobile number must be 10 digits") String mobileNumber) {

        boolean isDeleted = loanService.deleteLoan(mobileNumber);

        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDTO(LoanConstants.STATUS_200, LoanConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new ResponseDTO(LoanConstants.STATUS_417, LoanConstants.MESSAGE_417_DELETE));
    }

}
