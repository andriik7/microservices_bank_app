package com.microservicesbank.loans.service;

import com.microservicesbank.loans.dto.LoanDTO;

public interface ILoanService {

    void createLoan(String mobileNumber);

    LoanDTO fetchLoan(String mobileNumber);

    boolean updateLoan(LoanDTO loanDto);

    boolean deleteLoan(String mobileNumber);

}
