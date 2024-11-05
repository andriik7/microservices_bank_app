package com.microservicesbank.message.dto;

/**
 *
 * @param accountNumber
 * @param name
 * @param email
 * @param mobileNumber
 */

public record AccountsMessageDTO(Long accountNumber, String name, String email, String mobileNumber) {



}
