package com.microservicesbank.cards.service;

import com.microservicesbank.cards.dto.CardDTO;

public interface ICardService {

    void createCard(String mobileNumber);

    CardDTO fetchCard(String mobileNumber);

    boolean updateCard(CardDTO updatedCard);

    boolean deleteCard(String mobileNumber);
}
