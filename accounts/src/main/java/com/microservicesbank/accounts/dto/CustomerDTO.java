package com.microservicesbank.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(
        name = "Customer",
        description = "Schema to store Customer and Account details"
)
@Data
public class CustomerDTO {

    @Schema(
            description = "Name of the customer",
            example = "John Doe"
    )
    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 5, max = 30, message = "Name must be between 5 and 30 characters")
    private String name;

    @Schema(
            description = "Email address of the customer",
            example = "ak47.10.07.06@gmail.com"
    )
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email address")
    private String email;

    @Schema(
            description = "Mobile number of the customer",
            example = "0666992283"
    )
    @NotEmpty
    @Pattern(regexp = "^\\d{10}$", message = "Invalid mobile number")
    private String mobileNumber;

    @Schema(
            description = "Account details"
    )
    private AccountDTO accountDto;
}
