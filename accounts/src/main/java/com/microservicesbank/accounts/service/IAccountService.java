package com.microservicesbank.accounts.service;

import com.microservicesbank.accounts.dto.CustomerDTO;

public interface IAccountService {

    /**
     * This method is used to create an account
     *
     * @param customerDTO This is the object that contains the customer's information
     */
    /**
     * This method is used to create an account
     * <p>
     * This method will create a new account for the customer
     * </p>
     *
     * @param customerDTO This is the object that contains the customer's information
     */
    void createAccount(CustomerDTO customerDTO);
}
