package com.microservicesbank.cards.controller;

import com.microservicesbank.cards.constants.CardConstants;
import com.microservicesbank.cards.dto.ErrorResponseDTO;
import com.microservicesbank.cards.dto.ResponseDTO;
import com.microservicesbank.cards.service.ICardService;
import com.microservicesbank.cards.dto.CardDTO;
import com.microservicesbank.cards.dto.CardsContactInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "REST APIs for Cards Microservice",
        description = "Cards REST APIs to perform CRUD operations on Account Microservice")
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Validated
public class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    private ICardService cardService;

    private CardsContactInfoDTO cardsContactInfoDTO;

    @Operation(
            summary = "Create new card REST API",
            description = "Creates a new card based on provided 10-digit mobile number"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "HTTP Status 201 CREATED"
        ),
            @ApiResponse(
                    responseCode = "400",
                    description = "HTTP Status 400 BAD REQUEST",
                    content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("/createCard")
    public ResponseEntity<ResponseDTO> createCard(@RequestHeader("microbank-correlation-id") String correlationId,
                                                  @RequestParam @Pattern(regexp = "^\\d{10}$",
                                                          message = "Mobile number must be 10 digits") String mobileNumber) {

        logger.debug("createCard() method started");
        cardService.createCard(mobileNumber);
        logger.debug("createCard() method ended");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDTO(CardConstants.STATUS_201, CardConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch card REST API",
            description = "Fetches card details based on provided 10-digit mobile number"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "HTTP Status 200 OK"
        ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status 404 NOT FOUND",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("/fetchCard")
    public ResponseEntity<CardDTO> fetchCardDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                    @RequestParam @Pattern(regexp = "^\\d{10}$",
                                                            message = "Mobile number must be 10 digits") String mobileNumber) {

        logger.debug("fetchCardDetails() method started");
        CardDTO fetchedCard = cardService.fetchCard(mobileNumber);
        logger.debug("fetchCardDetails() method ended");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fetchedCard);
    }

    @Operation(
            summary = "Update card REST API",
            description = "Updates card details based on provided card details with appropriate card number"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "HTTP Status 200 OK"
        ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status 404 NOT FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "HTTP Status 417 CONFLICT",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping("/updateCard")
    public ResponseEntity<ResponseDTO> updateCardDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                         @Valid @RequestBody CardDTO cardDTO) {

        logger.debug("updateCardDetails() method started");
        boolean isUpdated = cardService.updateCard(cardDTO);
        logger.debug("updateCardDetails() method ended");
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ResponseDTO(CardConstants.STATUS_200, CardConstants.MESSAGE_200));
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                             .body(new ResponseDTO(CardConstants.STATUS_417, CardConstants.MESSAGE_417_UPDATE));
    }

    @Operation(
            summary = "Delete card REST API",
            description = "Deletes card details based on provided 10-digit mobile number"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "HTTP Status 200 OK"
        ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status 404 NOT FOUND",
                    content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "HTTP Status 417 CONFLICT",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status 500 INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @DeleteMapping("/deleteCard")
    public ResponseEntity<ResponseDTO> deleteCardDetails(@RequestHeader("microbank-correlation-id") String correlationId,
                                                         @RequestParam @Pattern(regexp = "^\\d{10}$",
                                                                 message = "Mobile number must be 10 digits") String mobileNumber) {

        logger.debug("deleteCardDetails() method started");
        boolean isDeleted = cardService.deleteCard(mobileNumber);
        logger.debug("deleteCardDetails() method ended");
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ResponseDTO(CardConstants.STATUS_200, CardConstants.MESSAGE_200));
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                             .body(new ResponseDTO(CardConstants.STATUS_417, CardConstants.MESSAGE_417_DELETE));
    }

    @GetMapping("/contactDetails")
    public ResponseEntity<CardsContactInfoDTO> getContactInfo(@RequestHeader("microbank-correlation-id") String correlationId) {

        logger.debug("getContactInfo() method started");
        logger.debug("getContactInfo() method ended");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardsContactInfoDTO);
    }
}
