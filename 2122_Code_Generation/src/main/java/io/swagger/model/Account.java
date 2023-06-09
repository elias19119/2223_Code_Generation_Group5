package io.swagger.model;


import io.swagger.model.Enums.AccountStatus;
import io.swagger.model.Enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String IBANNo;

    private AccountType accountType;

    private long balance;

    private LocalDate dateOfOpening;

    private AccountStatus accountStatus;

    private long transactionLimit = 20;

    private long dayLimit = 2000;

    private long absoluteLimit = 100;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIBANNo() {
        return IBANNo;
    }

    public void setIBANNo(String IBANNo) {
        this.IBANNo = IBANNo;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public LocalDate getDateOfOpening() {
        return dateOfOpening;
    }

    public void setDateOfOpening(LocalDate dateOfOpening) {
        this.dateOfOpening = dateOfOpening;
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
