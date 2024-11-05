package com.microservicesbank.message.functions;

import com.microservicesbank.message.dto.AccountsMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;


@Configuration
public class MessageFunctions {

    private static final Logger log = LoggerFactory.getLogger(MessageFunctions.class);

    //Function when I need input and output
    //Supplier when I need only output
    //Consumer when I need only input
    @Bean
    public Function<AccountsMessageDTO, AccountsMessageDTO> email() {

        return accountsMessageDTO -> {
            log.info("Sending email to " + accountsMessageDTO.toString());
            return accountsMessageDTO;
        };
    }

    @Bean
    public Function<AccountsMessageDTO, Long> sms() {

        return accountsMessageDTO -> {
            log.info("Sending SMS to " + accountsMessageDTO.toString());
            return accountsMessageDTO.accountNumber();
        };
    }

}