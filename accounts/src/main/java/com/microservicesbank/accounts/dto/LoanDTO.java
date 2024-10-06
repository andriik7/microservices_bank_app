package com.microservicesbank.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Schema(
        name = "Loan",
        description = "Schema to store loan details"
)
@Data
public class LoanDTO {

    @NotEmpty(message = "Mobile number cannot be a null or empty")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
    @Schema(
            description = "Mobile Number of Customer", example = "4365327698"
    )
    private String mobileNumber;

    @NotEmpty(message = "Loan number cannot be null and must be 16 digits")
    @Pattern(regexp = "^\\d{16}$", message = "Loan number must be 16 digits")
    @Schema(
            description = "Loan number of the customer", example = "1234123412341234"
    )
    private String loanNumber;

    @NotEmpty(message = "LoanType cannot be null or empty")
    @Schema(
            description = "Type of the loan", example = "Home Loan"
    )
    private String loanType;

    @Positive(message = "Total loan amount should be greater than 0")
    @Schema(
            description = "Total loan amount", example = "100000"
    )
    private int totalLoan;

    @PositiveOrZero(message = "Total loan amount paid should be equal or greater than 0")
    @Schema(
            description = "Total loan amount paid", example = "1000"
    )
    private int amountPaid;

    @PositiveOrZero(message = "Total outstanding loan amount should be equal or greater than 0")
    @Schema(
            description = "Total outstanding amount against a loan", example = "99000"
    )
    private int outstandingAmount;

}
