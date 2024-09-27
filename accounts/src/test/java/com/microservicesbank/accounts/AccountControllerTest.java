package com.microservicesbank.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicesbank.accounts.constants.AccountConstants;
import com.microservicesbank.accounts.display.CamelCaseDisplay;
import com.microservicesbank.accounts.dto.AccountDTO;
import com.microservicesbank.accounts.dto.CustomerDTO;
import com.microservicesbank.accounts.exception.ResourceNotFoundException;
import com.microservicesbank.accounts.service.IAccountService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource("/application-test.yml")
@AutoConfigureMockMvc
@DisplayNameGeneration(CamelCaseDisplay.class)
@SpringBootTest
@Transactional
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAccountService accountService;

    private static final String DEFAULT_MOBILE_NUMBER = "0666992283";
    private static final String INVALID_MOBILE_NUMBER = "123";
    private static final String NON_EXISTENT_MOBILE_NUMBER = "0111111111";

    private static CustomerDTO customerDTO;

    @BeforeEach
    public void setUp() {
        customerDTO = createTestCustomer();
    }

    @Test
    public void createAccountPositiveTest() throws Exception {
        mockMvc.perform(post("/api/createAccount")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerDTO)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.statusCode").value(AccountConstants.STATUS_201))
               .andExpect(jsonPath("$.statusMessage").value(AccountConstants.MESSAGE_201));
    }

    @Test
    public void createAccountNegativeInvalidMobileNumberTest() throws Exception {
        customerDTO.setMobileNumber(INVALID_MOBILE_NUMBER);

        mockMvc.perform(post("/api/createAccount")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerDTO)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.mobileNumber").value("Invalid mobile number"));
    }

    @Test
    public void fetchAccountDetailsTest() throws Exception {
        accountService.createAccount(customerDTO);

        mockMvc.perform(get("/api/fetch")
                                .param("mobileNumber", DEFAULT_MOBILE_NUMBER))
               .andExpect(status().isOk());

        mockMvc.perform(get("/api/fetch")
                                .param("mobileNumber", INVALID_MOBILE_NUMBER))
               .andExpect(status().is5xxServerError())
               .andExpect(jsonPath("$.errorMessage").value("fetchAccountDetails.mobileNumber: Mobile number must be 10 digits"));

        mockMvc.perform(get("/api/fetch")
                                .param("mobileNumber", NON_EXISTENT_MOBILE_NUMBER))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorMessage").value("Customer not found with mobileNumber : '" + NON_EXISTENT_MOBILE_NUMBER + "'"));
    }

    @Test
    public void updateAccountDetailsPositiveTest() throws Exception {
        accountService.createAccount(customerDTO);

        Long accountNumber = accountService.fetchAccount(customerDTO.getMobileNumber())
                                           .getAccountDto().getAccountNumber();

        CustomerDTO updatedCustomerDTO = createUpdatedCustomer(accountNumber);

        mockMvc.perform(put("/api/updateAccount")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedCustomerDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.statusCode").value(AccountConstants.STATUS_200))
               .andExpect(jsonPath("$.statusMessage").value(AccountConstants.MESSAGE_200));

        CustomerDTO updatedCustomer = accountService.fetchAccount(updatedCustomerDTO.getMobileNumber());
        assertTrue(updatedCustomer.getAccountDto().getAccountType().equals("Loans")
                           && updatedCustomer.getEmail().equals("yopta@example.com"));
    }

    @Test
    public void updateAccountDetailsNegativeInvalidAccountTest() throws Exception {
        accountService.createAccount(customerDTO);

        AccountDTO invalidAccount = new AccountDTO();
        invalidAccount.setAccountNumber(9999999999L);
        invalidAccount.setAccountType("Loans");
        invalidAccount.setBranchAddress("12345 Main Street");
        customerDTO.setAccountDto(invalidAccount);

        mockMvc.perform(put("/api/updateAccount")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerDTO)))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
               .andExpect(jsonPath("$.errorMessage").value("Account not found with accountNumber : '" + invalidAccount.getAccountNumber() + '\''));

        CustomerDTO notUpdatedCustomer = accountService.fetchAccount(customerDTO.getMobileNumber());
        assertTrue(notUpdatedCustomer.getAccountDto().getAccountType().equals("Savings")
                           && notUpdatedCustomer.getAccountDto().getBranchAddress().equals("123 Main Street, New York"));
    }

    @Test
    public void deleteAccountPositiveTest() throws Exception {
        accountService.createAccount(customerDTO);

        mockMvc.perform(delete("/api/deleteAccount").param("mobileNumber", DEFAULT_MOBILE_NUMBER))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.statusCode").value(AccountConstants.STATUS_200))
               .andExpect(jsonPath("$.statusMessage").value(AccountConstants.MESSAGE_200));

        assertThrows(ResourceNotFoundException.class, () -> accountService.fetchAccount(DEFAULT_MOBILE_NUMBER));
    }

    @Test
    public void deleteAccountNegativeNotValidNotFoundTest() throws Exception {
        accountService.createAccount(customerDTO);

        assertNotNull(accountService.fetchAccount(DEFAULT_MOBILE_NUMBER));

        mockMvc.perform(delete("/api/deleteAccount").param("mobileNumber", "111"))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
               .andExpect(jsonPath("$.errorMessage").value("deleteAccount.mobileNumber: Mobile number must be 10 digits"));

        mockMvc.perform(delete("/api/deleteAccount").param("mobileNumber", NON_EXISTENT_MOBILE_NUMBER))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
               .andExpect(jsonPath("$.errorMessage").value("Customer not found with mobileNumber : '" + NON_EXISTENT_MOBILE_NUMBER + "'"));
    }

    private CustomerDTO createTestCustomer() {
        CustomerDTO customer = new CustomerDTO();
        customer.setName("Andrii Kuchera");
        customer.setEmail("ak47.10.07.06@gmail.com");
        customer.setMobileNumber(DEFAULT_MOBILE_NUMBER);

        AccountDTO account = new AccountDTO();
        account.setAccountNumber(1234567890L);
        account.setAccountType("Savings");
        account.setBranchAddress("123 Main Street, New York");
        customer.setAccountDto(account);

        return customer;
    }

    private CustomerDTO createUpdatedCustomer(Long accountNumber) {
        CustomerDTO updatedCustomer = new CustomerDTO();
        updatedCustomer.setName("Jane Doe");
        updatedCustomer.setEmail("yopta@example.com");
        updatedCustomer.setMobileNumber("1234567890");

        AccountDTO updatedAccount = new AccountDTO();
        updatedAccount.setAccountNumber(accountNumber);
        updatedAccount.setAccountType("Loans");
        updatedAccount.setBranchAddress("54 Lenkavskoho Street, Ivano-Frankivsk");
        updatedCustomer.setAccountDto(updatedAccount);

        return updatedCustomer;
    }
}
