package microsevicesbank.card;

import com.microservicesbank.cards.CardsApplication;
import com.microservicesbank.cards.dto.CardDTO;
import com.microservicesbank.cards.exception.CardAlreadyExistsException;
import com.microservicesbank.cards.exception.ResourceNotFoundException;
import com.microservicesbank.cards.mapper.CardMapper;
import com.microservicesbank.cards.model.Card;
import com.microservicesbank.cards.repository.CardRepository;
import com.microservicesbank.cards.service.impl.CardServiceImpl;
import jakarta.transaction.Transactional;
import microsevicesbank.card.display.CamelCaseDisplay;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestPropertySource("/application-test.yml")
@DisplayNameGeneration(CamelCaseDisplay.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = CardsApplication.class)
@Transactional
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private static final String TEST_MOBILE_NUMBER = "0666992283";

    private static final String NON_EXISTENT_MOBILE_NUMBER = "0666992284";

    private static final String TEST_CARD_NUMBER = "1234123412341234";

    private static final String NON_EXISTENT_CARD_NUMBER = "1234123412341235";

    @Test
    public void testCreateCardTest() {

        when(cardRepository.findByMobileNumber(TEST_MOBILE_NUMBER)).thenReturn(Optional.empty());

        cardService.createCard(TEST_MOBILE_NUMBER);

        verify(cardRepository, times(1)).save(any());

        when(cardRepository.findByMobileNumber(TEST_MOBILE_NUMBER)).thenReturn(Optional.of(new Card()));

        assertThrows(CardAlreadyExistsException.class, () -> cardService.createCard(TEST_MOBILE_NUMBER));
    }


    @Test
    public void fetchCardTest() {

        Card fetchCard = new Card(1L, TEST_MOBILE_NUMBER, TEST_CARD_NUMBER, "Credit Card", 1000, 100, 900);

        when(cardRepository.findByMobileNumber(TEST_MOBILE_NUMBER))
                .thenReturn(Optional.of(fetchCard));

        assertEquals(cardService.fetchCard(TEST_MOBILE_NUMBER), CardMapper.mapToCardDTO(fetchCard, new CardDTO()));

        when(cardRepository.findByMobileNumber(NON_EXISTENT_MOBILE_NUMBER))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class , () -> cardService.fetchCard(NON_EXISTENT_MOBILE_NUMBER));
    }

    @Test
    public void updateCardTest() {

        Card toUpdate = new Card(1L, TEST_MOBILE_NUMBER, TEST_CARD_NUMBER, "Credit Card", 1000, 100, 900);
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(toUpdate));

        CardDTO updatedCard = CardMapper.mapToCardDTO(toUpdate, new CardDTO());
        updatedCard.setCardType("Debit Card");
        updatedCard.setAmountUsed(300);
        updatedCard.setAvailableAmount(700);

        assertTrue(cardService.updateCard(updatedCard));

        when(cardRepository.findByCardNumber(NON_EXISTENT_CARD_NUMBER)).thenReturn(Optional.empty());

        updatedCard.setCardNumber(NON_EXISTENT_CARD_NUMBER);

        assertThrows(ResourceNotFoundException.class, () -> cardService.updateCard(updatedCard));
    }

    @Test
    public void deleteCardTest() {
        Card toDelete = new Card(1L, TEST_MOBILE_NUMBER, TEST_CARD_NUMBER, "Credit Card", 1000, 100, 900);

        when(cardRepository.findByMobileNumber(TEST_MOBILE_NUMBER)).thenReturn(Optional.of(toDelete));

        assertTrue(cardService.deleteCard(TEST_MOBILE_NUMBER));
        verify(cardRepository, times(1)).delete(any(Card.class));

        when(cardRepository.findByMobileNumber(TEST_MOBILE_NUMBER)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cardService.deleteCard(TEST_MOBILE_NUMBER));
    }
}
