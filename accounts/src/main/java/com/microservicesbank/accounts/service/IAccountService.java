package com.microservicesbank.accounts.service;

import com.microservicesbank.accounts.dto.CustomerDTO;

public interface IAccountService {

    /**
     * Creates a new account for the customer
     *
     * @param customerDTO Customer's information
     */
    void createAccount(CustomerDTO customerDTO);

    /**
     * Fetches customer's account by mobile number
     *
     * @param mobileNumber Mobile number of the customer
     * @return CustomerDTO with AccountDTO in it
     */
    CustomerDTO fetchAccount(String mobileNumber);

    /**
     * Updates customer's account
     *
     * @param customerDTO Customer's information
     * @return true if account is updated successfully, false otherwise
     */
    boolean updateAccount(CustomerDTO customerDTO);

    boolean deleteAccount(String mobileNumber);

    boolean updateCommunicationStatus(Long accountNumber);
}
