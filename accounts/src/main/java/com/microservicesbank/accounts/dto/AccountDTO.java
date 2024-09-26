package com.microservicesbank.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(
        name = "Account",
        description = "Schema to store account details"
)
@Data
public class AccountDTO {

    @Schema(
            description = "Account number of bank account",
            example = "1234567890"
    )
    @NotEmpty(message = "Account number cannot be empty")
    @Pattern(regexp = "^\\d{10}$", message = "Account number must be 10 digits")
    private Long accountNumber;

    @Schema(
            description = "Account type of bank account",
            example = "Savings"
    )
    @NotEmpty(message = "Account type cannot be empty")
    private String accountType;

    @Schema(
            description = "Branch address of bank account",
            example = "123 Main Street, New York"
    )
    @NotEmpty(message = "Branch address cannot be empty")
    private String branchAddress;
}
