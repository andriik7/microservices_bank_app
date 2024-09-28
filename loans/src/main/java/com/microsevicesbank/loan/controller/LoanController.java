package com.microsevicesbank.loan.controller;

import com.microsevicesbank.loan.constants.LoanConstants;
import com.microsevicesbank.loan.dto.LoanDTO;
import com.microsevicesbank.loan.dto.ResponseDTO;
import com.microsevicesbank.loan.service.ILoanService;
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
        name = "REST APIs for Account Microservice",
        description = "Account REST APIs to perform CRUD operations on Account Microservice"
)
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Validated
public class LoanController {

    private ILoanService loanService;

    @PostMapping("/createLoan")
    public ResponseEntity<ResponseDTO> createLoan(@RequestParam @Pattern(regexp = "^\\d{10}$",
            message = "Mobile number must be 10 digits") String mobileNumber) {

        loanService.createLoan(mobileNumber);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDTO(LoanConstants.STATUS_201, LoanConstants.MESSAGE_201));
    }

    @GetMapping("/fetchLoan")
    public ResponseEntity<ResponseDTO> fetchLoanDetails(@RequestParam @Pattern(regexp = "^\\d{10}$",
            message = "Mobile number must be 10 digits") String mobileNumber) {

        loanService.fetchLoan(mobileNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(LoanConstants.STATUS_200, LoanConstants.MESSAGE_200));
    }

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
