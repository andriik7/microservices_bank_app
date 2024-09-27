package com.microsevicesbank.card.repository;

import com.microsevicesbank.card.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {


}
