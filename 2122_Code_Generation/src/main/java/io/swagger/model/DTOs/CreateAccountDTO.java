package io.swagger.model.DTOs;


import io.swagger.model.Enums.AccountType;

import java.util.UUID;

public class CreateAccountDTO {
    private UUID userId;
    private AccountType accountType;

    public CreateAccountDTO(UUID userId, AccountType accountType) {
        this.userId = userId;
        this.accountType = accountType;
    }

    public CreateAccountDTO() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
