package com.microservicesbank.accounts.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class Account extends SuperEntity{

    private Long id;

    @Id
    @Column(name = "account_number", unique = true)
    private Long accountNumber;

    @Column(name = "account_type", nullable = false)
    private String accountType;

    @Column(name = "branch_address", nullable = false)
    private String branchAddress;
}
