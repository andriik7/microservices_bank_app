package com.microservicesbank.accounts.service.impl;

import com.microservicesbank.accounts.dto.*;
import com.microservicesbank.accounts.exception.ResourceNotFoundException;
import com.microservicesbank.accounts.mapper.AccountMapper;
import com.microservicesbank.accounts.mapper.CustomerMapper;
import com.microservicesbank.accounts.model.Account;
import com.microservicesbank.accounts.model.Customer;
import com.microservicesbank.accounts.repository.AccountRepository;
import com.microservicesbank.accounts.repository.CustomerRepository;
import com.microservicesbank.accounts.service.ICustomerService;
import com.microservicesbank.accounts.service.client.CardsFeignClient;
import com.microservicesbank.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private AccountRepository accountRepository;

    private CustomerRepository customerRepository;

    private CardsFeignClient cardsFeignClient;

    private LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDTO fetchCustomerDetails(String mobileNumber, String correlationId) {

        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                                              .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        CustomerDetailsDTO customerDetailsDTO = CustomerMapper.mapToCustomerDetailsDTO(customer, new CustomerDetailsDTO());

        Account account = accountRepository.findByCustomerId(customer.getId())
                                           .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getId().toString()));
        AccountDTO accountDTO = AccountMapper.mapToAccountDTO(account, new AccountDTO());
        customerDetailsDTO.setAccountDto(accountDTO);

        ResponseEntity<CardDTO> cardResponse = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if (cardResponse != null) {
            customerDetailsDTO.setCardDto(cardResponse.getBody());
        }

        ResponseEntity<LoanDTO> loanResponse = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if (loanResponse != null) {
            customerDetailsDTO.setLoanDto(loanResponse.getBody());
        }

        return customerDetailsDTO;
    }
}
