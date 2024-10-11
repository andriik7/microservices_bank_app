package com.microservicesbank.loans.service.impl;


import com.microservicesbank.loans.constants.LoanConstants;
import com.microservicesbank.loans.exception.LoanAlreadyExistsException;
import com.microservicesbank.loans.exception.ResourceNotFoundException;
import com.microservicesbank.loans.mapper.LoanMapper;
import com.microservicesbank.loans.model.Loan;
import com.microservicesbank.loans.repository.LoanRepository;
import com.microservicesbank.loans.service.ILoanService;
import com.microservicesbank.loans.dto.LoanDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class LoanServiceImpl implements ILoanService {

    private LoanRepository loanRepository;

    @Override
    /**
     * Creates a new loan based on provided 10-digit mobile number and stores it in the database.
     * @param mobileNumber 10-digit mobile number of the customer
     */
    public void createLoan(String mobileNumber) {

        Optional<Loan> optionalLoan = loanRepository.findByMobileNumber(mobileNumber);
        if (optionalLoan.isPresent()) {
            throw new LoanAlreadyExistsException("Loan already exists with mobile number " + mobileNumber);
        }
        Loan saveLoan = createNewLoan(mobileNumber);
        loanRepository.save(saveLoan);
    }

    @Override
    /**
     * Fetches the loan details from the database based on the provided 10-digit mobile number.
     * @param mobileNumber 10-digit mobile number of the customer
     * @return LoanDTO with loan details
     */
    public LoanDTO fetchLoan(String mobileNumber) {

        Loan loan = loanRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );

        return LoanMapper.mapToLoanDTO(loan, new LoanDTO());
    }

    @Override
    /**
     * Updates the loan details in the database based on the provided loan details.
     * @param loanDto LoanDTO with updated loan details
     * @return true if updated successfully
     */
    public boolean updateLoan(LoanDTO loanDto) {

        Loan loan = loanRepository.findByLoanNumber(loanDto.getLoanNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "loanNumber", loanDto.getLoanNumber())
        );

        LoanMapper.mapToLoan(loanDto, loan);
        loanRepository.save(loan);

        return true;
    }

    @Override
    /**
     * Deletes the loan details from the database based on the provided 10-digit mobile number.
     * @param mobileNumber 10-digit mobile number of the customer
     * @return true if deleted successfully
     */
    public boolean deleteLoan(String mobileNumber) {

        Loan loan = loanRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );

        loanRepository.delete(loan);

        return true;
    }

    private Loan createNewLoan(String mobileNumber) {

        Loan loan = new Loan();
        loan.setMobileNumber(mobileNumber);
        Long randomLoanNumber = (1000000000000000L + new Random().nextLong(900000000000000L));
        loan.setLoanNumber(randomLoanNumber.toString());
        loan.setLoanType(LoanConstants.HOME_LOAN);
        loan.setTotalLoan(100000);
        loan.setAmountPaid(1000);
        loan.setOutstandingAmount(90000);
        return loan;
    }
}
