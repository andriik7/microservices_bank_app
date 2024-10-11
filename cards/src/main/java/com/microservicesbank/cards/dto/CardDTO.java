package com.microservicesbank.cards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Schema(
        name = "Card",
        description = "Schema to store card details"
)
@Data
public class CardDTO {

    @Schema(
            description = "Mobile number of customer",
            example = "0666992283"
    )
    @NotEmpty(message = "Mobile number cannot be empty")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Schema(
            description = "Card number of customer",
            example = "1234123412341234"
    )
    @Pattern(regexp = "^\\d{16}$", message = "Card number must be 16 digits")
    @NotEmpty(message = "Card number cannot be empty")
    private String cardNumber;

    @Schema(
            description = "Type of card",
            example = "Debit Card"
    )
    @NotEmpty(message = "Card type cannot be empty")
    private String cardType;

    @Schema(
            description = "Total limit of card",
            example = "1000"
    )
    @Positive(message = "Total limit must be greater than 0")
    private int totalLimit;

    @Schema(
            description = "Amount used of card",
            example = "100"
    )
    @PositiveOrZero(message = "Amount used must be greater than 0 or equal to 0")
    private int amountUsed;

    @Schema(
            description = "Available amount of card",
            example = "900"
    )
    @PositiveOrZero(message = "Available amount must be greater than 0 or equal to 0")
    private int availableAmount;
}
