package com.microservicesbank.accounts.service;

import com.microservicesbank.accounts.dto.CustomerDetailsDTO;

public interface ICustomerService {

    /**
     * Fetch customer details based on mobile number
     * @param mobileNumber
     * @return CustomerDetailsDTO
     */
    CustomerDetailsDTO fetchCustomerDetails(String mobileNumber);

}
