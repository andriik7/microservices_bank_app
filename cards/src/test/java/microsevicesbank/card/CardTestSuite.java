package microsevicesbank.card;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    CardServiceTest.class,
    CardControllerTest.class
})
public class CardTestSuite {
}
