package com.microsevicesbank.loan.service;

import com.microsevicesbank.loan.dto.LoanDTO;

public interface ILoanService {

    void createLoan(String mobileNumber);

    LoanDTO fetchLoan(String mobileNumber);

    boolean updateLoan(LoanDTO loanDto);

    boolean deleteLoan(String mobileNumber);

}
