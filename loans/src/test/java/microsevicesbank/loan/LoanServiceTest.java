package microsevicesbank.loan;

import com.microservicesbank.loans.LoansApplication;
import com.microservicesbank.loans.dto.LoanDTO;
import com.microservicesbank.loans.exception.LoanAlreadyExistsException;
import com.microservicesbank.loans.exception.ResourceNotFoundException;
import com.microservicesbank.loans.model.Loan;
import com.microservicesbank.loans.repository.LoanRepository;
import com.microservicesbank.loans.service.impl.LoanServiceImpl;
import jakarta.transaction.Transactional;
import microsevicesbank.loan.display.CamelCaseDisplay;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestPropertySource("/application-test.yml")
@DisplayNameGeneration(CamelCaseDisplay.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = LoansApplication.class)
@Transactional
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private static final String TEST_MOBILE_NUMBER = "0666992283";

    private static final String TEST_LOAN_NUMBER = "1234123412341234";

    @Test
    public void createLoanTest() {

        when(loanRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        loanService.createLoan(TEST_MOBILE_NUMBER);

        verify(loanRepository, times(1)).findByMobileNumber(anyString());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void createLoanAlreadyExistsTest() {

        when(loanRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(new Loan()));

        assertThrows(LoanAlreadyExistsException.class, () -> loanService.createLoan(TEST_MOBILE_NUMBER));

        verify(loanRepository, times(1)).findByMobileNumber(anyString());
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void fetchLoanTest() {

        when(loanRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(new Loan()));

        loanService.fetchLoan(TEST_MOBILE_NUMBER);

        verify(loanRepository, times(1)).findByMobileNumber(anyString());
    }

    @Test
    public void fetchLoanNotFoundTest() {

        when(loanRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(TEST_MOBILE_NUMBER));

        verify(loanRepository, times(1)).findByMobileNumber(anyString());
    }

    @Test
    public void updateLoanTest() {

        when(loanRepository.findByLoanNumber(TEST_LOAN_NUMBER)).thenReturn(Optional.of(new Loan()));

        LoanDTO loanDto = new LoanDTO();
        loanDto.setLoanNumber(TEST_LOAN_NUMBER);

        assertTrue(loanService.updateLoan(loanDto));

        verify(loanRepository, times(1)).findByLoanNumber(anyString());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void updateLoanNotFoundTest() {

        when(loanRepository.findByLoanNumber(TEST_LOAN_NUMBER)).thenReturn(Optional.empty());

        LoanDTO loanDto = new LoanDTO();
        loanDto.setLoanNumber(TEST_LOAN_NUMBER);

        assertThrows(ResourceNotFoundException.class, () -> loanService.updateLoan(loanDto));

        verify(loanRepository, times(1)).findByLoanNumber(anyString());
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    public void deleteLoanTest() {

        when(loanRepository.findByMobileNumber(TEST_MOBILE_NUMBER)).thenReturn(Optional.of(new Loan()));

        assertTrue(loanService.deleteLoan(TEST_MOBILE_NUMBER));

        verify(loanRepository, times(1)).findByMobileNumber(TEST_MOBILE_NUMBER);
        verify(loanRepository, times(1)).delete(any(Loan.class));
    }

    @Test
    public void deleteLoanNotFoundTest() {

        when(loanRepository.findByMobileNumber(TEST_MOBILE_NUMBER)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.deleteLoan(TEST_MOBILE_NUMBER));

        verify(loanRepository, times(1)).findByMobileNumber(TEST_MOBILE_NUMBER);
        verify(loanRepository, never()).delete(any(Loan.class));
    }

}
