package com.microservicesbank.cards.service.impl;


import com.microservicesbank.cards.constants.CardConstants;
import com.microservicesbank.cards.exception.CardAlreadyExistsException;
import com.microservicesbank.cards.exception.ResourceNotFoundException;
import com.microservicesbank.cards.mapper.CardMapper;
import com.microservicesbank.cards.model.Card;
import com.microservicesbank.cards.repository.CardRepository;
import com.microservicesbank.cards.service.ICardService;
import com.microservicesbank.cards.dto.CardDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class CardServiceImpl implements ICardService {

    private CardRepository cardRepository;

    /**
     * Service method to create a new card.
     * If the card already exists, throws a CardAlreadyExistsException.
     * @param mobileNumber the mobile number of the card owner.
     */
    @Override
    public void createCard(String mobileNumber) {

        Optional<Card> card = cardRepository.findByMobileNumber(mobileNumber);

        if (card.isPresent()) {
            throw new CardAlreadyExistsException("Card already exists with mobile number " + mobileNumber);
        }

        cardRepository.save(createNewCard(mobileNumber));
    }

    /**
     * Service method to fetch a card by its mobile number.
     * If the card does not exist, throws a ResourceNotFoundException.
     * @param mobileNumber the mobile number of the card owner.
     * @return the card details.
     */
    @Override
    public CardDTO fetchCard(String mobileNumber) {

        Card card = cardRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );

        return CardMapper.mapToCardDTO(card, new CardDTO());
    }

    /**
     * Service method to update a card.
     * If the card does not exist, throws a ResourceNotFoundException.
     * @param updatedCard the updated card details.
     * @return true if the card is updated successfully, false otherwise.
     */
    @Override
    public boolean updateCard(CardDTO updatedCard) {


        Card card = cardRepository.findByCardNumber(updatedCard.getCardNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Card", "cardNumber", updatedCard.getCardNumber())
        );

        CardMapper.mapToCard(updatedCard, card);
        cardRepository.save(card);
        return true;
    }

    /**
     * Service method to delete a card by its mobile number.
     * If the card does not exist, throws a ResourceNotFoundException.
     * @param mobileNumber the mobile number of the card owner.
     * @return true if the card is deleted successfully, false otherwise.
     */
    @Override
    public boolean deleteCard(String mobileNumber) {

        Card card = cardRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );

        cardRepository.delete(card);

        return true;
    }

    /**
     * Private method to create a new card object, used in the createCard service.
     * @param mobileNumber the mobile number of the card owner.
     * @return the new card object.
     */
    private Card createNewCard(String mobileNumber) {

        Card card = new Card();
        Long randomCardNumber = (1000000000000000L + new Random().nextLong(900000000000000L));
        card.setMobileNumber(mobileNumber);
        card.setCardNumber(randomCardNumber.toString());
        card.setCardType(CardConstants.CREDIT_CARD);
        card.setTotalLimit(CardConstants.NEW_CARD_LIMIT);
        card.setAmountUsed(0);
        card.setAvailableAmount(CardConstants.NEW_CARD_LIMIT);
        return card;

    }
}
