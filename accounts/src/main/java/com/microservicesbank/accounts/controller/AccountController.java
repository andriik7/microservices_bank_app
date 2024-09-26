package com.microservicesbank.accounts.controller;

import com.microservicesbank.accounts.constants.AccountConstants;
import com.microservicesbank.accounts.dto.CustomerDTO;
import com.microservicesbank.accounts.dto.ResponseDTO;
import com.microservicesbank.accounts.service.IAccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Validated
public class AccountController {

    private IAccountService accountService;

    @PostMapping("/createAccount")
    public ResponseEntity<ResponseDTO> createAccount(@Valid @RequestBody CustomerDTO customerDTO) {

        accountService.createAccount(customerDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ResponseDTO(AccountConstants.STATUS_201, AccountConstants.MESSAGE_201));
    }

    @GetMapping("/fetch")
    public ResponseEntity<CustomerDTO> fetchAccountDetails(@RequestParam @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits") String mobileNumber) {

        CustomerDTO customerDTO = accountService.fetchAccount(mobileNumber);

        return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
    }

    @PutMapping("/updateAccount")
    public ResponseEntity<ResponseDTO> updateAccountDetails(@Valid @RequestBody CustomerDTO customerDTO) {

        boolean isUpdated = accountService.updateAccount(customerDTO);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ResponseDTO(AccountConstants.STATUS_200, AccountConstants.MESSAGE_200));
        }
        return ResponseEntity.status(HttpStatus.OK)
                             .body(new ResponseDTO(AccountConstants.STATUS_417, AccountConstants.MESSAGE_417_UPDATE));

    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<ResponseDTO> deleteAccount(@RequestParam @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits") String mobileNumber) {
        if (accountService.deleteAccount(mobileNumber)) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ResponseDTO(AccountConstants.STATUS_200, AccountConstants.MESSAGE_200));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ResponseDTO(AccountConstants.STATUS_417, AccountConstants.MESSAGE_417_DELETE));
    }
}
