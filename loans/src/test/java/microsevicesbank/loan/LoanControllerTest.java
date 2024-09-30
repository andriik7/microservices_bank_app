package microsevicesbank.loan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsevicesbank.loan.LoansApplication;
import com.microsevicesbank.loan.constants.LoanConstants;
import com.microsevicesbank.loan.dto.LoanDTO;
import com.microsevicesbank.loan.exception.ResourceNotFoundException;
import com.microsevicesbank.loan.service.ILoanService;
import jakarta.transaction.Transactional;
import microsevicesbank.loan.display.CamelCaseDisplay;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.yml")
@DisplayNameGeneration(CamelCaseDisplay.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = LoansApplication.class)
@Transactional
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ILoanService loanService;

    private static final String DEFAULT_MOBILE_NUMBER = "0666992283";

    private static final String NON_EXISTENT_MOBILE_NUMBER = "1111111111";

    private static final String INVALID_MOBILE_NUMBER = "123";

    private static final String NON_EXISTENT_LOAN_NUMBER = "0234123412341235";

    private static final String INVALID_LOAN_NUMBER = "456";

    @Test
    public void createLoanTest() throws Exception {

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        mockMvc.perform(post("/api/createLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("mobileNumber", DEFAULT_MOBILE_NUMBER))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.statusCode").value(LoanConstants.STATUS_201))
               .andExpect(jsonPath("$.statusMessage").value(LoanConstants.MESSAGE_201));

        assertDoesNotThrow(() -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        loanService.deleteLoan(DEFAULT_MOBILE_NUMBER);
    }

    @Test
    public void createLoanAlreadyExistsTest() throws Exception {

        loanService.createLoan(DEFAULT_MOBILE_NUMBER);

        assertDoesNotThrow(() -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        mockMvc.perform(post("/api/createLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("mobileNumber", DEFAULT_MOBILE_NUMBER))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
               .andExpect(jsonPath("$.errorMessage").value("Loan already exists with mobile number " + DEFAULT_MOBILE_NUMBER));

        assertDoesNotThrow(() -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        loanService.deleteLoan(DEFAULT_MOBILE_NUMBER);
    }

    @Test
    public void createLoanInvalidMobileNumberTest() throws Exception {

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(INVALID_MOBILE_NUMBER));

        mockMvc.perform(post("/api/createLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("mobileNumber", INVALID_MOBILE_NUMBER))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
               .andExpect(jsonPath("$.errorMessage").value("createLoan.mobileNumber: Mobile number must be 10 digits"));

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(INVALID_MOBILE_NUMBER));
    }

    @Test
    public void fetchLoanTest() throws Exception {

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        loanService.createLoan(DEFAULT_MOBILE_NUMBER);

        LoanDTO fetchedLoan;

        assertDoesNotThrow(() -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        fetchedLoan = loanService.fetchLoan(DEFAULT_MOBILE_NUMBER);

        mockMvc.perform(get("/api/fetchLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("mobileNumber", DEFAULT_MOBILE_NUMBER))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.mobileNumber").value(fetchedLoan.getMobileNumber()))
               .andExpect(jsonPath("$.loanNumber").value(fetchedLoan.getLoanNumber()))
               .andExpect(jsonPath("$.loanType").value(fetchedLoan.getLoanType()))
               .andExpect(jsonPath("$.totalLoan").value(fetchedLoan.getTotalLoan()))
               .andExpect(jsonPath("$.amountPaid").value(fetchedLoan.getAmountPaid()))
               .andExpect(jsonPath("$.outstandingAmount").value(fetchedLoan.getOutstandingAmount()));

        assertDoesNotThrow(() -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        loanService.deleteLoan(DEFAULT_MOBILE_NUMBER);
    }

    @Test
    public void fetchLoanNotFoundTest() throws Exception {

        loanService.createLoan(DEFAULT_MOBILE_NUMBER);

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(NON_EXISTENT_MOBILE_NUMBER));

        mockMvc.perform(get("/api/fetchLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("mobileNumber", NON_EXISTENT_MOBILE_NUMBER))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
               .andExpect(jsonPath("$.errorMessage").value("Loan not found with mobileNumber : '" + NON_EXISTENT_MOBILE_NUMBER + "'"));

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(NON_EXISTENT_MOBILE_NUMBER));

        loanService.deleteLoan(DEFAULT_MOBILE_NUMBER);
    }

    @Test
    public void fetchLoanInvalidMobileNumberTest() throws Exception {

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(INVALID_MOBILE_NUMBER));

        mockMvc.perform(get("/api/fetchLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("mobileNumber", INVALID_MOBILE_NUMBER))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
               .andExpect(jsonPath("$.errorMessage").value("fetchLoanDetails.mobileNumber: Mobile number must be 10 digits"));
    }

    @Test
    public void updateLoanTest() throws Exception {

        loanService.createLoan(DEFAULT_MOBILE_NUMBER);

        assertDoesNotThrow(() -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        LoanDTO currentLoan = loanService.fetchLoan(DEFAULT_MOBILE_NUMBER);

        LoanDTO updatedLoan = new LoanDTO();
        updatedLoan.setLoanNumber(currentLoan.getLoanNumber());
        updatedLoan.setLoanType("Car Loan");
        updatedLoan.setTotalLoan(5000);
        updatedLoan.setAmountPaid(500);
        updatedLoan.setOutstandingAmount(4500);
        updatedLoan.setMobileNumber(NON_EXISTENT_MOBILE_NUMBER);

        mockMvc.perform(put("/api/updateLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedLoan)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.statusCode").value(LoanConstants.STATUS_200))
               .andExpect(jsonPath("$.statusMessage").value(LoanConstants.MESSAGE_200));

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));
        assertDoesNotThrow(() -> loanService.fetchLoan(NON_EXISTENT_MOBILE_NUMBER));

        loanService.deleteLoan(NON_EXISTENT_MOBILE_NUMBER);
    }

    @Test
    public void updateLoanNotFoundTest() throws Exception {

        loanService.createLoan(DEFAULT_MOBILE_NUMBER);

        LoanDTO currentLoan = loanService.fetchLoan(DEFAULT_MOBILE_NUMBER);

        LoanDTO updatedLoan = new LoanDTO();
        updatedLoan.setLoanNumber(NON_EXISTENT_LOAN_NUMBER);
        updatedLoan.setLoanType("Car Loan");
        updatedLoan.setTotalLoan(5000);
        updatedLoan.setAmountPaid(500);
        updatedLoan.setOutstandingAmount(4500);
        updatedLoan.setMobileNumber(NON_EXISTENT_MOBILE_NUMBER);

        mockMvc.perform(put("/api/updateLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedLoan)))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
               .andExpect(jsonPath("$.errorMessage").value("Loan not found with loanNumber : '" + NON_EXISTENT_LOAN_NUMBER + "'"));

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(NON_EXISTENT_MOBILE_NUMBER));

        assertDoesNotThrow(() -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        loanService.deleteLoan(DEFAULT_MOBILE_NUMBER);
    }

    @Test
    public void updateLoanInvalidMobileNumberTest() throws Exception {

        LoanDTO updatedLoan = new LoanDTO();
        updatedLoan.setLoanNumber(INVALID_LOAN_NUMBER);
        updatedLoan.setLoanType("");
        updatedLoan.setTotalLoan(0);
        updatedLoan.setAmountPaid(-1);
        updatedLoan.setOutstandingAmount(-1);
        updatedLoan.setMobileNumber(INVALID_MOBILE_NUMBER);

        mockMvc.perform(put("/api/updateLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedLoan)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.loanNumber").value("Loan number must be 16 digits"))
               .andExpect(jsonPath("$.mobileNumber").value("Mobile number must be 10 digits"))
               .andExpect(jsonPath("$.loanType").value("LoanType cannot be null or empty"))
               .andExpect(jsonPath("$.totalLoan").value("Total loan amount should be greater than 0"))
               .andExpect(jsonPath("$.amountPaid").value("Total loan amount paid should be equal or greater than 0"))
               .andExpect(jsonPath("$.outstandingAmount").value("Total outstanding loan amount should be equal or greater than 0"));
    }

    @Test
    public void deleteLoanTest() throws Exception {

        loanService.createLoan(DEFAULT_MOBILE_NUMBER);

        assertDoesNotThrow(() -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        mockMvc.perform(delete("/api/deleteLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("mobileNumber", DEFAULT_MOBILE_NUMBER))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.statusCode").value(LoanConstants.STATUS_200))
               .andExpect(jsonPath("$.statusMessage").value(LoanConstants.MESSAGE_200));

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));
    }

    @Test
    public void deleteLoanNotFoundTest() throws Exception {

        loanService.createLoan(DEFAULT_MOBILE_NUMBER);

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(NON_EXISTENT_MOBILE_NUMBER));

        mockMvc.perform(delete("/api/deleteLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("mobileNumber", NON_EXISTENT_MOBILE_NUMBER))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
               .andExpect(jsonPath("$.errorMessage").value("Loan not found with mobileNumber : '" + NON_EXISTENT_MOBILE_NUMBER + "'"));

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(NON_EXISTENT_MOBILE_NUMBER));
        assertDoesNotThrow(() -> loanService.fetchLoan(DEFAULT_MOBILE_NUMBER));

        loanService.deleteLoan(DEFAULT_MOBILE_NUMBER);
    }

    @Test
    public void deleteLoanInvalidMobileNumberTest() throws Exception {

        assertThrows(ResourceNotFoundException.class, () -> loanService.fetchLoan(INVALID_MOBILE_NUMBER));

        mockMvc.perform(delete("/api/deleteLoan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("mobileNumber", INVALID_MOBILE_NUMBER))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
               .andExpect(jsonPath("$.errorMessage").value("deleteLoanDetails.mobileNumber: Mobile number must be 10 digits"));
    }
}
