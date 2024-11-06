package com.microservicesbank.accounts.functions;

import com.microservicesbank.accounts.service.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AccountsFunctions {

    private static final Logger log = LoggerFactory.getLogger(AccountsFunctions.class);

    @Bean
    public Consumer<Long> updateCreatedAccount(IAccountService accountService) {

        return accountNumber -> {
          log.info("Updating communication status for account number: {}", accountNumber);
          accountService.updateCommunicationStatus(accountNumber);
        };
    }

    @Bean
    public Consumer<Long> logDeletedAccount(IAccountService accountService) {

        return accountNumber -> {
            log.info("Account was successfully deleted with account number: {}", accountNumber);
        };
    }
}
