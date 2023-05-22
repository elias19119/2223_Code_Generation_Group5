package io.swagger.model.DTOs;

import java.util.UUID;

public class DepositToCheckingAccountDTO {

    private String IBAN;
    private Long depositAmount;
    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public Long getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(Long depositAmount) {
        this.depositAmount = depositAmount;
    }
}
