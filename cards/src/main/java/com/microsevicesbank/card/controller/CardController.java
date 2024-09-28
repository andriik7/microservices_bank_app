package com.microsevicesbank.card.controller;

import com.microsevicesbank.card.constants.CardConstants;
import com.microsevicesbank.card.dto.CardDTO;
import com.microsevicesbank.card.dto.ResponseDTO;
import com.microsevicesbank.card.service.ICardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "REST APIs for Cards Microservice", description = "Cards REST APIs to perform CRUD operations on Account Microservice")
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Validated
public class CardController {

    private ICardService cardService;

    @PostMapping("/createCard")
    public ResponseEntity<ResponseDTO> createCard(@RequestParam @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits") String mobileNumber) {

        cardService.createCard(mobileNumber);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDTO(CardConstants.STATUS_201, CardConstants.MESSAGE_201));
    }

    @GetMapping("/fetchCard")
    public ResponseEntity<CardDTO> fetchCardDetails(@RequestParam @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits") String mobileNumber) {

        CardDTO fetchedCard = cardService.fetchCard(mobileNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fetchedCard);
    }

    @PutMapping("/updateCard")
    public ResponseEntity<ResponseDTO> updateCardDetails(@Valid @RequestBody CardDTO cardDTO) {

        boolean isUpdated = cardService.updateCard(cardDTO);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ResponseDTO(CardConstants.STATUS_200, CardConstants.MESSAGE_200));
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                             .body(new ResponseDTO(CardConstants.STATUS_417, CardConstants.MESSAGE_417_UPDATE));
    }

    @DeleteMapping("/deleteCard")
    public ResponseEntity<ResponseDTO> deleteCardDetails(@RequestParam @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits") String mobileNumber) {

        boolean isDeleted = cardService.deleteCard(mobileNumber);

        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ResponseDTO(CardConstants.STATUS_200, CardConstants.MESSAGE_200));
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                             .body(new ResponseDTO(CardConstants.STATUS_417, CardConstants.MESSAGE_417_DELETE));
    }
}
