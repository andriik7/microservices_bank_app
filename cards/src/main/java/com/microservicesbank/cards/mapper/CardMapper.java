package com.microservicesbank.cards.mapper;

import com.microservicesbank.cards.model.Card;
import com.microservicesbank.cards.dto.CardDTO;

public class CardMapper {

    public static CardDTO mapToCardDTO(Card card, CardDTO cardDTO) {

        cardDTO.setMobileNumber(card.getMobileNumber());
        cardDTO.setCardNumber(card.getCardNumber());
        cardDTO.setCardType(card.getCardType());
        cardDTO.setTotalLimit(card.getTotalLimit());
        cardDTO.setAmountUsed(card.getAmountUsed());
        cardDTO.setAvailableAmount(card.getAvailableAmount());

        return cardDTO;
    }

    public static Card mapToCard(CardDTO cardDTO, Card card) {

        card.setMobileNumber(cardDTO.getMobileNumber());
        card.setCardNumber(cardDTO.getCardNumber());
        card.setCardType(cardDTO.getCardType());
        card.setTotalLimit(cardDTO.getTotalLimit());
        card.setAmountUsed(cardDTO.getAmountUsed());
        card.setAvailableAmount(cardDTO.getAvailableAmount());

        return card;
    }

}
