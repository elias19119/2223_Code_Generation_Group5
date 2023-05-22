package io.swagger.model.DTOs;


import io.swagger.model.Enums.AccountType;

import java.util.UUID;

public class CreateAccountDTO {
    private UUID id;
    private AccountType accountType;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
