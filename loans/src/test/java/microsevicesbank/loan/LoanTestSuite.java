package microsevicesbank.loan;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    LoanServiceTest.class,
    LoanControllerTest.class
})
public class LoanTestSuite {
}
