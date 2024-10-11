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

        Account account = accountRepository.findByCustomerId(customer.getId())
                                           .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getId().toString()));
        AccountDTO accountDTO = AccountMapper.mapToAccountDTO(account, new AccountDTO());

        CardDTO cardDTO = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber).getBody();

        LoanDTO loanDTO = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber).getBody();

        CustomerDetailsDTO customerDetailsDTO = CustomerMapper.mapToCustomerDetailsDTO(customer, new CustomerDetailsDTO());
        customerDetailsDTO.setAccountDto(accountDTO);
        customerDetailsDTO.setCardDto(cardDTO);
        customerDetailsDTO.setLoanDto(loanDTO);

        return customerDetailsDTO;
    }
}
