package com.microsevicesbank.card.service.impl;


import com.microsevicesbank.card.constants.CardConstants;
import com.microsevicesbank.card.dto.CardDTO;
import com.microsevicesbank.card.exception.CardAlreadyExistsException;
import com.microsevicesbank.card.exception.ResourceNotFoundException;
import com.microsevicesbank.card.mapper.CardMapper;
import com.microsevicesbank.card.model.Card;
import com.microsevicesbank.card.repository.CardRepository;
import com.microsevicesbank.card.service.ICardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class CardServiceImpl implements ICardService {

    private CardRepository cardRepository;

    @Override
    public void createCard(String mobileNumber) {

        Optional<Card> card = cardRepository.findByMobileNumber(mobileNumber);

        if (card.isPresent()) {
            throw new CardAlreadyExistsException("Card already exists with mobile number " + mobileNumber);
        }

        cardRepository.save(createNewCard(mobileNumber));
    }

    @Override
    public CardDTO fetchCard(String mobileNumber) {
        Card card = cardRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );

        return CardMapper.mapToCardDTO(card, new CardDTO());
    }

    @Override
    public boolean updateCard(CardDTO updatedCard) {

        Card card = cardRepository.findByCardNumber(updatedCard.getCardNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Card", "cardNumber", updatedCard.getCardNumber())
        );

        CardMapper.mapToCard(updatedCard, card);
        cardRepository.save(card);

        return true;
    }

    @Override
    public boolean deleteCard(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "cardNumber", cardNumber)
        );

        cardRepository.delete(card);

        return true;
    }


    private Card createNewCard(String mobileNumber) {
        Card card = new Card();
        Long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
        card.setMobileNumber(mobileNumber);
        card.setCardNumber(randomCardNumber.toString());
        card.setCardType(CardConstants.CREDIT_CARD);
        card.setTotalLimit(CardConstants.NEW_CARD_LIMIT);
        card.setAmountUsed(0);
        card.setAvailableAmount(CardConstants.NEW_CARD_LIMIT);
        return card;

    }
}