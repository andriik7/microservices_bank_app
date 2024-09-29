package com.microsevicesbank.loan;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@OpenAPIDefinition(info = @Info(title = "Loans microservice REST API documentation", version = "1.0",
        description = "Documentation Card REST API",
        contact = @Contact(name = "Andrii Kuchera", email = "ak47.10.07.06@gmail.com", url = "https://github.com/andriik7"),
        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")),
        externalDocs = @ExternalDocumentation(description = "Bank Loans microservice REST API documentation", url = "http://localhost:9000/swagger-ui/index.html"))
public class LoansApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoansApplication.class, args);
    }

}
