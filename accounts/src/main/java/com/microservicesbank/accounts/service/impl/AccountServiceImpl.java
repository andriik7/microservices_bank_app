package com.microservicesbank.accounts.service.impl;

import com.microservicesbank.accounts.constants.AccountConstants;
import com.microservicesbank.accounts.dto.CustomerDTO;
import com.microservicesbank.accounts.exception.CustomerAlreadyExistsException;
import com.microservicesbank.accounts.mapper.CustomerMapper;
import com.microservicesbank.accounts.model.Account;
import com.microservicesbank.accounts.model.Customer;
import com.microservicesbank.accounts.repository.AccountRepository;
import com.microservicesbank.accounts.repository.CustomerRepository;
import com.microservicesbank.accounts.service.IAccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements IAccountService {

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;

    @Override
    public void createAccount(CustomerDTO customerDTO) {

        Customer customer = CustomerMapper.mapToCustomer(customerDTO, new Customer());
        if (customerRepository.findByMobileNumber(customer.getMobileNumber()).isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already exists with mobile number " + customer.getMobileNumber());
        };
        customer.setCreatedAt(LocalDateTime.now());
        customer.setCreatedBy("admin");
        Customer savedCustomer = customerRepository.save(customer);

        accountRepository.save(createNewAccount(savedCustomer));
    }

    private Account createNewAccount(Customer customer) {

        Account account = new Account();
        account.setId(customer.getId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        account.setAccountNumber(randomAccNumber);
        account.setAccountType(AccountConstants.SAVINGS);
        account.setBranchAddress(AccountConstants.ADDRESS);
        account.setCreatedAt(LocalDateTime.now());
        account.setCreatedBy("admin");
        return account;
    }
}
