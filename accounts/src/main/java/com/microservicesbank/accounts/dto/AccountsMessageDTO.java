package com.microservicesbank.accounts.dto;

public record AccountsMessageDTO(Long accountNumber, String name, String email, String mobileNumber) {
}
