package com.microservicesbank.accounts.service.impl;

import com.microservicesbank.accounts.constants.AccountConstants;
import com.microservicesbank.accounts.dto.AccountDTO;
import com.microservicesbank.accounts.dto.AccountsMessageDTO;
import com.microservicesbank.accounts.dto.CustomerDTO;
import com.microservicesbank.accounts.exception.CustomerAlreadyExistsException;
import com.microservicesbank.accounts.exception.ResourceNotFoundException;
import com.microservicesbank.accounts.mapper.AccountMapper;
import com.microservicesbank.accounts.mapper.CustomerMapper;
import com.microservicesbank.accounts.model.Account;
import com.microservicesbank.accounts.model.Customer;
import com.microservicesbank.accounts.repository.AccountRepository;
import com.microservicesbank.accounts.repository.CustomerRepository;
import com.microservicesbank.accounts.service.IAccountService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements IAccountService {

    private final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private StreamBridge streamBridge;

    @Override
    public void createAccount(CustomerDTO customerDTO) {

        Customer customer = CustomerMapper.mapToCustomer(customerDTO, new Customer());
        if (customerRepository.findByMobileNumber(customer.getMobileNumber()).isPresent()) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists with mobile number " + customer.getMobileNumber());
        }
        Customer savedCustomer = customerRepository.save(customer);

        Account savedAccount = accountRepository.save(createNewAccount(savedCustomer));
        sendCreatedAccountCommunication(savedAccount, savedCustomer);
    }

    private void sendCreatedAccountCommunication(Account account, Customer customer) {

        AccountsMessageDTO accountsMessageDTO = new AccountsMessageDTO(account.getAccountNumber(), customer.getName(),
                                                                       customer.getEmail(), customer.getMobileNumber());
        log.info("Sending account created notification request with data: {}", accountsMessageDTO);
        boolean result = streamBridge.send("sendCreatedAccount-out-0", accountsMessageDTO);
        log.info("Result of communication is {}", result ? "positive" : "negative");
    }

    @Override
    public CustomerDTO fetchAccount(String mobileNumber) {

        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Account account = accountRepository.findByCustomerId(customer.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getId().toString()));

        CustomerDTO customerDTO = CustomerMapper.mapToCustomerDTO(customer, new CustomerDTO());
        customerDTO.setAccountDto(AccountMapper.mapToAccountDTO(account, new AccountDTO()));
        return customerDTO;
    }

    @Override
    public boolean updateAccount(CustomerDTO customerDTO) {

        boolean isUpdated = false;

        AccountDTO accountDTO = customerDTO.getAccountDto();
        if (accountDTO != null) {
            Account account = accountRepository.findById(accountDTO.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "accountNumber",
                                                        accountDTO.getAccountNumber().toString()));
            AccountMapper.mapToAccount(accountDTO, account);
            accountRepository.save(account);

            Long customerId = account.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "customerId", customerId.toString()));
            CustomerMapper.mapToCustomer(customerDTO, customer);
            customerRepository.save(customer);
            isUpdated = true;

        }
        return isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {

        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Account account = accountRepository.findByCustomerId(customer.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getId().toString()));
        sendDeletedAccountCommunication(account, customer);

        accountRepository.delete(account);
        customerRepository.delete(customer);
        return true;
    }

    private void sendDeletedAccountCommunication(Account account, Customer customer) {

        AccountsMessageDTO accountsMessageDTO = new AccountsMessageDTO(account.getAccountNumber(), customer.getName(),
                                                                       customer.getEmail(), customer.getMobileNumber());
        log.info("Sending account deleted notification request with data: {}", accountsMessageDTO);
        boolean result = streamBridge.send("sendDeletedAccount-out-1", accountsMessageDTO);
        log.info("Result of communication is {}", result ? "positive" : "negative");
    }

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {

        boolean isUpdated = false;
        if (accountNumber != null) {
            Account account = accountRepository.findById(accountNumber).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "accountNumber", accountNumber.toString()));
            account.setCommunicationSw(true);
            accountRepository.save(account);
            isUpdated = true;
        }
        return isUpdated;
    }

    private Account createNewAccount(Customer customer) {

        Account account = new Account();
        account.setCustomerId(customer.getId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        account.setAccountNumber(randomAccNumber);
        account.setAccountType(AccountConstants.SAVINGS);
        account.setBranchAddress(AccountConstants.ADDRESS);
        return account;
    }
}
