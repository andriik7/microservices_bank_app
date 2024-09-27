package com.microservicesbank.accounts;

import com.microservicesbank.accounts.display.CamelCaseDisplay;
import com.microservicesbank.accounts.dto.AccountDTO;
import com.microservicesbank.accounts.dto.CustomerDTO;
import com.microservicesbank.accounts.exception.CustomerAlreadyExistsException;
import com.microservicesbank.accounts.exception.ResourceNotFoundException;
import com.microservicesbank.accounts.model.Account;
import com.microservicesbank.accounts.model.Customer;
import com.microservicesbank.accounts.repository.AccountRepository;
import com.microservicesbank.accounts.repository.CustomerRepository;
import com.microservicesbank.accounts.service.impl.AccountServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestPropertySource("/application-test.yml")
@DisplayNameGeneration(CamelCaseDisplay.class)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAccountWhenCustomerAlreadyExistsThrowsException() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setMobileNumber("1234567890");

        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(new Customer()));

        assertThrows(CustomerAlreadyExistsException.class, () -> {
            accountService.createAccount(customerDTO);
        });

        verify(customerRepository, times(1)).findByMobileNumber(anyString());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testCreateAccountSuccess() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setMobileNumber("1234567890");

        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        accountService.createAccount(customerDTO);

        verify(customerRepository, times(1)).findByMobileNumber(anyString());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @ParameterizedTest
    @CsvSource({
            "1234567890",
            "0987654321"
    })
    public void testFetchAccountSuccess(String mobileNumber) {
        Customer customer = new Customer();
        customer.setId(1L);
        Account account = new Account();
        account.setCustomerId(1L);

        when(customerRepository.findByMobileNumber(mobileNumber)).thenReturn(Optional.of(customer));
        when(accountRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(account));

        CustomerDTO result = accountService.fetchAccount(mobileNumber);

        assertNotNull(result);
        verify(customerRepository, times(1)).findByMobileNumber(mobileNumber);
        verify(accountRepository, times(1)).findByCustomerId(anyLong());
    }

    @Test
    public void testFetchAccountCustomerNotFoundThrowsException() {
        String mobileNumber = "1234567890";

        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.fetchAccount(mobileNumber);
        });

        verify(customerRepository, times(1)).findByMobileNumber(anyString());
        verify(accountRepository, never()).findByCustomerId(anyLong());
    }

    @Test
    public void testUpdateAccountSuccess() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber(123456789L);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setAccountDto(accountDTO);

        Account account = new Account();
        account.setCustomerId(1L);

        Customer customer = new Customer();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        boolean isUpdated = accountService.updateAccount(customerDTO);

        assertTrue(isUpdated);
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    public void testUpdateAccountAccountNotFoundThrowsException() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber(123456789L);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setAccountDto(accountDTO);

        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.updateAccount(customerDTO);
        });

        verify(accountRepository, times(1)).findById(anyLong());
        verify(customerRepository, never()).findById(anyLong());
    }

    @ParameterizedTest
    @CsvSource({
            "1234567890",
            "0987654321"
    })
    public void testDeleteAccountSuccess(String mobileNumber) {
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(customer));

        boolean isDeleted = accountService.deleteAccount(mobileNumber);

        assertTrue(isDeleted);
        verify(accountRepository, times(1)).deleteByCustomerId(anyLong());
        verify(customerRepository, times(1)).delete(any(Customer.class));
    }

    @Test
    public void testDeleteAccountСustomerNotFoundThrowsException() {
        String mobileNumber = "1234567890";

        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.deleteAccount(mobileNumber);
        });

        verify(customerRepository, times(1)).findByMobileNumber(anyString());
        verify(accountRepository, never()).deleteByCustomerId(anyLong());
    }
}
