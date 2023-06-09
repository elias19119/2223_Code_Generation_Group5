package io.swagger.model.DTOs;


import io.swagger.model.Enums.AccountStatus;
import io.swagger.model.Enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountDTO {
    private AccountType accountType;

    private AccountStatus accountStatus;

    private long transactionLimit = 20;

    private long dayLimit = 2000;

    private long absoluteLimit = 100;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public long getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(long transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public long getDayLimit() {
        return dayLimit;
    }

    public void setDayLimit(long dayLimit) {
        this.dayLimit = dayLimit;
    }

    public long getAbsoluteLimit() {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(long absoluteLimit) {
        this.absoluteLimit = absoluteLimit;
    }
}
