package microsevicesbank.card;

import com.microsevicesbank.card.CardsApplication;
import com.microsevicesbank.card.dto.CardDTO;
import com.microsevicesbank.card.exception.CardAlreadyExistsException;
import com.microsevicesbank.card.exception.ResourceNotFoundException;
import com.microsevicesbank.card.mapper.CardMapper;
import com.microsevicesbank.card.model.Card;
import com.microsevicesbank.card.repository.CardRepository;
import com.microsevicesbank.card.service.impl.CardServiceImpl;
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

    @Test
    public void testCreateCardTest() {

        when(cardRepository.findByMobileNumber("0666992283")).thenReturn(Optional.empty());

        cardService.createCard("0666992283");

        verify(cardRepository, times(1)).save(any());

        when(cardRepository.findByMobileNumber("0666992283")).thenReturn(Optional.of(new Card()));

        assertThrows(CardAlreadyExistsException.class, () -> cardService.createCard("0666992283"));
    }


    @Test
    public void fetchCardTest() {

        Card fetchCard = new Card(1L, "0666992283",
                                  "1234123412341234",
                                  "Credit Card", 1000,
                                  100, 900);

        when(cardRepository.findByMobileNumber("0666992283"))
                .thenReturn(Optional.of(fetchCard));

        assertEquals(cardService.fetchCard("0666992283"), CardMapper.mapToCardDTO(fetchCard, new CardDTO()));

        when(cardRepository.findByMobileNumber("0666992284"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class , () -> cardService.fetchCard("0666992284"));
    }

    @Test
    public void updateCardTest() {

        Card toUpdate = new Card(1L, "0666992283",
                                 "1234123412341234",
                                 "Credit Card", 1000,
                                 100, 900);
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(toUpdate));

        CardDTO updatedCard = CardMapper.mapToCardDTO(toUpdate, new CardDTO());
        updatedCard.setCardType("Debit Card");
        updatedCard.setAmountUsed(300);
        updatedCard.setAvailableAmount(700);

        assertTrue(cardService.updateCard(updatedCard));

        when(cardRepository.findByCardNumber("1234123412341235")).thenReturn(Optional.empty());

        updatedCard.setCardNumber("1234123412341235");

        assertThrows(ResourceNotFoundException.class, () -> cardService.updateCard(updatedCard));
    }

    @Test
    public void deleteCardTest() {
        Card toDelete = new Card(1L, "0666992283",
                                 "1234123412341234",
                                 "Credit Card", 1000,
                                 100, 900);

        when(cardRepository.findByCardNumber("1234123412341234")).thenReturn(Optional.of(toDelete));

        assertTrue(cardService.deleteCard("1234123412341234"));
        verify(cardRepository, times(1)).delete(any(Card.class));

        when(cardRepository.findByCardNumber("1234123412341235")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cardService.deleteCard("1234123412341235"));
    }

}