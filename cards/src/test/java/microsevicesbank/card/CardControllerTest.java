package microsevicesbank.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicesbank.cards.CardsApplication;
import com.microservicesbank.cards.constants.CardConstants;
import com.microservicesbank.cards.dto.CardDTO;
import com.microservicesbank.cards.exception.ResourceNotFoundException;
import com.microservicesbank.cards.service.ICardService;
import jakarta.transaction.Transactional;
import microsevicesbank.card.display.CamelCaseDisplay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.yml")
@DisplayNameGeneration(CamelCaseDisplay.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = CardsApplication.class)
@Transactional
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ICardService cardService;

    private CardDTO publicCard;

    private static final String DEFAULT_MOBILE_NUMBER = "0666992283";

    private static final String NON_EXISTENT_MOBILE_NUMBER = "1111111111";

    private static final String INVALID_MOBILE_NUMBER = "123";

    @BeforeEach
    public void setUp() {
        publicCard = new CardDTO();
        Long randomCardNumber = (1000000000000000L + new Random().nextLong(900000000000000L));
        publicCard.setMobileNumber(DEFAULT_MOBILE_NUMBER);
        publicCard.setCardNumber(randomCardNumber.toString());
        publicCard.setCardType(CardConstants.CREDIT_CARD);
        publicCard.setTotalLimit(CardConstants.NEW_CARD_LIMIT);
        publicCard.setAmountUsed(0);
        publicCard.setAvailableAmount(CardConstants.NEW_CARD_LIMIT);
    }

    @Test
    public void createCardSuccessTest() throws Exception {

        if (cardService.fetchCard(publicCard.getMobileNumber()) != null)
            cardService.deleteCard(publicCard.getMobileNumber());

        mockMvc.perform(post("/api/createCard")
                                .param("mobileNumber", publicCard.getMobileNumber()))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.statusCode").value(CardConstants.STATUS_201))
               .andExpect(jsonPath("$.statusMessage").value(CardConstants.MESSAGE_201));

        assertNotNull(cardService.fetchCard(publicCard.getMobileNumber()));

        mockMvc.perform(post("/api/createCard")
                                .param("mobileNumber", publicCard.getMobileNumber()))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
               .andExpect(jsonPath("$.errorMessage").value("Card already exists with mobile number " + publicCard.getMobileNumber()));

        CardDTO fetchedCard = cardService.fetchCard(publicCard.getMobileNumber());
        cardService.deleteCard(fetchedCard.getMobileNumber());
    }

    @Test
    public void сreateCardInvalidMobileNumberTest() throws Exception {

        publicCard.setMobileNumber(INVALID_MOBILE_NUMBER);

        mockMvc.perform(post("/api/createCard").param("mobileNumber", publicCard.getMobileNumber()))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
               .andExpect(jsonPath("$.errorMessage").value("createCard.mobileNumber: Mobile number must be 10 digits"));

    }

    @Test
    public void fetchCardDetailsSuccessTest() throws Exception {

        if (cardService.fetchCard(publicCard.getMobileNumber()) != null)
            cardService.deleteCard(publicCard.getMobileNumber());

        cardService.createCard(publicCard.getMobileNumber());
        CardDTO fetchedCard = cardService.fetchCard(publicCard.getMobileNumber());

        mockMvc.perform(get("/api/fetchCard").param("mobileNumber", publicCard.getMobileNumber()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.mobileNumber").value(fetchedCard.getMobileNumber()))
               .andExpect(jsonPath("$.cardNumber").value(fetchedCard.getCardNumber()))
               .andExpect(jsonPath("$.cardType").value(fetchedCard.getCardType()))
               .andExpect(jsonPath("$.totalLimit").value(fetchedCard.getTotalLimit()))
               .andExpect(jsonPath("$.amountUsed").value(fetchedCard.getAmountUsed()))
               .andExpect(jsonPath("$.availableAmount").value(fetchedCard.getAvailableAmount()));

        cardService.deleteCard(fetchedCard.getMobileNumber());
    }

    @Test
    public void fetchCardDetailsInvalidMobileNumberTest() throws Exception {

        mockMvc.perform(get("/api/fetchCard").param("mobileNumber", INVALID_MOBILE_NUMBER)).andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
               .andExpect(jsonPath("$.errorMessage").value("fetchCardDetails.mobileNumber: Mobile number must be 10 digits"));

        mockMvc.perform(get("/api/fetchCard").param("mobileNumber", NON_EXISTENT_MOBILE_NUMBER)).andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
               .andExpect(jsonPath("$.errorMessage").value("Card not found with mobileNumber : '" + NON_EXISTENT_MOBILE_NUMBER + "'"));
    }

    @Test
    public void updateCardSuccessTest() throws Exception {

        if (cardService.fetchCard(publicCard.getMobileNumber()) != null)
            cardService.deleteCard(publicCard.getMobileNumber());

        cardService.createCard(publicCard.getMobileNumber());
        publicCard = cardService.fetchCard(publicCard.getMobileNumber());

        CardDTO updatedCard = new CardDTO();
        updatedCard.setMobileNumber("0505554466");
        updatedCard.setCardNumber(publicCard.getCardNumber());
        updatedCard.setCardType("Debit Card");
        updatedCard.setTotalLimit(5000);
        updatedCard.setAmountUsed(500);
        updatedCard.setAvailableAmount(4500);

        mockMvc.perform(put("/api/updateCard")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedCard)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.statusCode").value(CardConstants.STATUS_200))
               .andExpect(jsonPath("$.statusMessage").value(CardConstants.MESSAGE_200));

        assertEquals(updatedCard, cardService.fetchCard(updatedCard.getMobileNumber()));

        cardService.deleteCard(updatedCard.getMobileNumber());
    }

    @Test
    public void updateCardInvalidMobileNumberTest() throws Exception {

        mockMvc.perform(put("/api/updateCard")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(publicCard)))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
               .andExpect(jsonPath("$.errorMessage").value("Card not found with cardNumber : '" + publicCard.getCardNumber() + "'"));

        publicCard.setMobileNumber(INVALID_MOBILE_NUMBER);
        publicCard.setCardNumber("12345");
        publicCard.setCardType("");
        publicCard.setTotalLimit(0);
        publicCard.setAmountUsed(-1);
        publicCard.setAvailableAmount(-1);

        mockMvc.perform(put("/api/updateCard")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(publicCard)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.mobileNumber").value("Mobile number must be 10 digits"))
               .andExpect(jsonPath("$.cardNumber").value("Card number must be 16 digits"))
               .andExpect(jsonPath("$.cardType").value("Card type cannot be empty"))
               .andExpect(jsonPath("$.totalLimit").value("Total limit must be greater than 0"))
               .andExpect(jsonPath("$.amountUsed").value("Amount used must be greater than 0 or equal to 0"))
               .andExpect(jsonPath("$.availableAmount").value("Available amount must be greater than 0 or equal to 0"));
    }

    @Test
    public void deleteCardSuccessTest() throws Exception {
        if (cardService.fetchCard(publicCard.getMobileNumber()) == null)
            cardService.createCard(publicCard.getMobileNumber());

        assertDoesNotThrow(() -> cardService.fetchCard(publicCard.getMobileNumber()));

        mockMvc.perform(delete("/api/deleteCard")
                                .param("mobileNumber", publicCard.getMobileNumber()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.statusCode").value(CardConstants.STATUS_200))
               .andExpect(jsonPath("$.statusMessage").value(CardConstants.MESSAGE_200));

        assertThrows(ResourceNotFoundException.class, () -> cardService.fetchCard(publicCard.getMobileNumber()));
    }

    @Test
    public void deleteCardInvalidMobileNumberTest() throws Exception {
        mockMvc.perform(delete("/api/deleteCard")
                                .param("mobileNumber", NON_EXISTENT_MOBILE_NUMBER))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
               .andExpect(jsonPath("$.errorMessage").value("Card not found with mobileNumber : '" + NON_EXISTENT_MOBILE_NUMBER + "'"));

        mockMvc.perform(delete("/api/deleteCard")
                                .param("mobileNumber", INVALID_MOBILE_NUMBER))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
               .andExpect(jsonPath("$.errorMessage").value("deleteCardDetails.mobileNumber: Mobile number must be 10 digits"));
    }

}
