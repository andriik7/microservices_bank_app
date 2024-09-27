package com.microsevicesbank.loan.repository;

import com.microsevicesbank.card.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Card, Long> {


}
